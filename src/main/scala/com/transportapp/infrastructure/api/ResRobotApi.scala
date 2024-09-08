package com.transportapp.infrastructure.api
import sttp.client4.fetch.FetchBackend
import cats.effect.IO
import com.transportapp.domain.models.{Station, Departure}
import com.transportapp.domain.models.resRobotModels.*
import sttp.client4.*
import sttp.client4.circe.*
import cats.implicits.*
import io.circe.generic.auto.*
import scala.util.{Try, Success, Failure}
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

class ResRobotApi extends TransportApi:
  private val backend = FetchBackend()
  private val stationCoordinates = List(
    (59.329444, 18.068611),
    (59.612565, 17.865964)
  )
  private val baseUrl = "https://api.resrobot.se/v2.1"
  private val accessId = Option(System.getenv("RES_ROBOT_TOKEN")).getOrElse {
    "api_key" // You might want to handle this case differently
  }
  override def loadStations(): IO[Either[String, List[Station]]] = {
    val paramMap = Map(
      "format"   -> "json",
      "accessId" -> accessId,
      "lang"     -> "en",
      "r"        -> "10000",
      "maxNo"    -> "1000"
    )

    val requests: List[
      Request[Either[ResponseException[String, Exception], RRStationResponse]]
    ] =
      stationCoordinates.map { case (lat, lon) =>
        val fullParamMap = paramMap ++ Map(
          "originCoordLat"  -> lat.toString,
          "originCoordLong" -> lon.toString
        )
        basicRequest
          .get(uri"$baseUrl/location.nearbystops?$fullParamMap")
          .response(asJson[RRStationResponse])
      }

    requests.traverse(request => IO.fromFuture(IO(backend.send(request)))).map {
      responses =>
        responses.traverse(_.body.left.map(_.getMessage)).map {
          stationResponses =>
            stationResponses.flatMap { response =>
              response.stopLocationOrCoordLocation
                .map(_.StopLocation)
                .map(convertToStation)
            }.distinct
        }
    }
  }

  override def loadDepartures(
      stationId: String
  ): IO[Option[List[Departure]]] = {
    val paramMap = Map(
      "id"        -> stationId,
      "format"    -> "json",
      "accessId"  -> accessId,
      "lang"      -> "en",
      "operators" -> "!275",
      "duration"  -> "120"
    )

    val request: Request[
      Either[ResponseException[String, Exception], RRDepartureResponse]
    ] =
      basicRequest
        .get(uri"$baseUrl/departureBoard?$paramMap")
        .response(asJson[RRDepartureResponse])

    println(s"Request: $request")

    IO.fromFuture(IO(backend.send(request))).map { response =>
      response.body match {
        case Right(departureResponse: RRDepartureResponse) =>
          val departures = departureResponse.Departure
            .map { rrDeparture =>
              val departure = convertToDeparture(rrDeparture)
              departure
            }
            .toList
            .sortBy(_.scheduledTime)
          if (departures.nonEmpty) Some(departures)
          else {
            None
          }
        case Left(error) => None
      }
    }
  }

  private def convertToStation(station: RRStation): Station =
    Station(name = station.name, id = station.extId)

  private def convertToDeparture(departure: RRDeparture): Departure =
    Try {
      val stockholmZone = ZoneId.of("UTC+2")
      val formatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
      val now           = ZonedDateTime.now(stockholmZone)
      val departureDateTime =
        LocalDateTime.parse(s"${departure.date}T${departure.time}", formatter)
      val departureLDT = ZonedDateTime.of(departureDateTime, stockholmZone)

      // Calculate waiting time
      val waitingTimeMinutes = ChronoUnit.MINUTES.between(now, departureLDT)
      val waitingTime = if (waitingTimeMinutes < 0) {
        "Departed"
      } else if ((waitingTimeMinutes / 60) >= 1) {
        f"${departureDateTime.getHour}%02d:${departureDateTime.getMinute}%02d"
      } else {
        s"${waitingTimeMinutes} min"
      }

      Departure(
        line = departure.ProductAtStop.line,
        destination = departure.direction.replaceFirst("""\s*\([^)]*\)""", ""),
        transportType = convertRRTransportType(departure.ProductAtStop.catOut),
        scheduledTime = departureDateTime,
        expectedTime = departureDateTime,
        waitingTime = waitingTime,
        operator = departure.ProductAtStop.operator
      )
    } match {
      case Success(dep) => dep
      case Failure(exception) =>
        println(s"Error in convertToDeparture: ${exception.getMessage}")
        exception.printStackTrace()
        throw exception
    }

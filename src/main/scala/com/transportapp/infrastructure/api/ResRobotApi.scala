package com.transportapp.infrastructure.api
import sttp.client4.fetch.FetchBackend
import cats.effect.IO
import com.transportapp.domain.models.{Station, Departure}
import com.transportapp.domain.models.resRobotModels.*
import sttp.client4.*
import sttp.client4.circe.*
import cats.implicits.*
import io.circe.generic.auto.*

import java.time.LocalDateTime, java.time.Duration
import java.time.format.DateTimeFormatter

class ResRobotApi extends TransportApi:
  private val backend = FetchBackend()
  private val stationCoordinates = List(
    (59.329444, 18.068611),
    (59.612565, 17.865964)
  )
  private val baseUrl  = "https://api.resrobot.se/v2.1"
  private val accessId = "31fc7aac-2151-4992-8857-db30d1927d9c"
  private val dateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

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

  override def loadDepartures(stationId: String): IO[Option[List[Departure]]] =
    val paramMap = Map(
      "id"       -> stationId,
      "format"   -> "json",
      "accessId" -> accessId
    )

    val request: Request[
      Either[ResponseException[String, Exception], RRDepartureResponse]
    ] =
      basicRequest
        .get(uri"$baseUrl/departureBoard?$paramMap")
        .response(asJson[RRDepartureResponse])

    IO.fromFuture(IO(backend.send(request))).map { response =>
      response.body match {
        case Right(departureResponse: RRDepartureResponse) =>
          departureResponse.Departure.map(convertToDeparture).toList match {
            case departures if departures.nonEmpty => Some(departures)
            case _ => println("No departures found"); None
          }
        case Left(error) =>
          println(s"Error loading departures: ${error.getMessage}")
          None
      }
    }

  private def convertToStation(station: RRStation): Station =
    Station(name = station.name, id = station.extId)

  private def convertToDeparture(departure: RRDeparture): Departure =
    Departure(
      line = departure.ProductAtStop.line,
      destination = departure.direction,
      transportType = convertRRTransportType(departure.ProductAtStop.catOut),
      scheduledTime = LocalDateTime.parse(departure.time, dateTimeFormatter),
      expectedTime = LocalDateTime.parse(departure.time, dateTimeFormatter),
      waitingTime =
        s"${Duration.between(LocalDateTime.now(), LocalDateTime.parse(departure.time, dateTimeFormatter))} min"
    )

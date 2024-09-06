package com.transportapp.infrastructure.api
import com.transportapp.domain.models.{Station, Departure, TransportType}
import com.transportapp.domain.models.slmodels.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import cats.effect.IO
import sttp.client4.fetch.FetchBackend
import sttp.client4.*
import sttp.client4.circe.*
import io.circe.generic.auto.*

import scala.concurrent.ExecutionContext.Implicits.global

class SLApi extends TransportApi:
  private val backend = FetchBackend()
  val stopsUrl        = "https://nicoferra.tplinkdns.com:61001/api/sl-stops"
  private val baseUrl = "https://transport.integration.sl.se/v1"
  private val dateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

  def convertToStation(station: SLStation): Station =
    Station(name = station.name, id = station.id.toString)

  def getDeparturesUrl(siteId: String, filter: TransportType): String =
    val transportType: String = filter match
      case TransportType.Bus   => "BUS"
      case TransportType.Train => "TRAIN"
      case TransportType.Metro => "METRO"
      case TransportType.Tram  => "TRAM"
      case TransportType.Ship  => "SHIP"
      case TransportType.Ferry => "FERRY"
      case TransportType.Taxi  => "TAXI"
      case TransportType.All   => ""
    val url = s"$baseUrl/sites/$siteId/departures"
    if (transportType.isEmpty) url else s"$url?transport=$transportType"

  def loadStations(): IO[Either[String, List[Station]]] = IO.fromFuture {
    IO {
      val request = basicRequest
        .get(uri"$stopsUrl")
        .response(asJson[List[SLStation]])
      request.send(backend).map { response =>
        // with the response, build theS list of stations modifying the values
        response.body match {
          case Right(stations) =>
            Right(stations.map { station =>
              convertToStation(station)
            })
          case Left(error) => Left(error.getMessage)
        }
      }
    }
  }

  override def loadDepartures(stationId: String): IO[Option[List[Departure]]] =
    loadDepartures(stationId, TransportType.All)

  def loadDepartures(
      stationId: String,
      filter: TransportType
  ): IO[Option[List[Departure]]] = IO.fromFuture {
    IO {
      val request = basicRequest
        .get(uri"${getDeparturesUrl(stationId, filter)}")
        .response(asJson[SLDepartureResponse])

      request.send(backend).map { response =>
        response.body match {
          case Right(departureResponse) =>
            departureResponse.departures
              .map(
                convertToDeparture
              )
              .toList match {
              case departures if departures.nonEmpty => Some(departures)
              case _ =>
                println(s"Empty Departure List")
                None
            }
          case Left(error) =>
            println(s"Error loading departures: ${error.getMessage}")
            None
        }
      }
    }
  }

  private def convertToDeparture(slDeparture: SLDeparture): Departure =
    Departure(
      line = slDeparture.line.designation,
      destination = slDeparture.destination,
      transportType = convertTransportType(slDeparture.line.transport_mode),
      scheduledTime =
        LocalDateTime.parse(slDeparture.scheduled, dateTimeFormatter),
      expectedTime =
        LocalDateTime.parse(slDeparture.expected, dateTimeFormatter),
      waitingTime = slDeparture.display
    )

  private def convertTransportType(mode: String): TransportType =
    mode.toUpperCase match
      case "BUS"   => TransportType.Bus
      case "TRAIN" => TransportType.Train
      case "METRO" => TransportType.Metro
      case "TRAM"  => TransportType.Tram
      case "FERRY" => TransportType.Ferry
      case "SHIP"  => TransportType.Ship
      case "TAXI"  => TransportType.Taxi
      case _ =>
        throw new IllegalArgumentException(s"Unknown transport mode: $mode")

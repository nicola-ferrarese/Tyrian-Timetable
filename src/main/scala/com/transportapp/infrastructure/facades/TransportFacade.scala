package com.transportapp.infrastructure.facades
import com.transportapp.domain.models.{Departure, Station, TransportType}
import cats.effect.IO
import com.transportapp.infrastructure.api.{ResRobotApi, SLApi}
import cats.implicits.*
import scala.concurrent.duration.*

class TransportFacade() {
  val SLApi                       = new SLApi()
  val RRApi                       = new ResRobotApi()

  def loadStations(): IO[Either[String, List[Station]]] =
    (loadSLStations, loadResRobotStations).parMapN { (slResult, rrResult) =>
      for {
        slStations <- slResult
        rrStations <- rrResult
      } yield slStations ++ rrStations
    }

  def getDepartures(
      stationId: String,
      filter: TransportType
  ): IO[Option[List[Departure]]] =
    (
      getSLDepartures(stationId, filter),
      getResRobotDepartures(stationId, filter)
    ).parMapN {
      case (Some(slDepartures), Some(rrDepartures)) =>
        Some(slDepartures ++ rrDepartures)
      case (Some(slDepartures), None) => Some(slDepartures)
      case (None, Some(rrDepartures)) => Some(rrDepartures)
      case _                          => None
    }

  def loadSLStations: IO[Either[String, List[Station]]] =
    def retryLoadStations(
        retriesLeft: Int
    ): IO[Either[String, List[Station]]] = {
      println(s"Attempting to load bus stations. Retries left: $retriesLeft")
      SLApi.loadStations().flatMap {
        case Right(stations) =>
          IO.pure(Right(stations))
        case _ if retriesLeft > 0 =>
          IO.sleep(1.second) >> retryLoadStations(retriesLeft - 1)
        case Left(error) =>
          IO.pure(
            Left(
              s"Failed to load stations after 3 attempts. Last error: $error"
            )
          )
      }
    }
    retryLoadStations(3)


  def getSLDepartures(
      stationId: String,
      filter: TransportType
  ): IO[Option[List[Departure]]] =
    SLApi.loadDepartures(stationId, filter)

  def getResRobotDepartures(
      stationId: String,
      filter: TransportType
  ): IO[Option[List[Departure]]] =
    RRApi.loadDepartures(stationId)

  def loadResRobotStations: IO[Either[String, List[Station]]] =
    println("Loading ResRobot stations")
    RRApi.loadStations().flatMap {
      case Right(stations) =>
        IO.pure(Right(stations))
      case Left(error) =>
        IO.pure(
          Left(
            s"Failed to load stations, Error: $error"
          )
        )
    }
}

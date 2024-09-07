package com.transportapp.infrastructure.facades
import com.transportapp.domain.models.{Departure, Station, TransportType}
import cats.effect.IO
import com.transportapp.infrastructure.api.*

import scala.concurrent.duration.*

class TransportFacade(SLApi: SLApi) {
  private val Mode: TransportType = TransportType.Bus
  val RRApi                       = new ResRobotApi()
  def loadSLStations: IO[Either[String, List[Station]]] =
    if (Mode == TransportType.Bus) {
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
    } else {
      println("Mode is not Bus")
      IO.pure(Left("Mode is not Bus"))
    }

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

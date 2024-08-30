package com.transportapp.infrastructure.api
import cats.effect.IO
import com.transportapp.domain.models.{Departure, Station}

trait TransportApi {
  def loadStations(): IO[Either[String, List[Station]]]
  def loadDepartures(stationId: String): IO[Either[String, List[Departure]]]
}

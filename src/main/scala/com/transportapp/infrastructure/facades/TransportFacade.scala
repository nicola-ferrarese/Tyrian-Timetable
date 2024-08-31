package com.transportapp.infrastructure.facades
import com.transportapp.domain.models.{Departure, Station, TransportType}
import cats.effect.IO
import com.transportapp.infrastructure.api.SLApi
class TransportFacade(SLApi: SLApi) {
  private val Mode:  TransportType = TransportType.Bus
  
  def loadSLStations: IO[Either[String, List[Station]]] = 
    if (Mode == TransportType.Bus) 
      println("Loading bus stations")
      SLApi.loadStations()
    else 
      IO.pure(Left("Mode is not Bus"))
  
  def getSLDepartures(stationId: String): IO[Either[String, List[Departure]]] =
      SLApi.loadDepartures(stationId)
} 

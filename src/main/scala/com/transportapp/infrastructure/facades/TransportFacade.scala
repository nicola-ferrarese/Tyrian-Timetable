package com.transportapp.infrastructure.facades
import com.transportapp.domain.models.Station
import cats.effect.IO
import com.transportapp.domain.models.TransportType
import com.transportapp.infrastructure.api.SLApi
class TransportFacade {
  private val Mode:  TransportType = TransportType.Bus
  val stations: IO[Either[String, List[Station]]] = if (Mode == TransportType.Bus) {
    println("Loading bus stations")
    val api = SLApi()
    //wait for the api to load the stations, then return the result, it's a future
    api.loadStations()

  }
  else {
    IO.pure(Left("Mode is not Bus"))
  }

}

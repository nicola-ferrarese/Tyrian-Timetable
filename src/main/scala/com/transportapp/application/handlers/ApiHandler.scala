package com.transportapp.application.handlers


import com.transportapp.application.commands.ApiCommand
import com.transportapp.domain.events.ApiEvent
import com.transportapp.infrastructure.facades.TransportFacade
import cats.effect.IO

class ApiHandler(transportFacade: TransportFacade) {
  def handle(command: ApiCommand): IO[ApiEvent] = command match {
    case ApiCommand.LoadStations =>
      transportFacade.loadStations().map(ApiEvent.StationsLoaded(_))
    case ApiCommand.GetDepartures(stationId, filter) =>
      transportFacade.getDepartures(stationId, filter).map {
        case Some(departures) => ApiEvent.DeparturesLoaded(departures)
        case None =>
          println(s"Error loading departures for station $stationId");
          ApiEvent.DeparturesLoaded(List.empty)
      }
  }
}

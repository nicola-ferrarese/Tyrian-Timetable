package com.transportapp.application.handlers

import cats.effect.IO
import com.transportapp.infrastructure.facades.TransportFacade
import com.transportapp.application.commands.SLCommand
import com.transportapp.domain.events.SLEvent

class SLHandler(transportFacade: TransportFacade){
  def handle(command: SLCommand): IO[SLEvent] = command match {
    case SLCommand.LoadStations =>
      transportFacade.loadSLStations.map(SLEvent.StationsLoaded(_))
    case SLCommand.GetDepartures(stationId) =>
      transportFacade.getSLDepartures(stationId).map {
        case Some(departures) => SLEvent.DeparturesLoaded(departures)
        case None => SLEvent.DeparturesLoaded(List.empty)
      }
  }
}
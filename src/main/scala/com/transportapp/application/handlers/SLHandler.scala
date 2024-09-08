package com.transportapp.application.handlers

import cats.effect.IO
import com.transportapp.infrastructure.facades.TransportFacade
import com.transportapp.application.commands.{
  Command,
  ResRobotCommand,
  SLCommand
}
import com.transportapp.domain.events.SLEvent

class SLHandler(transportFacade: TransportFacade) {
  def handle(command: Command): IO[SLEvent] = command match {
    case SLCommand.LoadStations =>
      transportFacade.loadSLStations.map(SLEvent.StationsLoaded(_))
    case SLCommand.GetDepartures(stationId, filter) =>
      transportFacade.getSLDepartures(stationId, filter).map {
        case Some(departures) => SLEvent.DeparturesLoaded(departures)
        case None             => SLEvent.DeparturesLoaded(List.empty)
      }
    case ResRobotCommand.LoadStations =>
      transportFacade.loadResRobotStations.map(SLEvent.StationsLoaded(_))
    case ResRobotCommand.GetDepartures(stationId, filter) =>
      transportFacade.getResRobotDepartures(stationId, filter).map {
        case Some(departures) => SLEvent.DeparturesLoaded(departures)
        case None             => SLEvent.DeparturesLoaded(List.empty)
      }
  }
}

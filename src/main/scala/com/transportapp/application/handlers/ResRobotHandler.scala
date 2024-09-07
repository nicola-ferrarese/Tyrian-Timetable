package com.transportapp.application.handlers

import cats.effect.IO
import com.transportapp.application.commands.ResRobotCommand
import com.transportapp.domain.events.SLEvent
import com.transportapp.infrastructure.facades.TransportFacade

class ResRobotHandler(transportFacade: TransportFacade) {
  def handle(command: ResRobotCommand): IO[SLEvent] = command match {
    case ResRobotCommand.LoadStations =>
      transportFacade.loadResRobotStations.map(SLEvent.StationsLoaded(_))
    case ResRobotCommand.GetDepartures(stationId, filter) =>
      transportFacade.getResRobotDepartures(stationId, filter).map {
        case Some(departures) => SLEvent.DeparturesLoaded(departures)
        case None             => SLEvent.DeparturesLoaded(List.empty)
      }
  }
}

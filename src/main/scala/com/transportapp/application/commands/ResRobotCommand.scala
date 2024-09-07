package com.transportapp.application.commands
import com.transportapp.domain.models.TransportType

enum ResRobotCommand extends Command:
  case LoadStations
  case GetDepartures(stationId: String, filter: TransportType)

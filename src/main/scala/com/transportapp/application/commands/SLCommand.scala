package com.transportapp.application.commands
import com.transportapp.domain.models.TransportType

enum SLCommand extends Command:
  case LoadStations
  case GetDepartures(stationId: String, filter: TransportType)

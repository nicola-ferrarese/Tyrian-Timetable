package com.transportapp.application.commands
import com.transportapp.domain.models.TransportType
 
trait Command

enum ApiCommand extends Command {
  case LoadStations
  case GetDepartures(stationId: String, filter: TransportType) 
}


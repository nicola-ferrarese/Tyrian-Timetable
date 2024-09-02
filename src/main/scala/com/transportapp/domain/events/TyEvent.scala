package com.transportapp.domain.events

import com.transportapp.domain.models.Station
import com.transportapp.domain.models.TransportType

enum TyEvent extends AppEvent:
  case stationSelected(station: Station)
  case inputUpdated(input: String)
  case TransportFilterUpdated(filter: TransportType)
  case getStationName(stationId: String)
  case UpdateDepartures
  case NoOp

package com.transportapp.domain.events

import com.transportapp.domain.models.{Station, Departure}

enum SLEvent extends AppEvent:
  case StationsLoaded(stations: Either[String, List[Station]])
  case DeparturesLoaded(departures:  List[Departure])
  case NoOp

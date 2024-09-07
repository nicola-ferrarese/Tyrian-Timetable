package com.transportapp.domain.events

import com.transportapp.domain.models.{Departure, Station}

enum ResRobotEvent extends AppEvent:
  case StationsLoaded(stations: Either[String, List[Station]])
  case DeparturesLoaded(departures: List[Departure])
  case NoOp

package com.transportapp.domain.events
import com.transportapp.domain.models.{Departure, Station}

trait AppEvent

enum ApiEvent extends AppEvent {
  case StationsLoaded(stations: Either[String, List[Station]])
  case DeparturesLoaded(departures: List[Departure])
}

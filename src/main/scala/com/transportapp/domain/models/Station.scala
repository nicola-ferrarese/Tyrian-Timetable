package com.transportapp.domain.models

enum TransportType:
  case Bus, Airplane
  
case class Station(id: String, name: String, transportType: TransportType):
  require(id.nonEmpty, "Station id cannot be empty")
  require(name.nonEmpty, "Station name cannot be empty")

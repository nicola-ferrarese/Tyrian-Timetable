package com.transportapp.domain.models

enum TransportType:
  case All, Bus, Train, Ferry, Tram, Metro, Taxi, Ship

case class Station(id: String, name: String):
  require(name.nonEmpty, "Station name cannot be empty")
  require(id.nonEmpty, "Station id cannot be empty")

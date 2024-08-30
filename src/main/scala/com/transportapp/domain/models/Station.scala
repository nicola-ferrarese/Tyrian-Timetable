package com.transportapp.domain.models

enum TransportType:
  case Bus, Airplane, Train, Ferry, Tram, Metro
  
case class Station(id: String, name: String):
  require(name.nonEmpty, "Station name cannot be empty")
  require(id.nonEmpty, "Station id cannot be empty")

case class SLStation(id: Int, name: String, lat: Option[Double], lon: Option[Double]):
  require(name.nonEmpty, "Station name cannot be empty")
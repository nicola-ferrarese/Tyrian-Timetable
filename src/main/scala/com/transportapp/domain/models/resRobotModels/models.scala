package com.transportapp.domain.models.resRobotModels

import com.transportapp.domain.models.TransportType

case class RRStationContainer(StopLocation: RRStation)

case class RRStation(
    extId: String,
    name: String,
    lat: Option[Double],
    lon: Option[Double]
):
  require(name.nonEmpty, "Station name cannot be empty")

case class RRDeparture(
    direction: String,
    time: String,
    ProductAtStop: RRProduct
)

case class RRProduct(
    line: String,
    displayNumber: String,
    catOut: String
)

enum RRTransportType:
  case BLT, BRE, BXB, BAX, BBL, BRB, FLT, JAX, JLT, JRE, JIC, JST, JEX, JBL,
    JEN, JNT, SLT, TLT, ULT

def convertRRTransportType(mode: String): TransportType =
  mode.toUpperCase match {
    case "BLT" | "BRE" | "BXB" | "BAX" | "BBL" | "BRB" => TransportType.Bus
    case "FLT"                                         => TransportType.Ferry
    case "JAX" | "JLT" | "JRE" | "JIC" | "JST" | "JEX" | "JBL" | "JEN" |
        "JNT" =>
      TransportType.Train
    case "SLT" => TransportType.Tram
    case "TLT" => TransportType.Taxi
    case "ULT" => TransportType.Metro
  }

case class RRDepartureResponse(Departure: List[RRDeparture])
case class RRStationResponse(stopLocationOrCoordLocation: List[RRStationContainer])
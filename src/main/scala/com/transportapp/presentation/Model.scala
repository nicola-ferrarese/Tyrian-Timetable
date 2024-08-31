package com.transportapp.presentation

import com.transportapp.domain.models.*
case class Model(
    slStations : Either[String, List[Station]],
    slDepartures: Option[List[Departure]],
    isTestMode: Boolean = false,
    selectedStation: Station,
    output: String = "",
    slFilteredStations:  Option[List[Station]] = None
)


object Model:
  val initial: Model = Model(
    slStations = Left("No data"),
    slDepartures = None,
    isTestMode = false,
    selectedStation = Station("0", "No station selected"),
    output = ""
  )
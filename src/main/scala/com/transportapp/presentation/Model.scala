package com.transportapp.presentation

import com.transportapp.domain.models.{Departure, Station}
import com.transportapp.domain.models.TransportType



case class Model(
      slStations : Either[String, List[Station]],
      slDepartures: Option[List[Departure]],
      isTestMode: Boolean = false,
      selectedStation: Station,
      output: String = "",
      searchVisible: Boolean = false,
      slFilteredStations:  Option[List[Station]] = None,
      slTransportTypeFilter: TransportType = TransportType.All,
      subdomain: String = "",
                )

object Model:
  val initial: Model = Model(
    slStations = Left("No data"),
    slDepartures = None,
    selectedStation = Station("0", "No station selected"),
  )
  extension (model: Model)
    def updateStation(station: Station): Model =
      val CheckedStation = getStation(station.id)
      model.copy(selectedStation = CheckedStation, searchVisible = false)


    def updateStations(stations: Either[String, List[Station]]): Model =
      model.copy(slStations = stations)

    def updateDepartures(departures: List[Departure]): Model =
      model.copy(slDepartures = Some(departures))

    def updateOutput(output: String): Model =
      model.copy(output = output)

    def toggleSearchVisible: Model =
      model.copy(searchVisible = !model.searchVisible)

    def toggleAppMode: Model =
      model.copy(isTestMode = !model.isTestMode)

    def updateFilteredStations(stations: List[Station]): Model =
      model.copy(slFilteredStations = Some(stations))

    private def filterStations(term: String): List[Station] =
      model.slStations match
        case Right(stations) =>
          stations.filter(_.name.toLowerCase.contains(term.toLowerCase))
        case _ => List.empty

    def updateFilteredStations(term: String): Model =
      model.copy(slFilteredStations = Some(filterStations(term).take(6)), searchVisible=term.nonEmpty)

    def updateTransportTypeFilter(filter: TransportType): Model =
        model.copy(slTransportTypeFilter = filter)

    private def getStation(stationId: String): Station =
      model.slStations match
        case Right(stations) =>
          stations.find(_.id == stationId) match
            case station =>
              station.getOrElse(Station("0", "No station found"))              
        case Left(error) =>
          Station(stationId, "Loading..")
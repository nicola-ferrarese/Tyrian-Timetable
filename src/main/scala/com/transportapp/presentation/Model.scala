package com.transportapp.presentation

import com.transportapp.domain.models.{Departure, Station}
import com.transportapp.domain.models.TransportType

case class Model(
    Stations: Either[String, List[Station]],
    Departures: Option[List[Departure]],
    isTestMode: Boolean = false,
    selectedStation: Station,
    output: String = "",
    searchVisible: Boolean = false,
    FilteredStations: Option[List[Station]] = None,
    TransportTypeFilter: TransportType = TransportType.All,
    subdomain: String = ""
)

object Model:
  val initial: Model = Model(
    Stations = Left("No data"),
    Departures = None,
    selectedStation = Station("0", "No station selected")
  )
  extension (model: Model)
    def updateStation(station: Station): Model =
      val CheckedStation = getStation(station.id)
      model.copy(
        selectedStation = CheckedStation,
        searchVisible = false,
        Departures = None
      )

    def updateStations(stations: Either[String, List[Station]]): Model =
      model.updateOutput(stations.toString).copy(Stations = stations)

    def updateDepartures(newDepartures: List[Departure]): Model = {
      val allDepartures =
        (model.Departures.getOrElse(List.empty) ++ newDepartures)
          .groupBy(d => (d.line, d.destination, d.waitingTime, d.scheduledTime))
          .values
          .map(_.head)
          .toList
          .sortBy(_.scheduledTime)

      val filteredDepartures =
        if (model.TransportTypeFilter == TransportType.All) allDepartures
        else allDepartures.filter(_.transportType == model.TransportTypeFilter)

      model.copy(Departures = Some(filteredDepartures))
    }

    def updateOutput(output: String): Model =
      model.copy(output = output)

    def toggleSearchVisible: Model =
      model.copy(searchVisible = !model.searchVisible)

    def toggleAppMode: Model =
      model.copy(isTestMode = !model.isTestMode)

    def updateFilteredStations(stations: List[Station]): Model =
      model.copy(FilteredStations = Some(stations))

    private def filterStations(term: String): List[Station] =
      model.Stations match
        case Right(stations) =>
          stations.filter(_.name.toLowerCase.contains(term.toLowerCase))
        case _ => List.empty

    def updateFilteredStations(term: String): Model =
      model.copy(
        FilteredStations = Some(filterStations(term).take(6)),
        searchVisible = term.nonEmpty
      )

    def updateTransportTypeFilter(filter: TransportType): Model =
      model.copy(TransportTypeFilter = filter)

    private def getStation(stationId: String): Station =
      model.Stations match
        case Right(stations) =>
          stations.find(_.id == stationId) match
            case station =>
              station.getOrElse(Station("0", "No station found"))
        case Left(error) =>
          Station(stationId, "Loading..")

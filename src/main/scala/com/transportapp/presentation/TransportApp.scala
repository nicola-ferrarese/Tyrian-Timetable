package com.transportapp.presentation


import scala.scalajs.js.annotation.*
import tyrian.*
import tyrian.Html.*
import cats.effect.IO
import com.transportapp.infrastructure.facades.TransportFacade
import com.transportapp.infrastructure.api.SLApi
import com.transportapp.domain.models.{Departure, Station}
import com.transportapp.application.commands.*
import com.transportapp.application.handlers.SLHandler
import com.transportapp.domain.events.*

import scala.scalajs.js
import com.transportapp.domain.models.TransportType


  
@JSExportTopLevel("TyrianApp")
object TransportApp extends TyrianIOApp[Msg, Model]:
  private val slApi = SLApi()
  private val transportFacade = TransportFacade(slApi)
  private val slCommandHandler = SLHandler(transportFacade)

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    val initialStation = Station("1183", "Professorsslingan")
    val model = Model.initial.updateStation(initialStation)
    val loadStationsCmd = Cmd.Run(slCommandHandler.handle(SLCommand.LoadStations).map(Msg.HandleEvent.apply(_)))
    val loadDeparturesCmd = Cmd.Run(slCommandHandler.handle(SLCommand.GetDepartures(initialStation.id, model.slTransportTypeFilter)).map(Msg.HandleEvent.apply(_)))
    (model, Cmd.Batch(loadStationsCmd, loadDeparturesCmd))

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {
    case Msg.ExecuteCommand(cmd) => handleCommand(cmd, model)
    case Msg.HandleEvent(event) => handleEvent(event, model)
    case Msg.ToggleAppMode => (model.toggleAppMode, Cmd.None)
    case Msg.NoOp => (model, Cmd.None)
  }


  private def handleCommand(cmd: Command, model: Model): (Model, Cmd[IO, Msg]) =
    cmd match
      case slCmd: SLCommand =>
        (model, Cmd.Run(slCommandHandler.handle(slCmd).map(Msg.HandleEvent.apply)))
      case ModelUpdateCommand(updateFn) =>
        (updateFn(model), Cmd.None)
      case _ =>
        (model, Cmd.None)

  private def handleEvent(event: AppEvent, model: Model): (Model, Cmd[IO, Msg]) =
    val (updatedModel, cmd) = event match
      case SLEvent.StationsLoaded(stations) =>
        (model.updateStations(stations), Cmd.None)

      case SLEvent.DeparturesLoaded(departures) =>
        (model.updateDepartures(departures), Cmd.None)

      case TyEvent.stationSelected(station) =>
        val getDeparturesCmd = Cmd.Run(IO.pure(Msg.ExecuteCommand(SLCommand.GetDepartures(station.id, model.slTransportTypeFilter))))
        val clearInputCmd = Cmd.Run(IO.delay {
          js.Dynamic.global.document
            .getElementById("search-input")
            .asInstanceOf[js.Dynamic]
            .value = ""
        }.as(Msg.NoOp))
        (model.updateStation(station), Cmd.Batch(getDeparturesCmd, clearInputCmd))


      case TyEvent.inputUpdated(term) =>
        (model.updateFilteredStations(term), Cmd.None)
      case TyEvent.TransportFilterUpdated(filter) =>
        val getDeparturesCmd = Cmd.Run(IO.pure(Msg.ExecuteCommand(SLCommand.GetDepartures(model.selectedStation.id, filter))))
        (model.updateTransportTypeFilter(filter), getDeparturesCmd)

      case _ => (model, Cmd.None)

    (updatedModel, cmd)


  def router: Location => Msg = _ => Msg.NoOp

  def view(model: Model): Html[Msg] =
    div(
      button(onClick(Msg.ToggleAppMode))("Toggle App Mode"),
      button(onClick(Msg.HandleEvent(TyEvent.TransportFilterUpdated(TransportType.All))))("All"),
      button(onClick(Msg.HandleEvent(TyEvent.TransportFilterUpdated(TransportType.Bus))))("Bus"),
      button(onClick(Msg.HandleEvent(TyEvent.TransportFilterUpdated(TransportType.Metro))))("Metro"),
      if model.isTestMode then testModeView(model) else normalModeView(model)
    )

  private def normalModeView(model: Model): Html[Msg] =
    div(
      tyrian.Html.h2("Stockholm Transit Tracker"),
      div(cls := "header-container")(
        tyrian.Html.span(cls:= "current-stop")(s"From: ${model.selectedStation.name}"),

      div(cls := "search-container")(
        input(
          cls := "search-input",
          id := "search-input",
          placeholder := "Search stop",
          onInput(s => Msg.HandleEvent(TyEvent.inputUpdated(s)))
        ),
        div(
          _class := s"search-results ${if model.searchVisible then "visible" else ""}",
          id := "search-results"
        )(
          model.slFilteredStations.map { stations =>
            stations.map { station =>
              div(_class := "search-result-item")(
                span(onClick(Msg.HandleEvent(TyEvent.stationSelected(station))))(
                  text(s"${station.name}")
                )
              )
            }
          }.getOrElse(List.empty)
        )
      ),
      ),
      model.slDepartures.map(renderData(_, model)).getOrElse(div("No data"))
    )

  private def renderData(data: List[Departure], model: Model): Html[Msg] =
    table(
      thead(
        tr(
          List(
            Option.when(model.slTransportTypeFilter == TransportType.All)(th("Type")),
            Some(th("Line")),
            Some(th("Destination")),
            Some(th("Est.Time"))
          ).flatten
        )
      ),
      tbody(
        data.map { departure =>
          tr(
            List(
              Option.when(model.slTransportTypeFilter == TransportType.All)(
                td(_class := "centeritem")(text(departure.transportType.toString))
              ),
              Some(td(_class := "centeritem")(text(departure.line))),
              Some(td(text(departure.destination))),
              Some(td(_class := "centeritem")(text(departure.waitingTime)))
            ).flatten
          )
        }
      )
    )

  private def testModeView(model: Model): Html[Msg] =
    div(
      h1("Test Mode"),
      button(onClick(Msg.ExecuteCommand(SLCommand.LoadStations)))("Load Bus Stations"),
      button(onClick(Msg.ExecuteCommand(SLCommand.GetDepartures("1183", TransportType.All))))("Load Bus Departures"),
      div(
        h2("API Responses"),
        pre(model.output),
      )
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None

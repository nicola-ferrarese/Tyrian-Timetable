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


  
@JSExportTopLevel("TyrianApp")
object TransportApp extends TyrianIOApp[Msg, Model]:
  private val slApi = SLApi()
  private val transportFacade = TransportFacade(slApi)
  private val slCommandHandler = SLHandler(transportFacade)

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    val model = Model.initial
    (model, Cmd.Run(slCommandHandler.handle(SLCommand.LoadStations).map(Msg.HandleEvent.apply(_))))

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {
    case Msg.ExecuteCommand(cmd) => handleCommand(cmd, model)
    case Msg.HandleEvent(event) => handleEvent(event, model)
    case Msg.ToggleAppMode => (model.copy(isTestMode = !model.isTestMode), Cmd.None)
    case Msg.NoOp => (model, Cmd.None)
    case Msg.FilterStations(term) =>
      (model.copy(slFilteredStations = Some(filterStations(term, model))), Cmd.None)
  }


  private def handleCommand(cmd: Command, model: Model): (Model, Cmd[IO, Msg]) =
    val effect = cmd match
      case slCmd: SLCommand => slCommandHandler.handle(slCmd)
      case _ => IO.pure(SLEvent.NoOp)

    (model, Cmd.Run(effect.map(Msg.HandleEvent.apply(_))))

  private def handleEvent(event: AppEvent, model: Model): (Model, Cmd[IO, Msg]) =
    val updatedModel = event match
      case SLEvent.StationsLoaded(stations) => model.copy(slStations = stations, output=stations.toString)
      case SLEvent.DeparturesLoaded(departures) => model.copy(slDepartures = Some(departures), output=departures.toString)
      case _ => model

    (updatedModel, Cmd.None)


  def router: Location => Msg = _ => Msg.NoOp

  def view(model: Model): Html[Msg] =
    div(
      button(onClick(Msg.ToggleAppMode))("Toggle App Mode"),
      if model.isTestMode then testModeView(model) else normalModeView(model)
    )

  private def normalModeView(model: Model): Html[Msg] =
    div(
      tyrian.Html.h2("Stockholm Transit Tracker"),
      div(cls := "search-container")(
        input(
          cls := "search-input",
          id := "search-input",
          placeholder := "Search stop",
          onInput(s => Msg.FilterStations(s))
        ),
        div(
          _class := s"search-results visible",
          id := "search-results"
        )(
          model.slFilteredStations.map { stations =>
            div()(
              stations.map { station =>
                div(_class := "search-result-item")(
                  tyrian.Html.span(onClick(Msg.ExecuteCommand(SLCommand.GetDepartures(station.id)))
                    )(
                      text(station.name)
                    )
                )

              }
            )
          }.getOrElse(p("No stations found"))
          
        ),
        model.slDepartures.map(renderData).getOrElse(p("Loading..."))
          


      //model.slDepartures.map(renderData).getOrElse(p("Loading..."))

      )
    )

  private def filterStations(searchTerm: String, model: Model): List[Station] =
    val filter= searchTerm.toUpperCase()
    model.slStations.map(_.filter(station => station.name.toUpperCase().contains(filter))).getOrElse(List())




  private def renderData(data: List[Departure]): Html[Msg] =
    table(
      thead(
        tr(
          th("Line"),
          th("Destination"),
          th("Est.Time")
        )
      ),
      tbody(
        data.map { departure =>
          tr(
            td(_class := "centeritem")(text(departure.line)),
            td(text(departure.destination)),
            td(_class := "centeritem")(text(departure.waitingTime))
          )
        }
      )
    )

  private def testModeView(model: Model): Html[Msg] =
    div(
      h1("Test Mode"),
      button(onClick(Msg.ExecuteCommand(SLCommand.LoadStations)))("Load Bus Stations"),
      button(onClick(Msg.ExecuteCommand(SLCommand.GetDepartures("1183"))))("Load Bus Departures"),
      div(
        h2("API Responses"),
        pre(model.output),
      )
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None

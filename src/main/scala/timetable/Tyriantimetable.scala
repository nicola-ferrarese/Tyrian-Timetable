package timetable

import cats.effect.IO
import tyrian.Html.*
import tyrian.*
import api.ApiService
import api.models.{Departure, Stop}
import tyrian.cmds.LocalStorage
import scala.concurrent.duration._
import scala.scalajs.js.annotation.*
import timetable.Msg.*
import timetable.Utils.*

case class Model(
                  data: Option[List[Departure]] = None,
                  error: Option[String] = None,
                  stops: List[Stop] = List.empty,
                  currentStop: Stop = Stop(1183, "Professorsslingan"), // Default stop
                  searchTerm: String = "",
                  isSearchVisible: Boolean = false,
                  subdomain: String = "Tyrian-Timetable" // Subdomain for the app, to allow Github Pages hosting
                )

object Model:
  val initial: Model = Model()

@JSExportTopLevel("TyrianApp")
object Tyriantimetable extends TyrianIOApp[Msg, Model]:
  // Setup initial model state
  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    val initialModel = Model.initial
    val getInitialStopId = Cmd.Batch(
      Utils.getStopIdFromUrl,
      LocalStorage.getItem[IO, Msg]("stopId") {
        case Right(LocalStorage.Result.Found(stopId)) => Msg.InitialStopId(stopId)
        case Left(_) => Msg.InitialStopId("1183")
      }
    )    
    (initialModel, Cmd.Batch(getInitialStopId, Utils.getStops))
  
  // Update function to handle messages
  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {
    // Recieve departure data from the API
    case Msg.DataReceived(data) =>
      val newUrl = if model.subdomain.isEmpty then s"/?stopId=${model.currentStop.id}" else s"/${model.subdomain}?stopId=${model.currentStop.id}"
      (model.copy(data = Some(data)), Nav.pushUrl[IO](newUrl))
    // Error fetching departures data
    case Msg.DataFetchFailed(error) => 
      (model.copy(error = Some(error)), Cmd.None)
    // Recieve full list of stops
    case Msg.StopsReceived(stops) =>
      val updatedCurrentStop = stops.find(_.id == model.currentStop.id).getOrElse(model.currentStop)
      (model.copy(stops = stops, currentStop = updatedCurrentStop), Cmd.None)
    // Fetch stops again if the initial fetch fails
    case Msg.StopsFetchFailed(error) => 
      (model.copy(error = Some(error)), Cmd.Run(ApiService.fetchStops().map {
        case Right(stops) => Msg.StopsReceived(stops)
        case Left(error) => Msg.StopsFetchFailed(error)
      }))
    // Tick to update and fetch data
    case Msg.FetchDataTick =>
      (model, Utils.getPublicTransportData(model.currentStop.id.toString))
    // Set the current stop and fetch the data
    case Msg.SetCurrentStop(stop) =>
      (model.copy(currentStop = stop, searchTerm = "", isSearchVisible = false),
        Cmd.Batch(
          Utils.getPublicTransportData(stop.id.toString),
        )
      )
    // Update the URL when the stop is changed
    case Msg.UpdateUrl(stopId) =>
      val newStop = model.stops.find(_.id.toString == stopId).getOrElse(model.currentStop)
      val newUrl = if model.subdomain.isEmpty then s"/?stopId=$stopId" else s"/${model.subdomain}?stopId=$stopId"
      (model.copy(currentStop = newStop), Cmd.Batch(
        Nav.pushUrl[IO](newUrl),
        Utils.getPublicTransportData(newStop.id.toString),
        LocalStorage.setItem("stopId", stopId) {
            case LocalStorage.Result.Success => Msg.Logger(s"Saved stopId: $stopId")
            case e => Msg.Logger(s"Error saving stopId: $e")
          }
        )
      )
    // Misc
    case Msg.InitialStopId(stopId) =>
      (model.copy(currentStop = Stop(stopId.toInt, "Loading...")), Utils.getPublicTransportData(stopId))
    case Msg.UpdateSearchTerm(term) =>
      if model.searchTerm.equals("") then (model.copy(searchTerm = term, isSearchVisible = false), Cmd.None)
      else (model.copy(searchTerm = term, isSearchVisible = true), Cmd.None)
    case Msg.ToggleSearchVisibility(visible) =>
      if model.searchTerm.equals("") then (model, Cmd.None)
      else (model.copy(isSearchVisible = visible), Cmd.None)
    case Msg.Logger(msg) =>
      println(msg)
      (model, Cmd.None)
    case Msg.NoOp => (model, Cmd.None)
  }
  
  // Router to handle URL changes
  override def router: Location => Msg =
    case loc: Location.Internal =>
      val params = Utils.parseQueryParams(loc.search)
      println(s"Parsed params: $params")
      params.get("stopId") match
        case Some(stopId) =>
          Msg.UpdateUrl(stopId)
          Msg.SetCurrentStop(Stop(stopId.toInt, "Loading..."))
        case None =>
          Msg.Logger("No stopId found in URL")
    case _ =>
      println("Non-internal location, using default stopId")
      Msg.UpdateUrl("1183")

  // Subscriptions to update the model
  val tick = Sub.every[IO](30.second, "FetchDataTick").map(_ => Msg.FetchDataTick)
  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.Batch[IO, Msg](tick)
  
  // View functions to render the UI
  def view(model: Model): Html[Msg] =
    div(
      tyrian.Html.h2("Stockholm Transit Tracker"),
      div(_class := "header-container")(
        tyrian.Html.span(_class := "current-stop")(s"From: ${model.currentStop.name}"),
        div(_class := "search-container")(
          input(
            cls := "search-input",
            id := "search-input",
            placeholder := "Search stop",
            onInput(s => Msg.UpdateSearchTerm(s)),
            onFocus(Msg.ToggleSearchVisibility(true)),
            value := model.searchTerm
          ),
          div(
            _class := s"search-results ${if model.isSearchVisible && model.searchTerm.nonEmpty then "visible" else ""}",
            id := "search-results"
          )(
            model.stops
              .filter(_.name.toLowerCase.contains(model.searchTerm.toLowerCase))
              .take(5)
              .map(stop =>
                div(_class := "search-result-item")(
                  tyrian.Html.span(onClick(Msg.SetCurrentStop(stop)))(text(stop.name))
                )
              )
          )
        )
      ),
      model.error.map(error => p(s"Error: $error")).getOrElse(
        model.data.map(renderData).getOrElse(p("Loading..."))
      )
    )
  // Helper function to render the data
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
            td(_class := "centeritem")(departure.line.designation),
            td(departure.destination),
            td(_class := "centeritem")(departure.display)
          )
        }
      )
    )
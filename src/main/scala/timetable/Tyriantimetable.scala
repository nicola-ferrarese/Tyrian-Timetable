package timetable

import cats.effect.IO
import tyrian.Html.*
import tyrian.*
import api.ApiService
import api.models.{Departure, Stop}
import scala.scalajs.js.annotation.*


enum Msg:
  case DataReceived(data: List[Departure])
  case DataFetchFailed(error: String)
  case StopsReceived(stops: List[Stop])
  case StopsFetchFailed(error: String)
  case SetCurrentStop(stop: Stop)
  case UpdateUrl(stopId: String)
  case UpdateSearchTerm(term: String)
  case ToggleSearchVisibility(visible: Boolean)
  case NoOp

case class Model(
                  data: Option[List[Departure]] = None,
                  error: Option[String] = None,
                  stops: List[Stop] = List.empty,
                  currentStop: Stop = Stop(1183, "Professorslingan"), // Default stop
                  searchTerm: String = "",
                  isSearchVisible: Boolean = false,
                  subdomain: String = "Tyrian-Timetable" // Subdomain for the app, to allow Github Pages hosting
                )

object Model:
  val initial: Model = Model(currentStop = Stop(1183, "Professorslingan"))

@JSExportTopLevel("TyrianApp")
object Tyriantimetable extends TyrianIOApp[Msg, Model]:
  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    val stopId = flags.getOrElse("stopId", "1183") // Default stop
    (Model.initial, Cmd.Batch(getPublicTransportData(stopId), getStops))

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {
    case Msg.DataReceived(data) =>
      val newUrl = if model.subdomain.isEmpty then s"/${model.currentStop.id}" else s"/${model.subdomain}/${model.currentStop.id}"
      (model.copy(data = Some(data)), Nav.pushUrl[IO](newUrl))
      
    case Msg.DataFetchFailed(error) => (model.copy(error = Some(error)), Cmd.None)
    case Msg.StopsReceived(stops) => (model.copy(stops = stops), Cmd.None)
    case Msg.StopsFetchFailed(error) => (model.copy(error = Some(error)), Cmd.None)
    case Msg.SetCurrentStop(stop) => 
      (model.copy(currentStop = stop, searchTerm = "", isSearchVisible = false), 
        Cmd.Batch(
          getPublicTransportData(stop.id.toString),
        )
      )
    case Msg.UpdateUrl(stopId) =>
      val newStop = model.stops.find(_.id.toString == stopId).getOrElse(model.currentStop)
      val newUrl = if model.subdomain.isEmpty then s"/$stopId" else s"/${model.subdomain}/$stopId"
      (model.copy(currentStop = newStop), Cmd.Batch(
        Nav.pushUrl[IO](newUrl))
      )

    case Msg.UpdateSearchTerm(term) =>
        if model.searchTerm.equals("") then (model.copy(searchTerm = term, isSearchVisible = false), Cmd.None)
        else (model.copy(searchTerm = term, isSearchVisible = true), Cmd.None)
    case Msg.ToggleSearchVisibility(visible) =>
        if model.searchTerm.equals("") then (model, Cmd.None)
        else (model.copy(isSearchVisible = visible), Cmd.None)
    case Msg.NoOp => (model, Cmd.None)
  }

  def view(model: Model): Html[Msg] =
    div(
      h2("stockholm Transit Tracker"),
      div(_class := "header-container")(
        span(_class := "current-stop")(s"From: ${model.currentStop.name}"),
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
                  span(onClick(Msg.SetCurrentStop(stop)))(text(stop.name))
                )
              )
          )
        )
      ),
      model.error.map(error => p(s"Error: $error")).getOrElse(
        model.data.map(renderData).getOrElse(p("Loading..."))
      )
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None

  override def router: Location => Msg =
    case loc: Location.Internal =>
      loc.pathName match
        case s"/$stopId" => Msg.UpdateUrl(stopId)
        case _ => Msg.NoOp
    case _ => Msg.NoOp

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

  private def getPublicTransportData(siteId: String): Cmd[IO, Msg] =
    Cmd.Run {
      ApiService.fetchData(siteId).map {
        case Right(data) => Msg.DataReceived(data)
        case Left(error) => Msg.DataFetchFailed(error)
      }
    }

  private def getStops: Cmd[IO, Msg] =
    Cmd.Run {
      ApiService.fetchStops().map {
        case Right(stops) => Msg.StopsReceived(stops)
        case Left(error) => Msg.StopsFetchFailed(error)
      }
    }
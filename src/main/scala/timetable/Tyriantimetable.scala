package timetable

import cats.effect.IO
import tyrian.Html.*
import tyrian.*
import api.ApiService
import api.models.Departure
import scala.scalajs.js.annotation.*

enum Msg:
  case DataReceived(data: List[Departure])
  case DataFetchFailed(error: String)
  case NoOp

case class Model(
                  data: Option[List[Departure]] = None,
                  error: Option[String] = None
                )
object Model:
  val initial: Model = Model()

@JSExportTopLevel("TyrianApp")
object Tyriantimetable extends TyrianIOApp[Msg, Model]:
  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (Model.initial, getPublicTransportData("1183"))

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {
    case Msg.DataReceived(data) => (model.copy(data = Some(data)), Cmd.None)
    case Msg.DataFetchFailed(error) => (model.copy(error = Some(error)), Cmd.None)
    case Msg.NoOp => (model, Cmd.None)
    
  }

  def view(model: Model): Html[Msg] =
    div(
      h1("Public Transport Information"),
      model.error.map(error => p(s"Error: $error")).getOrElse(
        model.data.map(renderData).getOrElse(p("Loading..."))
      )
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None

  override def router: Location => Msg = Routing.none(Msg.NoOp)

  private def renderData(data: List[Departure]): Html[Msg] =
    ul(data.map { departure =>
      li(
        s"${departure.line.designation} (${departure.line.transport_mode}) to ${departure.destination} - ${departure.display} (Expected: ${departure.expected})"
      )
    })


  private def getPublicTransportData(siteId: String): Cmd[IO, Msg] =
    Cmd.Run {
      ApiService.fetchData(siteId).map {
        case Right(data) => Msg.DataReceived(data)
        case Left(error) => Msg.DataFetchFailed(error)
      }
    }





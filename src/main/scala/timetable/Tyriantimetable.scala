package timetable

import cats.effect.IO
import tyrian.Html.*
import tyrian.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("TyrianApp")
case class Model(routes: List[String], waitingTimes: Map[String, Int])

enum Msg:
  case FetchRoutes
  case RoutesReceived(routes: List[String])
  case FetchWaitingTimes
  case WaitingTimesReceived(times: Map[String, Int])
  case NoOp

object TransitApp extends TyrianIOApp[Msg, Model]:
  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (Model(List.empty, Map.empty), Cmd.None)

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
    //case Msg.FetchRoutes =>
    //  (model, Cmd.None)
    //case Msg.RoutesReceived(routes) =>
    //  (model.copy(routes = routes), Cmd.None)
    case Msg.FetchWaitingTimes =>
      (model, Cmd.None)
    case _ => (model, Cmd.None)
    //case Msg.WaitingTimesReceived(times) =>
    //  (model.copy(waitingTimes = times), Cmd.None)

  def view(model: Model): Html[Msg] =
    div(
      h1("Transit Information"),
      button(onClick(Msg.FetchRoutes))("Refresh Routes"),
      button(onClick(Msg.FetchWaitingTimes))("Refresh Waiting Times"),
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None
  def router: Location => Msg = Routing.none(Msg.NoOp)






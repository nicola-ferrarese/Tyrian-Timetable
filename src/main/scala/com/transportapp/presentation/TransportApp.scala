package com.transportapp.presentation


import scala.scalajs.js.annotation.*
import tyrian.*
import tyrian.Html.*
import cats.effect.IO
import com.transportapp.infrastructure.facades.TransportFacade

enum Msg:
  case Departures, Station, None
  case UpdateOutput(output: String)

case class Model(count: Int, output: String):
  val transportFacade = TransportFacade()


object Model:
  val initial: Model = Model(0, "")
  
@JSExportTopLevel("TyrianApp")
object TransportApp extends TyrianIOApp[Msg, Model]:



  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (Model(0, ""), Cmd.None)

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
    case Msg.Departures =>
      (model.copy(count = model.count + 1), Cmd.Run {
        println("Fetching departures")
        model.transportFacade.getSLDepartures("1183").map {
          case Right(departures) => Msg.UpdateOutput(departures.toString())
          case Left(error) => Msg.UpdateOutput(s"Error: $error")
        }
      })
    case Msg.Station =>
      (model.copy(count = model.count - 1), Cmd.Run {
        println("Fetching stations")
        model.transportFacade.stations.map {
          case Right(stations) => Msg.UpdateOutput(stations.toString())
          case Left(error) => Msg.UpdateOutput(s"Error: $error")
        }
      })
    case Msg.UpdateOutput(newOutput) =>
      (model.copy(output = newOutput), Cmd.None)
    case Msg.None => (model, Cmd.None)
        
  
  def router: Location => Msg = _ => Msg.None
    
  def view(model: Model): Html[Msg] =
    div(style("display", "flex"))(
      div(style("flex", "1"))(
        button(onClick(Msg.Station))("Stations"),
        div(model.count.toString),
        button(onClick(Msg.Departures))("Departures")
      ),
      div(style("flex", "1"),
        style("border-left", "1px solid black"),
        style("padding-left", "20px"),
        style("white-space", "pre-wrap"))(
        h3("Output:"),
        div(model.output)
      )
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None

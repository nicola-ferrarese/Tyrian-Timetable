package com.transportapp.presentation


import scala.scalajs.js.annotation.*
import tyrian.*
import tyrian.Html.*
import cats.effect.IO
import com.transportapp.infrastructure.facades.TransportFacade

enum Msg:
  case Increment, Decrement, None
 
case class Model(count: Int)

object Model:
  val initial: Model = Model(0)
  
@JSExportTopLevel("TyrianApp")
object TransportApp extends TyrianIOApp[Msg, Model]:
    

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (Model(0), Cmd.None)

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
      case Msg.Increment => (model.copy(count = model.count + 1), Cmd.None)
      case Msg.Decrement => (model.copy(count = model.count - 1), Cmd.Run {
        TransportFacade().stations.map {
          case Right(stations) => println(stations); Msg.None
          case Left(error) => println(s"Error: $error"); Msg.None
        }
      })
      case Msg.None => (model, Cmd.None)
        
  
  def router: Location => Msg = _ => Msg.Increment
    
  def view(model: Model): Html[Msg] =
    div(
      button(onClick(Msg.Decrement))("-"),
      div(model.count.toString),
      button(onClick(Msg.Increment))("+") ,
      button(onClick(Msg.Increment))("+")
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None

package timetable
import tyrian.*
import cats.effect.IO
import org.scalajs.dom
import api.models.{Departure, Stop}
import api.ApiService

object Utils:
  def parseQueryParams(searchOption: Option[String]): Map[String, String] =
    searchOption match
      case Some(search) =>
        search.stripPrefix("?")
          .split("&")
          .flatMap { param =>
            val parts = param.split("=")
            if (parts.length == 2) Some(parts(0) -> parts(1)) else None
          }
          .toMap
      case None =>
        println("No search string found")
        Map.empty

  def getStopIdFromUrl: Cmd[IO, Msg] =
    Cmd.Run {
      IO {
        val stopId = dom.window.location.search
          .stripPrefix("?")
          .split("&")
          .map(_.split("="))
          .collect { case Array(key, value) => key -> value }
          .toMap
          .get("stopId")
          .getOrElse("1183")
        Msg.InitialStopId(stopId)
      }
    }

  def getPublicTransportData(siteId: String): Cmd[IO, Msg] =
    Cmd.Run {
      ApiService.fetchData(siteId).map {
        case Right(data) => Msg.DataReceived(data)
        case Left(error) => Msg.DataFetchFailed(error)
      }
    }

  def getStops: Cmd[IO, Msg] =
    Cmd.Run {
      ApiService.fetchStops().map {
        case Right(stops) => Msg.StopsReceived(stops)
        case Left(error) => Msg.StopsFetchFailed(error)
      }
    }





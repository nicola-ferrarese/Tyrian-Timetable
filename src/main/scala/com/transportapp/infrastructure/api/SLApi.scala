package com.transportapp.infrastructure.api
import com.transportapp.domain.models.{SLStation, Station}
import cats.effect.IO
import sttp.client4.fetch.FetchBackend
import sttp.client4.*
import sttp.client4.circe.*
import io.circe.generic.auto.*

import scala.concurrent.ExecutionContext.Implicits.global

class SLApi {
  
  private val backend = FetchBackend()
  val stopsUrl = "https://nicoferra.tplinkdns.com:61001/api/sl-stops"

  def convertToStation(station: SLStation): Station = {
    Station(name = station.name, id = station.id.toString)
  }
  
  def loadStations(): IO[Either[String, List[Station]]] = IO.fromFuture {
    IO {
      val request = basicRequest.get(uri"$stopsUrl")
        .response(asJson[List[SLStation]])
      request.send(backend).map { response =>
        // with the response, build the list of stations modifying the values
        response.body match {
          case Right(stations) => Right(stations.map { station =>
            convertToStation(station)
          })
          case Left(error) => Left(error.getMessage)
        }
      }
    }
  }
 
}






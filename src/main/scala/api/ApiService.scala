package api

import sttp.client4.*
import sttp.client4.circe.*
import io.circe.generic.auto.*
import cats.effect.IO
import api.models.{ApiResponse, Departure, Stop}
import sttp.client4.fetch.FetchBackend

import scala.concurrent.ExecutionContext.Implicits.global

object ApiService {
  private val backend = FetchBackend()
  private val baseUrl = "https://transport.integration.sl.se/v1"
  private val stopsUrl = "https://nicoferra.tplinkdns.com:61001/api/sl-stops"
  def getDeparturesUrl(siteId: String): String = s"$baseUrl/sites/$siteId/departures"

  def fetchData(siteId: String): IO[Either[String, List[Departure]]] = IO.fromFuture {
    IO {
      val request = basicRequest.get(uri"${getDeparturesUrl(siteId)}")
        .response(asJson[ApiResponse])

      request.send(backend).map { response =>
        response.body match {
          case Right(data) => Right(data.departures)
          case Left(error) => Left(error.getMessage)
        }
      }
    }
  }

  def fetchStops(): IO[Either[String, List[Stop]]] = IO.fromFuture {
    IO {
      println("Fetching stops data")
      val request = basicRequest.get(uri"$stopsUrl")
        .response(asJson[List[Stop]])

      request.send(backend).map { response =>
        response.body match {
          case Right(stops) => Right(stops)
          case Left(error) => Left(error.getMessage)
        }
      }
    }
  }
}
package api

import sttp.client4.*
import sttp.client4.circe.*
import io.circe.generic.auto.*
import cats.effect.IO
import api.models.{ApiResponse, Departure}
import sttp.client4.fetch.FetchBackend

import scala.concurrent.ExecutionContext.Implicits.global

object ApiService {
  private val backend = FetchBackend()
  private val baseUrl = "https://transport.integration.sl.se/v1"
  def getDeparturesUrl(siteId: String): String = s"$baseUrl/sites/$siteId/departures"

  def fetchData(siteId: String): IO[Either[String, List[Departure]]] = IO.fromFuture {
    IO {
      println(s"Fetching data for site ID: $siteId") // Log the request
      val request = basicRequest.get(uri"${getDeparturesUrl(siteId)}")
        .response(asJson[ApiResponse])

      request.send(backend).map { response =>
        println(s"Received response: ${response.body}") // Log the response
        response.body match {
          case Right(data) => Right(data.departures)
          case Left(error) => Left(error.getMessage)
        }
      }
    }
  }
}
import io.circe._
import io.circe.generic.semiauto._

object ApiService {
  private val baseUrl = "https://transport.integration.sl.se/v1"

  case class Journey(
                      id: Long,
                      state: String,
                      prediction_state: String,
                      passenger_level: String
                    )

  case class StopArea(
                       id: Int,
                       name: String,
                       sname: String,
                       `type`: String
                     )

  case class StopPoint(
                        id: Int,
                        name: String,
                        designation: String
                      )

  case class Line(
                   id: Int,
                   designation: String,
                   transport_mode: String,
                   group_of_lines: String
                 )

  case class Deviation(
                        importance: Int,
                        consequence: String,
                        message: String
                      )

  case class Departure(
                        direction: String,
                        direction_code: Int,
                        via: String,
                        destination: String,
                        state: String,
                        scheduled: String,
                        expected: String,
                        display: String,
                        journey: Journey,
                        stop_area: StopArea,
                        stop_point: StopPoint,
                        line: Line,
                        deviations: List[Deviation]
                      )

  case class Scope(
                    description: String,
                    lines: String,
                    stop_areas: String,
                    stop_points: String
                  )

  case class StopDeviation(
                            id: Long,
                            level: Int,
                            message: String,
                            scope: Scope
                          )

  case class DepartureResponse(
                                departures: List[Departure],
                                stop_deviations: List[StopDeviation]
                              )

  // Decoders
  implicit val journeyDecoder: Decoder[Journey] = deriveDecoder[Journey]
  implicit val stopAreaDecoder: Decoder[StopArea] = deriveDecoder[StopArea]
  implicit val stopPointDecoder: Decoder[StopPoint] = deriveDecoder[StopPoint]
  implicit val lineDecoder: Decoder[Line] = deriveDecoder[Line]
  implicit val deviationDecoder: Decoder[Deviation] = deriveDecoder[Deviation]
  implicit val departureDecoder: Decoder[Departure] = deriveDecoder[Departure]
  implicit val scopeDecoder: Decoder[Scope] = deriveDecoder[Scope]
  implicit val stopDeviationDecoder: Decoder[StopDeviation] = deriveDecoder[StopDeviation]
  implicit val departureResponseDecoder: Decoder[DepartureResponse] = deriveDecoder[DepartureResponse]

  def getDeparturesUrl(siteId: String): String = s"$baseUrl/sites/$siteId/departures"

  // TODO: Implement actual API call using HTTP client
}

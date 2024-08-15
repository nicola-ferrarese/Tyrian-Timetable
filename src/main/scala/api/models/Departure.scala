package api.models

case class ApiResponse(departures: List[Departure])

case class Departure(
                      destination: String,
                      display: String,
                      expected: String,
                      line: Line
                    )

case class Line(
                 designation: String,
                 transport_mode: String
               )

case class Stop(
                 id: Int,
                 name: String,
               )
import api.ApiService
import munit.*
import io.circe.parser.*
import api.ApiService.*

class ApiServiceTest extends FunSuite {
  val sampleJson = """
  {
    "departures": [
      {
        "direction": "string",
        "direction_code": 2,
        "via": "string",
        "destination": "string",
        "state": "NOTEXPECTED",
        "scheduled": "2024-01-01T01:00:00",
        "expected": "2024-01-01T01:00:00",
        "display": "string",
        "journey": {
          "id": 2020062310015,
          "state": "NOTEXPECTED",
          "prediction_state": "NORMAL",
          "passenger_level": "EMPTY"
        },
        "stop_area": {
          "id": 41483,
          "name": "Abborrkroksvägen",
          "sname": "string",
          "type": "BUSTERM"
        },
        "stop_point": {
          "id": 41483,
          "name": "Universitetet",
          "designation": "D"
        },
        "line": {
          "id": 13,
          "designation": "13X",
          "transport_mode": "BUS",
          "group_of_lines": "tunnelbanans gröna linje"
        },
        "deviations": [
          {
            "importance": 5,
            "consequence": "INFORMATION",
            "message": "Resa förbi Arlanda C kräver både UL- och SL- biljett."
          }
        ]
      }
    ],
    "stop_deviations": [
      {
        "id": 26170662,
        "level": 5,
        "message": "Tack för att du följer Folkhälsomyndighetens rekommendationer och visar hänsyn när du reser!",
        "scope": {
          "description": "string",
          "lines": "string",
          "stop_areas": "string",
          "stop_points": "string"
        }
      }
    ]
  }
  """

  test("ApiService should correctly parse the sample JSON response") {
    val result = decode[DepartureResponse](sampleJson)
    assert(result.isRight, "JSON parsing should succeed")

    val response = result.getOrElse(fail("Failed to parse JSON"))
    assertEquals(response.departures.length, 1)
    assertEquals(response.stop_deviations.length, 1)

    val departure = response.departures.head
    assertEquals(departure.direction, "string")
    assertEquals(departure.direction_code, 2)
    assertEquals(departure.destination, "string")
    assertEquals(departure.state, "NOTEXPECTED")
    assertEquals(departure.scheduled, "2024-01-01T01:00:00")
    assertEquals(departure.expected, "2024-01-01T01:00:00")
    
    assertEquals(departure.journey.state, "NOTEXPECTED")
    assertEquals(departure.journey.prediction_state, "NORMAL")
    assertEquals(departure.journey.passenger_level, "EMPTY")

    assertEquals(departure.stop_area.id, 41483)
    assertEquals(departure.stop_area.name, "Abborrkroksvägen")
    assertEquals(departure.stop_area.`type`, "BUSTERM")

    assertEquals(departure.stop_point.id, 41483)
    assertEquals(departure.stop_point.name, "Universitetet")
    assertEquals(departure.stop_point.designation, "D")

    assertEquals(departure.line.id, 13)
    assertEquals(departure.line.designation, "13X")
    assertEquals(departure.line.transport_mode, "BUS")
    assertEquals(departure.line.group_of_lines, "tunnelbanans gröna linje")

    assertEquals(departure.deviations.length, 1)
    assertEquals(departure.deviations.head.importance, 5)
    assertEquals(departure.deviations.head.consequence, "INFORMATION")
    assertEquals(departure.deviations.head.message, "Resa förbi Arlanda C kräver både UL- och SL- biljett.")

    val stopDeviation = response.stop_deviations.head
    assertEquals(stopDeviation.id, 26170662L)
    assertEquals(stopDeviation.level, 5)
    assertEquals(stopDeviation.message, "Tack för att du följer Folkhälsomyndighetens rekommendationer och visar hänsyn när du reser!")
    assertEquals(stopDeviation.scope.description, "string")
    assertEquals(stopDeviation.scope.lines, "string")
    assertEquals(stopDeviation.scope.stop_areas, "string")
    assertEquals(stopDeviation.scope.stop_points, "string")
  }

  test("ApiService should correctly construct the API URL") {
    val siteId = "12345"
    val expectedUrl = s"https://transport.integration.sl.se/v1/sites/$siteId/departures"
    assertEquals(ApiService.getDeparturesUrl(siteId), expectedUrl)
  }
}
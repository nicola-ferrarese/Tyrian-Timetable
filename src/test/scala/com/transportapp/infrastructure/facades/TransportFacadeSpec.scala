import utest._
import cats.effect.IO
import com.transportapp.infrastructure.facades.TransportFacade
import com.transportapp.infrastructure.api.SLApi
import com.transportapp.domain.models.{Station, Departure, TransportType}
import cats.effect.unsafe.implicits.global
import java.time.LocalDateTime
import scala.concurrent.{Future, ExecutionContext}

object TransportFacadeSpec extends TestSuite {
  implicit val ec: ExecutionContext = ExecutionContext.global

  def tests = Tests {
    test("TransportFacade") {
      // Use a fixed timestamp for testing
      val fixedTimestamp = LocalDateTime.of(2023, 1, 1, 12, 0)

      val mockSLApi = new SLApi {
        override def loadStations(): IO[Either[String, List[Station]]] =
          IO.pure(
            Right(
              List(
                Station("1", "Central Station"),
                Station("2", "North Station")
              )
            )
          )

        override def loadDepartures(
            stationId: String,
            filter: TransportType
        ): IO[Option[List[Departure]]] =
          if (stationId == "1") {
            IO.pure(
              Some(
                List(
                  Departure(
                    "101",
                    "Destination A",
                    TransportType.Bus,
                    fixedTimestamp,
                    fixedTimestamp,
                    "5 min"
                  ),
                  Departure(
                    "102",
                    "Destination B",
                    TransportType.Metro,
                    fixedTimestamp,
                    fixedTimestamp,
                    "10 min"
                  )
                ).filter(
                  if (filter == TransportType.All) _ => true
                  else _.transportType == filter
                )
              )
            )
          } else {
            IO.pure(None)
          }
      }

      val transportFacade = new TransportFacade(mockSLApi)

      def runIO[A](io: IO[A]): Future[A] = io.unsafeToFuture()

      test("loadSLStations should return a list of stations") {
        val stationsFuture = runIO(transportFacade.loadSLStations)
        stationsFuture.map { stations =>
          assert(
            stations == Right(
              List(
                Station("1", "Central Station"),
                Station("2", "North Station")
              )
            )
          )
        }
      }

      test("getSLDepartures should return departures for a valid station ID") {
        val departuresFuture =
          runIO(transportFacade.getSLDepartures("1", TransportType.All))
        departuresFuture.map { departures =>
          assert(departures.isDefined)
          assert(departures.get.length == 2)
          assert(
            departures.get.head == Departure(
              "101",
              "Destination A",
              TransportType.Bus,
              fixedTimestamp,
              fixedTimestamp,
              "5 min"
            )
          )
        }
      }

      test("getSLDepartures should return None for an invalid station ID") {
        val departuresFuture =
          runIO(transportFacade.getSLDepartures("3", TransportType.All))
        departuresFuture.map { departures =>
          assert(departures.isEmpty)
        }
      }

      test("getSLDepartures should filter departures by transport type") {
        val departuresFuture =
          runIO(transportFacade.getSLDepartures("1", TransportType.Bus))
        departuresFuture.map { departures =>
          assert(departures.isDefined)
          assert(departures.get.length == 1)
          assert(departures.get.head.transportType == TransportType.Bus)
        }
      }
    }
  }
}

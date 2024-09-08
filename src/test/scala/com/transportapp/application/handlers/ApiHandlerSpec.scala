import utest._
import cats.effect.IO
import com.transportapp.infrastructure.facades.TransportFacade
import com.transportapp.application.commands.ApiCommand
import com.transportapp.domain.events.ApiEvent
import com.transportapp.domain.models.{Departure, Station, TransportType}
import com.transportapp.application.handlers.ApiHandler
import cats.effect.unsafe.implicits.global
import java.time.LocalDateTime
import scala.concurrent.{Future, ExecutionContext}

object ApiHandlerSpec extends TestSuite {
  implicit val ec: ExecutionContext = ExecutionContext.global

  def tests = Tests {
    test("ApiHandler") {
      // Use a fixed timestamp for testing
      val fixedTimestamp = LocalDateTime.of(2023, 1, 1, 12, 0)

      def createMockFacadeAndHandler(
          stations: Either[String, List[Station]] =
            Right(List(Station("1", "Central Station"))),
          departures: Option[List[Departure]] = Some(
            List(
              Departure(
                "1",
                "Destination",
                TransportType.Bus,
                fixedTimestamp,
                fixedTimestamp,
                "10 min",
                "A"
              )
            )
          )
      ): ApiHandler = {
        val mockFacade = new TransportFacade() {
          override def loadSLStations: IO[Either[String, List[Station]]] =
            IO.pure(stations)
          override def getSLDepartures(
              stationId: String,
              filter: TransportType
          ): IO[Option[List[Departure]]] = IO.pure(departures)
        }
        new ApiHandler(mockFacade)
      }

      def runIO[A](io: IO[A]): Future[A] =
        io.unsafeToFuture()

      test("return StationsLoaded event when LoadStations command is given") {
        val eventFuture =
          runIO(createMockFacadeAndHandler().handle(ApiCommand.LoadStations))
        eventFuture.map { event =>
          assert(
            event == SLEvent.StationsLoaded(
              Right(List(Station("1", "Central Station")))
            )
          )
        }
      }

      test(
        "return DeparturesLoaded event with departures when GetDepartures command is given with valid station ID"
      ) {
        val eventFuture = runIO(
          createMockFacadeAndHandler().handle(
            ApiCommand.GetDepartures("1", TransportType.All)
          )
        )
        eventFuture.map { event =>
          assert(
            event == SLEvent.DeparturesLoaded(
              List(
                Departure(
                  "1",
                  "Destination",
                  TransportType.Bus,
                  fixedTimestamp,
                  fixedTimestamp,
                  "10 min",
                  "A"
                )
              )
            )
          )
        }
      }

      test(
        "return DeparturesLoaded event with empty list when GetDepartures command is given with invalid station ID"
      ) {
        val eventFuture = runIO(
          createMockFacadeAndHandler(departures = None).handle(
            ApiCommand.GetDepartures("invalid", TransportType.All)
          )
        )
        eventFuture.map { event =>
          assert(event == SLEvent.DeparturesLoaded(List.empty))
        }
      }

      test(
        "return StationsLoaded event with Left when loading stations fails"
      ) {
        val eventFuture = runIO(
          createMockFacadeAndHandler(stations = Left("Failed to load stations"))
            .handle(ApiCommand.LoadStations)
        )
        eventFuture.map { event =>
          assert(
            event == ApiEvent.StationsLoaded(Left("Failed to load stations"))
          )
        }
      }
    }
  }
}

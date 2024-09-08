import utest._
import cats.effect.IO
import com.transportapp.infrastructure.facades.TransportFacade
import com.transportapp.infrastructure.api.SLApi
import com.transportapp.infrastructure.api.ResRobotApi
import com.transportapp.domain.models.{Station, Departure, TransportType}
import cats.effect.unsafe.implicits.global
import java.time.LocalDateTime
import scala.concurrent.{Future, ExecutionContext}

// In your test code
class TestableTransportFacade(
    override val SLApi: SLApi,
    override val RRApi: ResRobotApi
) extends TransportFacade

// Updated test suite
object TransportFacadeSpec extends TestSuite {
  implicit val ec: ExecutionContext = ExecutionContext.global

  def tests: Tests = Tests {
    test("TransportFacade") {
      val fixedTimestamp = LocalDateTime.of(2023, 1, 1, 12, 0)

      val mockSLApi = new SLApi {
        override def loadStations(): IO[Either[String, List[Station]]] =
          IO.pure(Right(List(Station("1", "SL Station"))))

        override def loadDepartures(
            stationId: String,
            filter: TransportType
        ): IO[Option[List[Departure]]] =
          IO.pure(
            Some(
              List(
                Departure(
                  "101",
                  "SL Destination",
                  TransportType.Bus,
                  fixedTimestamp,
                  fixedTimestamp,
                  "5 min",
                  "SL"
                )
              )
            )
          )
      }

      val mockRRApi = new ResRobotApi {
        override def loadStations(): IO[Either[String, List[Station]]] =
          IO.pure(Right(List(Station("2", "RR Station"))))

        override def loadDepartures(
            stationId: String
        ): IO[Option[List[Departure]]] =
          IO.pure(
            Some(
              List(
                Departure(
                  "201",
                  "RR Destination",
                  TransportType.Metro,
                  fixedTimestamp,
                  fixedTimestamp,
                  "10 min",
                  "RR"
                )
              )
            )
          )
      }

      val transportFacade = new TestableTransportFacade(mockSLApi, mockRRApi)

      def runIO[A](io: IO[A]): Future[A] = io.unsafeToFuture()

      test(
        "loadStations should return combined stations from SL and ResRobot"
      ) {
        val stationsFuture = runIO(transportFacade.loadStations())
        stationsFuture.map { stationsResult =>
          assert(stationsResult.isRight)
          val stations = stationsResult.getOrElse(List.empty)
          assert(stations.length == 2)
          assert(stations.exists(_.name == "SL Station"))
          assert(stations.exists(_.name == "RR Station"))
        }
      }
    }
  }
}

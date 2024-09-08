import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import com.transportapp.presentation.Model
import com.transportapp.domain.models.{Station, Departure, TransportType}
import java.time.LocalDateTime

class ModelSpec extends AnyFunSpec with Matchers {
  describe("Model") {
    val initialStation = Station("1", "Central Station")
    var model          = Model.initial.updateStation(initialStation)

    it("should have default values in the initial model") {
      model.Stations shouldBe Left("No data")
      model.Departures shouldBe None
      model.isTestMode shouldBe false
      model.selectedStation.id shouldBe initialStation.id
      model.output shouldBe empty
      model.searchVisible shouldBe false
      model.FilteredStations shouldBe None
      model.TransportTypeFilter shouldBe TransportType.All
    }

    it("should update the selected station") {
      val newStation = Station("2", "North Station")
      val updatedModel = model
        .updateStations(Right(List(newStation, initialStation)))
        .updateStation(newStation)
      updatedModel.selectedStation shouldBe newStation
      updatedModel.searchVisible shouldBe false
    }

    it("should update the list of stations") {
      model = model.copy(Stations =
        Right(
          List(Station("1", "Central Station"), Station("2", "North Station"))
        )
      )
      val stations =
        List(Station("1", "East Station"), Station("2", "West Station"))
      val updatedModel = model.updateStations(Right(stations))
      updatedModel.Stations shouldBe Right(stations)
    }

    it("should update the list of departures") {
      val now = LocalDateTime.of(2023, 1, 1, 12, 0)
      val now1 = LocalDateTime.of(2023, 1, 1, 12, 5)
      val departures = List(
        Departure("101", "Line 1", TransportType.Bus, now, now, "5 min", "A"),
        Departure("102", "Line 1", TransportType.Metro, now1, now1, "10 min", "B")
      )
      val updatedModel =
        model.copy(Departures = None).updateDepartures(departures)
      updatedModel.Departures shouldBe Some(departures)
    }

    it("should update the output string") {
      val output       = "Test output"
      val updatedModel = model.updateOutput(output)
      updatedModel.output shouldBe output
    }

    it("should toggle the search visibility") {
      val updatedModel = model.toggleSearchVisible
      updatedModel.searchVisible shouldBe true
      val toggledBackModel = updatedModel.toggleSearchVisible
      toggledBackModel.searchVisible shouldBe false
    }

    it("should toggle the test mode") {
      val updatedModel = model.toggleAppMode
      updatedModel.isTestMode shouldBe true
      val toggledBackModel = updatedModel.toggleAppMode
      toggledBackModel.isTestMode shouldBe false
    }

    it("should update the filtered stations list") {
      val stations =
        List(Station("1", "Central Station"), Station("2", "North Station"))
      val modelWithStations = model.updateStations(Right(stations))
      val updatedModel = modelWithStations.updateFilteredStations("Central")
      updatedModel.FilteredStations shouldBe Some(
        List(Station("1", "Central Station"))
      )
      updatedModel.searchVisible shouldBe true
    }

    it("should update the transport type filter") {
      val updatedModel = model.updateTransportTypeFilter(TransportType.Bus)
      updatedModel.TransportTypeFilter shouldBe TransportType.Bus
    }
  }
}

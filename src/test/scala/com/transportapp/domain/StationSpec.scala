import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.transportapp.domain.models.Station

class StationSpec extends AnyFlatSpec with Matchers {

  "A Station" should "be created with an id, name, and transport type" in {
    val station = Station("1", "Central Station")
    station.id should be("1")
    station.name should be("Central Station")
  }

  it should "not allow creation with empty id" in {
    an [IllegalArgumentException] should be thrownBy {
      Station("", "Central Station")
    }
  }

  it should "not allow creation with empty name" in {
    an [IllegalArgumentException] should be thrownBy {
      Station("1", "")
    }
  }

  it should "be equal to another station with the same id, name, and transport type" in {
    val station1 = Station("1", "Central Station")
    val station2 = Station("1", "Central Station")
    station1 should equal(station2)
  }

  it should "not be equal to another station with different id" in {
    val station1 = Station("1", "Central Station")
    val station2 = Station("2", "Central Station")
    station1 should not equal station2
  }
}
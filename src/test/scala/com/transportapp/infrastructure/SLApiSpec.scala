package com.transportapp.infrastructure
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import com.transportapp.infrastructure.api.SLApi

import com.transportapp.domain.models.{SLStation, Station}

class SLApiSpec extends AnyFlatSpec with Matchers with ScalaFutures{
  // Provide an  implicit runtime for IO operations
  def convertToStation(station: SLStation): Station = {
    Station(name = station.name, id = station.id.toString)
  }
  "SLStation to Station conversion" should "work for valid input" in {
    val slStation = SLStation(id = 1, name = "Central Station", lat = Some(59.3293), lon = Some(18.0686))
    val station = SLApi().convertToStation(slStation)

    station.id shouldBe "1"
    station.name shouldBe "Central Station"

  }

  it should "handle stations with missing coordinates" in {
    val slStation = SLStation(id = 2, name = "North Station", lat = None, lon = None)
    val station = convertToStation(slStation)

    station.id shouldBe "2"
    station.name shouldBe "North Station"
  }

  it should "handle stations with very long names" in {
    val longName = "A" * 100 // 100 character long name
    val slStation = SLStation(id = 3, name = longName, lat = Some(59.3293), lon = Some(18.0686))
    val station = convertToStation(slStation)

    station.id shouldBe "3"
    station.name shouldBe longName

  }

  it should "handle stations with special characters in names" in {
    val specialName = "Stätion №1 (Söder)"
    val slStation = SLStation(id = 4, name = specialName, lat = Some(59.3293), lon = Some(18.0686))
    val station = convertToStation(slStation)

    station.id shouldBe "4"
    station.name shouldBe specialName
  }

  it should "throw an exception for SLStation with empty name" in {
    assertThrows[IllegalArgumentException] {
      SLStation(id = 5, name = "", lat = Some(59.3293), lon = Some(18.0686))
    }
  }

  it should "handle conversion of maximum possible ID" in {
    val maxId = Int.MaxValue
    val slStation = SLStation(id = maxId, name = "Max ID Station", lat = Some(59.3293), lon = Some(18.0686))
    val station = convertToStation(slStation)

    station.id shouldBe maxId.toString
    station.name shouldBe "Max ID Station"
  }

  it should "handle conversion of minimum possible ID" in {
    val minId = 0 // Assuming 0 is a valid ID
    val slStation = SLStation(id = minId, name = "Min ID Station", lat = Some(59.3293), lon = Some(18.0686))
    val station = convertToStation(slStation)

    station.id shouldBe minId.toString
    station.name shouldBe "Min ID Station"
  }

  // This test checks that our Station class correctly enforces its own requirements
  "Station creation" should "throw an exception for empty id" in {
    assertThrows[IllegalArgumentException] {
      Station(id = "", name = "Invalid Station")
    }
  }
}

package com.transportapp.presentation

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import tyrian.*
import TransportApp.*

class TransportAppSpec extends AnyFlatSpec with Matchers:

  "TransportApp" should "initialize with count 0" in {
    val (initialModel, _) = TransportApp.init(Map())
    initialModel.count should be(0)
  }

  it should "increment count" in {
    val model = Model(0, "")
    val updateFunction = TransportApp.update(model)
    val (updatedModel, _) = updateFunction(Msg.Departures)
    updatedModel.count should be(1)
  }

  it should "decrement count" in {
    val model = Model(1, "")
    val updateFunction = TransportApp.update(model)
    val (updatedModel, _) = updateFunction(Msg.Station)
    updatedModel.count should be(0)
  }
package com.transportapp.domain.models.slmodels

case class SLDeparture(
                        destination: String,
                        state: String,
                        display: String,
                        scheduled: String,
                        expected: String,
                        line: SLLine,
                        stop_area: SLStopArea,
                        journey: SLJourney
                      )

case class SLLine(
                   id: Int,
                   designation: String,
                   transport_mode: String
                 )

case class SLStopArea(
                       id: Int,
                       name: String,
                       `type`: String
                     )

case class SLJourney(
                      id: Long,
                      state: String
                    )

case class SLDepartureResponse(departures: List[SLDeparture])


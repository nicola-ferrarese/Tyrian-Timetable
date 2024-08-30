package com.transportapp.domain.models
import java.time.LocalDateTime

case class Departure(
                      line: String,
                      destination: String,
                      transportType: TransportType,
                      scheduledTime: LocalDateTime,
                      expectedTime: LocalDateTime,
                      waitingTime: String
                    )



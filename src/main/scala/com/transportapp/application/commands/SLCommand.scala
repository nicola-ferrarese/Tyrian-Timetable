package com.transportapp.application.commands

enum SLCommand extends Command:
    case LoadStations
    case GetDepartures(stationId: String)

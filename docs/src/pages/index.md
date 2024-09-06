# Stockholm Transit Tracker
## Overview
This Transport App is a Scala-based application designed to provide real-time information about public transportation in Stockholm. It interfaces with the Stockholm Local (SL) API to fetch and display station information and departure times for various types of public transport.
The architecture allows for reusability and modularity, in order to have freedom of choiche about data sources and the application itself, making it a proof-of-concept for a generic Tyrian Application.

## Architecture

The application follows a clean architecture pattern, separating concerns into distinct layers:

1. _**Presentation Layer**_: Handles user interface and interactions
2. _**Application Layer**_: Manages business logic and use cases
3. _**Domain Layer**_: Contains core business logic and entities
4. _**Infrastructure Layer**_: Deals with external services and data sources

### Application Overview

```mermaid
classDiagram
    class SL_Station {
        +String id
        +String name
        +String transportType
    }
    class SL_Departure {
        +String id
        +String transportType
        +String destination
        +String scheduledTime
        +String status
    }


    class Station {
        +String id
        +String name
        +TransportType transportType
    }
    class Departure {
        +String id
        +TransportType transportType
        +String destination
        +LocalDateTime scheduledTime
        +DepartureStatus status
    }


    SL_Station .. Station : Converted to
    SL_Departure .. Departure : Converted to



class Station {
+String id
+String name
}

class Departure {
+String line
+String destination
+TransportType transportType
+LocalDateTime scheduledTime
+LocalDateTime expectedTime
+String waitingTime
}

class Model {
+Either[String, List[Station]] slStations
+Option[List[Departure]] slDepartures
+Boolean isTestMode
+Station selectedStation
+String output
+Boolean searchVisible
+Option[List[Station]] slFilteredStations
+TransportType slTransportTypeFilter
+String subdomain
}




class TransportApi {
<<interface>>
+loadStations() IO[Either[String, List[Station]]]
+loadDepartures(String stationId) IO[Option[List[Departure]]]
}

class SLApi {
+loadStations() IO[Either[String, List[Station]]]
+loadDepartures(String stationId, TransportType filter) IO[Option[List[Departure]]]
}

class TransportFacade {
-SLApi slApi
+loadSLStations() IO[Either[String, List[Station]]]
+getSLDepartures(String stationId, TransportType filter) IO[Option[List[Departure]]]
}

class SLHandler {
-TransportFacade transportFacade
+handle(SLCommand) IO[SLEvent]
}

class TransportApp {
+init(Map[String, String]) (Model, Cmd[IO, Msg])
+update(Model) Msg => (Model, Cmd[IO, Msg])
+view(Model) Html[Msg]
+subscriptions(Model) Sub[IO, Msg]
}


TransportApi <|.. SLApi
TransportFacade --> SLApi
SLHandler --> TransportFacade
TransportApp --> SLHandler
TransportApp --> Model
TransportApp --> TransportFacade
Model --> Station
Model --> Departure

```


This diagram illustrates the high-level structure of the application, showing the relationships between key classes and interfaces.

### Events and Commands
```mermaid
classDiagram
    class TransportType {
        <<enumeration>>
        All
        Bus
        Train
        Ferry
        Tram
        Metro
        Taxi
        Ship
    }

    class Station {
        +String id
        +String name
    }

    class Departure {
        +String line
        +String destination
        +TransportType transportType
        +LocalDateTime scheduledTime
        +LocalDateTime expectedTime
        +String waitingTime
    }



    class Command {
        <<interface>>
    }

    class SLCommand {
        <<enumeration>>
        LoadStations
        GetDepartures(String stationId, TransportType filter)
    }



    class AppEvent {
        <<interface>>
    }

    class SLEvent {
        <<enumeration>>
        StationsLoaded(Either[String, List[Station]] stations)
        DeparturesLoaded(List[Departure] departures)
        NoOp
    }

    class TyEvent {
        <<enumeration>>
        stationSelected(Station station)
        inputUpdated(String input)
        TransportFilterUpdated(TransportType filter)
        getStationName(String stationId)
        UpdateDepartures
        NoOp
    }

    AppEvent <|-- TyEvent
    Command <|-- SLCommand
    AppEvent <|-- SLEvent

    Departure --> TransportType
    SLCommand --> TransportType
    TyEvent --> TransportType
    SLEvent --> Station
    SLEvent --> Departure
    TyEvent --> Station
```


This diagram shows the flow of events and commands within the application, demonstrating how user actions are translated into application state changes.

### Core Components and Interactions
```mermaid
classDiagram
    class SL_Station {
        +String id
        +String name
        +String transportType
    }
    class SL_Departure {
        +String id
        +String transportType
        +String destination
        +String scheduledTime
        +String status
    }
    class Station {
        +String id
        +String name
    }
    class Departure {
        +String line
        +String destination
        +TransportType transportType
        +LocalDateTime scheduledTime
        +LocalDateTime expectedTime
        +String waitingTime
    }
    class TransportType {
        <<enumeration>>
        All
        Bus
        Train
        Ferry
        Tram
        Metro
        Taxi
        Ship
    }
    class Model {
        +Either[String, List[Station]] slStations
        +Option[List[Departure]] slDepartures
        +Station selectedStation
        +TransportType slTransportTypeFilter
    }
    class SLApi {
        +loadStations() IO[Either[String, List[Station]]]
        +loadDepartures(String, TransportType) IO[Option[List[Departure]]]
    }
    class TransportFacade {
        -SLApi slApi
        +loadSLStations() IO[Either[String, List[Station]]]
        +getSLDepartures(String, TransportType) IO[Option[List[Departure]]]
    }
    class SLCommand {
        <<enumeration>>
        LoadStations
        GetDepartures(String, TransportType)
    }
    class SLEvent {
        <<enumeration>>
        StationsLoaded(Either[String, List[Station]])
        DeparturesLoaded(List[Departure])
    }
    class TyEvent {
        <<enumeration>>
        stationSelected(Station)
        TransportFilterUpdated(TransportType)
        UpdateDepartures
    }
    class SLHandler {
        -TransportFacade transportFacade
        +handle(SLCommand) IO[SLEvent]
    }
    class TransportApp {
        +init(Map[String, String]) (Model, Cmd[IO, Msg])
        +update(Model) Msg => (Model, Cmd[IO, Msg])
        +view(Model) Html[Msg]
    }

    SL_Station ..> Station : Converted to
    SL_Departure ..> Departure : Converted to
    Station --> TransportType
    Departure --> TransportType
    Model --> Station
    Model --> Departure
    Model --> TransportType
    SLApi ..> SL_Station : Receives
    SLApi ..> SL_Departure : Receives
    TransportFacade --> SLApi : Uses
    SLHandler --> TransportFacade : Uses
    TransportApp --> Model : Updates
    TransportApp --> SLHandler : Uses
    TransportApp --> TransportFacade : Uses
    SLCommand --> TransportType
    SLEvent --> Station
    SLEvent --> Departure
    TyEvent --> Station
    TyEvent --> TransportType
    TransportApp ..> SLCommand : Generates
    TransportApp ..> SLEvent : Handles
    TransportApp ..> TyEvent : Handles
    SLHandler ..> SLCommand : Receives
    SLHandler ..> SLEvent : Produces
```


This diagram provides a detailed view of how core components interact, including data transformations and the flow of information through the system.

## Key Components

- `TransportApp`: The main application class that orchestrates the entire system.
- `Model`: Represents the application's state.
- `TransportFacade`: Acts as an intermediary between the application and the SL API.
- `SLApi`: Handles direct communication with the Stockholm Local API.
- `SLHandler`: Processes commands and generates events related to SL data.
- `Station` and `Departure`: Domain entities representing transport stations and departures.

### Getting Started
To run this project locally, follow these steps:
- Ensure you have sbt (Scala Build Tool) and yarn installed on your system.
- Clone this repository to your local machine.
- Navigate to the project directory in your terminal.
- Run the following commands:

```bash
sbt clean fastLinkJS
yarn install
yarn start
```
Open your web browser and navigate to the address provided by the yarn start command (typically http://localhost:1234).
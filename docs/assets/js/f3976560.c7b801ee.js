"use strict";(self.webpackChunktyrian_transport_docs=self.webpackChunktyrian_transport_docs||[]).push([[795],{9674:(n,e,t)=>{t.r(e),t.d(e,{assets:()=>l,contentTitle:()=>s,default:()=>c,frontMatter:()=>i,metadata:()=>o,toc:()=>p});var a=t(4848),r=t(8453);const i={},s="Stockholm Transit Tracker",o={type:"mdx",permalink:"/Tyrian-Timetable/docs/",source:"@site/src/pages/index.md",title:"Stockholm Transit Tracker",description:"Overview",frontMatter:{},unlisted:!1},l={},p=[{value:"Overview",id:"overview",level:2},{value:"Architecture",id:"architecture",level:2},{value:"Application Overview",id:"application-overview",level:3},{value:"Events and Commands",id:"events-and-commands",level:3},{value:"Core Components and Interactions",id:"core-components-and-interactions",level:3},{value:"Key Components",id:"key-components",level:2},{value:"Getting Started",id:"getting-started",level:3}];function d(n){const e={a:"a",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",header:"header",li:"li",mermaid:"mermaid",ol:"ol",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,r.R)(),...n.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(e.header,{children:(0,a.jsx)(e.h1,{id:"stockholm-transit-tracker",children:"Stockholm Transit Tracker"})}),"\n",(0,a.jsx)(e.h2,{id:"overview",children:"Overview"}),"\n",(0,a.jsx)(e.p,{children:"This Transport App is a Scala-based application designed to provide real-time information about public transportation in Stockholm. It interfaces with the Stockholm Local (SL) API to fetch and display station information and departure times for various types of public transport.\nThe architecture allows for reusability and modularity, in order to have freedom of choiche about data sources and the application itself, making it a proof-of-concept for a generic Tyrian Application."}),"\n",(0,a.jsx)(e.h2,{id:"architecture",children:"Architecture"}),"\n",(0,a.jsx)(e.p,{children:"The application follows a clean architecture pattern, separating concerns into distinct layers:"}),"\n",(0,a.jsxs)(e.ol,{children:["\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.em,{children:(0,a.jsx)(e.strong,{children:"Presentation Layer"})}),": Handles user interface and interactions"]}),"\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.em,{children:(0,a.jsx)(e.strong,{children:"Application Layer"})}),": Manages business logic and use cases"]}),"\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.em,{children:(0,a.jsx)(e.strong,{children:"Domain Layer"})}),": Contains core business logic and entities"]}),"\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.em,{children:(0,a.jsx)(e.strong,{children:"Infrastructure Layer"})}),": Deals with external services and data sources"]}),"\n"]}),"\n",(0,a.jsx)(e.h3,{id:"application-overview",children:"Application Overview"}),"\n",(0,a.jsx)(e.mermaid,{value:"classDiagram\n    class SL_Station {\n        +String id\n        +String name\n        +String transportType\n    }\n    class SL_Departure {\n        +String id\n        +String transportType\n        +String destination\n        +String scheduledTime\n        +String status\n    }\n\n\n    class Station {\n        +String id\n        +String name\n        +TransportType transportType\n    }\n    class Departure {\n        +String id\n        +TransportType transportType\n        +String destination\n        +LocalDateTime scheduledTime\n        +DepartureStatus status\n    }\n\n\n    SL_Station .. Station : Converted to\n    SL_Departure .. Departure : Converted to\n\n\n\nclass Station {\n+String id\n+String name\n}\n\nclass Departure {\n+String line\n+String destination\n+TransportType transportType\n+LocalDateTime scheduledTime\n+LocalDateTime expectedTime\n+String waitingTime\n}\n\nclass Model {\n+Either[String, List[Station]] slStations\n+Option[List[Departure]] slDepartures\n+Boolean isTestMode\n+Station selectedStation\n+String output\n+Boolean searchVisible\n+Option[List[Station]] slFilteredStations\n+TransportType slTransportTypeFilter\n+String subdomain\n}\n\n\n\n\nclass TransportApi {\n<<interface>>\n+loadStations() IO[Either[String, List[Station]]]\n+loadDepartures(String stationId) IO[Option[List[Departure]]]\n}\n\nclass SLApi {\n+loadStations() IO[Either[String, List[Station]]]\n+loadDepartures(String stationId, TransportType filter) IO[Option[List[Departure]]]\n}\n\nclass TransportFacade {\n-SLApi slApi\n+loadSLStations() IO[Either[String, List[Station]]]\n+getSLDepartures(String stationId, TransportType filter) IO[Option[List[Departure]]]\n}\n\nclass SLHandler {\n-TransportFacade transportFacade\n+handle(SLCommand) IO[SLEvent]\n}\n\nclass TransportApp {\n+init(Map[String, String]) (Model, Cmd[IO, Msg])\n+update(Model) Msg => (Model, Cmd[IO, Msg])\n+view(Model) Html[Msg]\n+subscriptions(Model) Sub[IO, Msg]\n}\n\n\nTransportApi <|.. SLApi\nTransportFacade --\x3e SLApi\nSLHandler --\x3e TransportFacade\nTransportApp --\x3e SLHandler\nTransportApp --\x3e Model\nTransportApp --\x3e TransportFacade\nModel --\x3e Station\nModel --\x3e Departure\n"}),"\n",(0,a.jsx)(e.p,{children:"This diagram illustrates the high-level structure of the application, showing the relationships between key classes and interfaces."}),"\n",(0,a.jsx)(e.h3,{id:"events-and-commands",children:"Events and Commands"}),"\n",(0,a.jsx)(e.mermaid,{value:"classDiagram\n    class TransportType {\n        <<enumeration>>\n        All\n        Bus\n        Train\n        Ferry\n        Tram\n        Metro\n        Taxi\n        Ship\n    }\n\n    class Station {\n        +String id\n        +String name\n    }\n\n    class Departure {\n        +String line\n        +String destination\n        +TransportType transportType\n        +LocalDateTime scheduledTime\n        +LocalDateTime expectedTime\n        +String waitingTime\n    }\n\n\n\n    class Command {\n        <<interface>>\n    }\n\n    class SLCommand {\n        <<enumeration>>\n        LoadStations\n        GetDepartures(String stationId, TransportType filter)\n    }\n\n\n\n    class AppEvent {\n        <<interface>>\n    }\n\n    class SLEvent {\n        <<enumeration>>\n        StationsLoaded(Either[String, List[Station]] stations)\n        DeparturesLoaded(List[Departure] departures)\n        NoOp\n    }\n\n    class TyEvent {\n        <<enumeration>>\n        stationSelected(Station station)\n        inputUpdated(String input)\n        TransportFilterUpdated(TransportType filter)\n        getStationName(String stationId)\n        UpdateDepartures\n        NoOp\n    }\n\n    AppEvent <|-- TyEvent\n    Command <|-- SLCommand\n    AppEvent <|-- SLEvent\n\n    Departure --\x3e TransportType\n    SLCommand --\x3e TransportType\n    TyEvent --\x3e TransportType\n    SLEvent --\x3e Station\n    SLEvent --\x3e Departure\n    TyEvent --\x3e Station"}),"\n",(0,a.jsx)(e.p,{children:"This diagram shows the flow of events and commands within the application, demonstrating how user actions are translated into application state changes."}),"\n",(0,a.jsx)(e.h3,{id:"core-components-and-interactions",children:"Core Components and Interactions"}),"\n",(0,a.jsx)(e.mermaid,{value:"classDiagram\n    class SL_Station {\n        +String id\n        +String name\n        +String transportType\n    }\n    class SL_Departure {\n        +String id\n        +String transportType\n        +String destination\n        +String scheduledTime\n        +String status\n    }\n    class Station {\n        +String id\n        +String name\n    }\n    class Departure {\n        +String line\n        +String destination\n        +TransportType transportType\n        +LocalDateTime scheduledTime\n        +LocalDateTime expectedTime\n        +String waitingTime\n    }\n    class TransportType {\n        <<enumeration>>\n        All\n        Bus\n        Train\n        Ferry\n        Tram\n        Metro\n        Taxi\n        Ship\n    }\n    class Model {\n        +Either[String, List[Station]] slStations\n        +Option[List[Departure]] slDepartures\n        +Station selectedStation\n        +TransportType slTransportTypeFilter\n    }\n    class SLApi {\n        +loadStations() IO[Either[String, List[Station]]]\n        +loadDepartures(String, TransportType) IO[Option[List[Departure]]]\n    }\n    class TransportFacade {\n        -SLApi slApi\n        +loadSLStations() IO[Either[String, List[Station]]]\n        +getSLDepartures(String, TransportType) IO[Option[List[Departure]]]\n    }\n    class SLCommand {\n        <<enumeration>>\n        LoadStations\n        GetDepartures(String, TransportType)\n    }\n    class SLEvent {\n        <<enumeration>>\n        StationsLoaded(Either[String, List[Station]])\n        DeparturesLoaded(List[Departure])\n    }\n    class TyEvent {\n        <<enumeration>>\n        stationSelected(Station)\n        TransportFilterUpdated(TransportType)\n        UpdateDepartures\n    }\n    class SLHandler {\n        -TransportFacade transportFacade\n        +handle(SLCommand) IO[SLEvent]\n    }\n    class TransportApp {\n        +init(Map[String, String]) (Model, Cmd[IO, Msg])\n        +update(Model) Msg => (Model, Cmd[IO, Msg])\n        +view(Model) Html[Msg]\n    }\n\n    SL_Station ..> Station : Converted to\n    SL_Departure ..> Departure : Converted to\n    Station --\x3e TransportType\n    Departure --\x3e TransportType\n    Model --\x3e Station\n    Model --\x3e Departure\n    Model --\x3e TransportType\n    SLApi ..> SL_Station : Receives\n    SLApi ..> SL_Departure : Receives\n    TransportFacade --\x3e SLApi : Uses\n    SLHandler --\x3e TransportFacade : Uses\n    TransportApp --\x3e Model : Updates\n    TransportApp --\x3e SLHandler : Uses\n    TransportApp --\x3e TransportFacade : Uses\n    SLCommand --\x3e TransportType\n    SLEvent --\x3e Station\n    SLEvent --\x3e Departure\n    TyEvent --\x3e Station\n    TyEvent --\x3e TransportType\n    TransportApp ..> SLCommand : Generates\n    TransportApp ..> SLEvent : Handles\n    TransportApp ..> TyEvent : Handles\n    SLHandler ..> SLCommand : Receives\n    SLHandler ..> SLEvent : Produces"}),"\n",(0,a.jsx)(e.p,{children:"This diagram provides a detailed view of how core components interact, including data transformations and the flow of information through the system."}),"\n",(0,a.jsx)(e.h2,{id:"key-components",children:"Key Components"}),"\n",(0,a.jsxs)(e.ul,{children:["\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.code,{children:"TransportApp"}),": The main application class that orchestrates the entire system."]}),"\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.code,{children:"Model"}),": Represents the application's state."]}),"\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.code,{children:"TransportFacade"}),": Acts as an intermediary between the application and the SL API."]}),"\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.code,{children:"SLApi"}),": Handles direct communication with the Stockholm Local API."]}),"\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.code,{children:"SLHandler"}),": Processes commands and generates events related to SL data."]}),"\n",(0,a.jsxs)(e.li,{children:[(0,a.jsx)(e.code,{children:"Station"})," and ",(0,a.jsx)(e.code,{children:"Departure"}),": Domain entities representing transport stations and departures."]}),"\n"]}),"\n",(0,a.jsx)(e.h3,{id:"getting-started",children:"Getting Started"}),"\n",(0,a.jsx)(e.p,{children:"To run this project locally, follow these steps:"}),"\n",(0,a.jsxs)(e.ul,{children:["\n",(0,a.jsx)(e.li,{children:"Ensure you have sbt (Scala Build Tool) and yarn installed on your system."}),"\n",(0,a.jsx)(e.li,{children:"Clone this repository to your local machine."}),"\n",(0,a.jsx)(e.li,{children:"Navigate to the project directory in your terminal."}),"\n",(0,a.jsx)(e.li,{children:"Run the following commands:"}),"\n"]}),"\n",(0,a.jsx)(e.pre,{children:(0,a.jsx)(e.code,{className:"language-bash",children:"sbt clean fastLinkJS\nyarn install\nyarn start\n"})}),"\n",(0,a.jsxs)(e.p,{children:["Open your web browser and navigate to the address provided by the yarn start command (typically ",(0,a.jsx)(e.a,{href:"http://localhost:1234",children:"http://localhost:1234"}),")."]})]})}function c(n={}){const{wrapper:e}={...(0,r.R)(),...n.components};return e?(0,a.jsx)(e,{...n,children:(0,a.jsx)(d,{...n})}):d(n)}}}]);
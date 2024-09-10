"use strict";(self.webpackChunktyrian_transport_docs=self.webpackChunktyrian_transport_docs||[]).push([[202],{5516:(e,n,a)=>{a.r(n),a.d(n,{assets:()=>l,contentTitle:()=>s,default:()=>p,frontMatter:()=>r,metadata:()=>o,toc:()=>d});var i=a(4848),t=a(8453);const r={sidebar_position:6,sidebar_label:"Implementation"},s="Implementazione",o={id:"tutorial-basics/implementation",title:"Implementazione",description:"Caratteristiche Principali di Tyrian",source:"@site/docs/tutorial-basics/implementation.md",sourceDirName:"tutorial-basics",slug:"/tutorial-basics/implementation",permalink:"/Tyrian-Timetable/docs/docs/tutorial-basics/implementation",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:6,frontMatter:{sidebar_position:6,sidebar_label:"Implementation"},sidebar:"docSidebar",previous:{title:"Detailed Design",permalink:"/Tyrian-Timetable/docs/docs/tutorial-basics/detailed_design"}},l={},d=[{value:"Caratteristiche Principali di Tyrian",id:"caratteristiche-principali-di-tyrian",level:2},{value:"Aggiornamenti del Modello in Tyrian",id:"aggiornamenti-del-modello-in-tyrian",level:3},{value:"Gestione di Handler, Comandi ed Eventi",id:"gestione-di-handler-comandi-ed-eventi",level:2},{value:"Handler",id:"handler",level:3},{value:"Comandi ed Eventi",id:"comandi-ed-eventi",level:3},{value:"Pattern Matching",id:"pattern-matching",level:2},{value:"Operazioni Monadiche e Elaborazione Parallela",id:"operazioni-monadiche-e-elaborazione-parallela",level:2},{value:"Uso di Cats Effect per le Richieste API",id:"uso-di-cats-effect-per-le-richieste-api",level:3},{value:"Multiple Api e ParMapN",id:"multiple-api-e-parmapn",level:3}];function c(e){const n={code:"code",h1:"h1",h2:"h2",h3:"h3",header:"header",li:"li",ol:"ol",p:"p",pre:"pre",strong:"strong",...(0,t.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.header,{children:(0,i.jsx)(n.h1,{id:"implementazione",children:"Implementazione"})}),"\n",(0,i.jsx)(n.h2,{id:"caratteristiche-principali-di-tyrian",children:"Caratteristiche Principali di Tyrian"}),"\n",(0,i.jsx)(n.p,{children:"Tyrian \xe8 un framework Scala.js che implementa il pattern Model-View-Update (MVU). Le principali caratteristiche utilizzate in questo progetto sono:"}),"\n",(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsxs)(n.li,{children:[(0,i.jsx)(n.strong,{children:"Gestione dello Stato"}),": Tyrian gestisce lo stato dell'applicazione in modo funzionale e immutabile."]}),"\n",(0,i.jsxs)(n.li,{children:[(0,i.jsx)(n.strong,{children:"Ciclo di Aggiornamento"}),": Utilizza un ciclo di aggiornamento per gestire gli eventi e modificare lo stato."]}),"\n",(0,i.jsxs)(n.li,{children:[(0,i.jsx)(n.strong,{children:"Rendering Dichiarativo"}),": Permette di definire la vista in modo dichiarativo."]}),"\n"]}),"\n",(0,i.jsx)(n.p,{children:"Esempio di definizione del modello e della vista in Tyrian:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-scala",children:'case class Model(\n  slStations: Either[String, List[Station]],\n  slDepartures: Option[List[Departure]],\n  selectedStation: Station,\n  // ... \n)\n\ndef view(model: Model): Html[Msg] =\n  div(cls := "TyrianContent")(\n    div(cls := "header-button-container")(\n      // ...\n    ),\n    div()(\n      model.slStations match {\n        case Right(stations) => stationList(stations)\n        case Left(error) => div(cls := "error-message")(error)\n      }\n    )\n  )\n'})}),"\n",(0,i.jsx)(n.h3,{id:"aggiornamenti-del-modello-in-tyrian",children:"Aggiornamenti del Modello in Tyrian"}),"\n",(0,i.jsx)(n.p,{children:"Tyrian gestisce gli aggiornamenti del modello in modo puro e immutabile. La funzione update prende il modello corrente e un messaggio, e restituisce una tupla contenente il modello aggiornato e un comando da eseguire:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-scala",children:"def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {\ncase Msg.ExecuteCommand(cmd) => handleCommand(cmd, model)\ncase Msg.HandleEvent(event) => handleEvent(event, model)\n// ... \n}\n"})}),"\n",(0,i.jsx)(n.p,{children:"Questo pattern assicura che tutti i cambiamenti di stato siano espliciti e tracciabili. La classe Model tipicamente include metodi helper per aggiornare parti specifiche dello stato:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-scala",children:"case class Model(\nslStations: Either[String, List[Station]],\nslDepartures: Option[List[Departure]],\n// ... \n) {\ndef updateStations(stations: Either[String, List[Station]]): Model =\nthis.copy(slStations = stations)\n\ndef updateDepartures(departures: List[Departure]): Model =\nthis.copy(slDepartures = Some(departures))\n// ... \n}\n"})}),"\n",(0,i.jsx)(n.p,{children:"Questi metodi restituiscono sempre una nuova istanza di Model usando copy, mantenendo l'immutabilit\xe0. Questo approccio facilita il ragionamento sui cambiamenti di stato e aiuta a prevenire effetti collaterali."}),"\n",(0,i.jsx)(n.h2,{id:"gestione-di-handler-comandi-ed-eventi",children:"Gestione di Handler, Comandi ed Eventi"}),"\n",(0,i.jsx)(n.p,{children:"Il progetto utilizza un sistema di comandi ed eventi per gestire la logica dell'applicazione. Questo approccio separa chiaramente le intenzioni (comandi) dalle conseguenze (eventi)."}),"\n",(0,i.jsx)(n.h3,{id:"handler",children:"Handler"}),"\n",(0,i.jsxs)(n.p,{children:["L'",(0,i.jsx)(n.code,{children:"ApiHandler"})," \xe8 responsabile di processare i comandi e produrre gli eventi corrispondenti:"]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-scala",children:'class ApiHandler(transportFacade: TransportFacade) {\n  def handle(command: ApiCommand): IO[ApiEvent] = command match {\n    case ApiCommand.LoadStations =>\n      transportFacade.loadStations().map(ApiEvent.StationsLoaded(_))\n    case ApiCommand.GetDepartures(stationId, filter) =>\n      transportFacade.getDepartures(stationId, filter).map {\n        case Some(departures) => ApiEvent.DeparturesLoaded(departures)\n        case None =>\n          println(s"Error loading departures for station $stationId");\n          ApiEvent.DeparturesLoaded(List.empty)\n      }\n  }\n}\n'})}),"\n",(0,i.jsx)(n.h3,{id:"comandi-ed-eventi",children:"Comandi ed Eventi"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-scala",children:"trait AppEvent\n\nenum ApiEvent extends AppEvent {\n  case StationsLoaded(stations: Either[String, List[Station]])\n  case DeparturesLoaded(departures: List[Departure])\n}\n\nenum TyEvent extends AppEvent:\n    case stationSelected(station: Station)\n    case inputUpdated(input: String)\n    case TransportFilterUpdated(filter: TransportType)\n    case getStationName(stationId: String)\n    case UpdateDepartures\n    case NoOp\n"})}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-scala",children:"trait Command\n\nenum ApiCommand extends Command {\n  case LoadStations\n  case GetDepartures(stationId: String, filter: TransportType)\n}\n"})}),"\n",(0,i.jsx)(n.h2,{id:"pattern-matching",children:"Pattern Matching"}),"\n",(0,i.jsx)(n.p,{children:"Il pattern matching \xe8 ampiamente utilizzato, ad esempio nella gestione dei comandi:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-scala",children:"private def handleCommand(cmd: Command, model: Model): (Model, Cmd[IO, Msg]) =\n  cmd match\n    case apiCommand: ApiCommand => (  \n        model,Cmd.Run(\n                apiCommandHandler.handle(apiCommand).map(Msg.HandleEvent.apply)))\n    case _ =>  (model, Cmd.None)\n"})}),"\n",(0,i.jsx)(n.h2,{id:"operazioni-monadiche-e-elaborazione-parallela",children:"Operazioni Monadiche e Elaborazione Parallela"}),"\n",(0,i.jsx)(n.p,{children:"Il progetto fa un uso estensivo di monadi, in particolare IO di Cats Effect, per gestire operazioni asincrone ed effetti collaterali in modo funzionale puro."}),"\n",(0,i.jsx)(n.h3,{id:"uso-di-cats-effect-per-le-richieste-api",children:"Uso di Cats Effect per le Richieste API"}),"\n",(0,i.jsx)(n.p,{children:"Il progetto utilizza Cats Effect per gestire le operazioni asincrone, in particolare per le richieste API. Questo approccio permette di scrivere codice asincrono in modo composizionale e type-safe."}),"\n",(0,i.jsxs)(n.p,{children:["per adattarsi alle Api di ResRobot, \xe8 stato necessario gestire piu richieste in parallelo, per fare cio si \xe8 utilizzato il metodo ",(0,i.jsx)(n.code,{children:"traverse"})," di Cats Effect."]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-scala",children:'class RRApi extends TransportApi:\n  private val backend = FetchBackend()\n\noverride def loadStations(): IO[Either[String, List[Station]]] = {\n  val paramMap = Map(<params-map>) \n\n  // Build a list of requests for each station coordinate\n  val requests: List[\n    Request[Either[ResponseException[String, Exception], RRStationResponse]]\n  ] =\n    stationCoordinates.map { case (lat, lon) =>\n      val fullParamMap = paramMap ++ Map(\n        "originCoordLat"  -> lat.toString,\n        "originCoordLong" -> lon.toString\n      )\n      basicRequest\n        .get(uri"$baseUrl/location.nearbystops?$fullParamMap")\n        .response(asJson[RRStationResponse])\n    }\n  // Send requests in parallel and collect responses\n  requests.traverse(request => IO.fromFuture(IO(backend.send(request)))).map {\n    responses =>\n      responses.traverse(_.body.left.map(_.getMessage)).map {\n        stationResponses =>\n          stationResponses.flatMap { response =>\n            response.stopLocationOrCoordLocation\n              .map(_.StopLocation)\n              .map(convertToStation)\n          }.distinct\n      }\n  }\n}\n'})}),"\n",(0,i.jsxs)(n.p,{children:["Questo codice utilizza ",(0,i.jsx)(n.code,{children:"IO.fromFuture"})," per wrappare la chiamata asincrona in un'operazione ",(0,i.jsx)(n.code,{children:"IO"}),", permettendo una gestione sicura e composizionale dell'effetto."]}),"\n",(0,i.jsx)(n.h3,{id:"multiple-api-e-parmapn",children:"Multiple Api e ParMapN"}),"\n",(0,i.jsx)(n.p,{children:"La classe TransportFacade dimostra l'uso di IO e l'elaborazione parallela per gestire multiple fonti di dati:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-scala",children:"import cats.effect.IO\nimport cats.implicits.*\n\nclass TransportFacade() {\n  val SLApi = new SLApi()\n  val RRApi  = new ResRobotApi()\n  \n  def loadStations(): IO[Either[String, List[Station]]] =\n    (loadSLStations, loadResRobotStations).parMapN { (slResult, rrResult) =>\n      for {\n        slStations <- slResult\n        rrStations <- rrResult\n      } yield slStations ++ rrStations\n    }\n\n  def getDepartures(stationId: String,filter: TransportType): IO[Option[List[Departure]]] =\n    ( getSLDepartures(stationId, filter),\n      getResRobotDepartures(stationId, filter)\n    ).parMapN {\n      case (Some(slDepartures), Some(rrDepartures)) =>\n        Some(slDepartures ++ rrDepartures)\n      case (Some(slDepartures), None) => Some(slDepartures)\n      case (None, Some(rrDepartures)) => Some(rrDepartures)\n      case _                          => None\n    }\n// ...\n}\n"})}),"\n",(0,i.jsx)(n.p,{children:"Qui, parMapN viene utilizzato per eseguire chiamate API in parallelo, migliorando le prestazioni. I risultati vengono poi combinati usando for-comprehension e pattern matching."})]})}function p(e={}){const{wrapper:n}={...(0,t.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(c,{...e})}):c(e)}}}]);
"use strict";(self.webpackChunktyrian_transport_docs=self.webpackChunktyrian_transport_docs||[]).push([[202],{5516:(e,i,n)=>{n.r(i),n.d(i,{assets:()=>d,contentTitle:()=>r,default:()=>m,frontMatter:()=>s,metadata:()=>o,toc:()=>l});var a=n(4848),t=n(8453);const s={sidebar_position:6,sidebar_label:"Implementation"},r="Implementazione",o={id:"tutorial-basics/implementation",title:"Implementazione",description:"Caratteristiche Principali di Tyrian",source:"@site/docs/tutorial-basics/implementation.md",sourceDirName:"tutorial-basics",slug:"/tutorial-basics/implementation",permalink:"/Tyrian-Timetable/docs/docs/tutorial-basics/implementation",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:6,frontMatter:{sidebar_position:6,sidebar_label:"Implementation"},sidebar:"docSidebar",previous:{title:"Detailed Design",permalink:"/Tyrian-Timetable/docs/docs/tutorial-basics/detailed_design"}},d={},l=[{value:"Caratteristiche Principali di Tyrian",id:"caratteristiche-principali-di-tyrian",level:2},{value:"Gestione di Handler, Comandi ed Eventi",id:"gestione-di-handler-comandi-ed-eventi",level:2},{value:"Handler",id:"handler",level:3},{value:"Comandi ed Eventi",id:"comandi-ed-eventi",level:3},{value:"Uso di Cats Effect per le Richieste API",id:"uso-di-cats-effect-per-le-richieste-api",level:2},{value:"Meccanismi Avanzati di Scala",id:"meccanismi-avanzati-di-scala",level:2},{value:"Pattern Matching",id:"pattern-matching",level:3},{value:"Higher-Order Functions",id:"higher-order-functions",level:3},{value:"Impliciti",id:"impliciti",level:3},{value:"Type Classes",id:"type-classes",level:3},{value:"TODO: Add more details about the implementation, show merging of data sources",id:"todo-add-more-details-about-the-implementation-show-merging-of-data-sources",level:4}];function c(e){const i={code:"code",h1:"h1",h2:"h2",h3:"h3",h4:"h4",header:"header",li:"li",ol:"ol",p:"p",pre:"pre",strong:"strong",...(0,t.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(i.header,{children:(0,a.jsx)(i.h1,{id:"implementazione",children:"Implementazione"})}),"\n",(0,a.jsx)(i.h2,{id:"caratteristiche-principali-di-tyrian",children:"Caratteristiche Principali di Tyrian"}),"\n",(0,a.jsx)(i.p,{children:"Tyrian \xe8 un framework Scala.js che implementa il pattern Model-View-Update (MVU). Le principali caratteristiche utilizzate in questo progetto sono:"}),"\n",(0,a.jsxs)(i.ol,{children:["\n",(0,a.jsxs)(i.li,{children:[(0,a.jsx)(i.strong,{children:"Gestione dello Stato"}),": Tyrian gestisce lo stato dell'applicazione in modo funzionale e immutabile."]}),"\n",(0,a.jsxs)(i.li,{children:[(0,a.jsx)(i.strong,{children:"Ciclo di Aggiornamento"}),": Utilizza un ciclo di aggiornamento per gestire gli eventi e modificare lo stato."]}),"\n",(0,a.jsxs)(i.li,{children:[(0,a.jsx)(i.strong,{children:"Rendering Dichiarativo"}),": Permette di definire la vista in modo dichiarativo."]}),"\n"]}),"\n",(0,a.jsx)(i.p,{children:"Esempio di definizione del modello e della vista in Tyrian:"}),"\n",(0,a.jsx)(i.pre,{children:(0,a.jsx)(i.code,{className:"language-scala",children:'case class Model(\n  slStations: Either[String, List[Station]],\n  slDepartures: Option[List[Departure]],\n  selectedStation: Station,\n  // ... altri campi\n)\n\ndef view(model: Model): Html[Msg] =\n  div(cls := "TyrianContent")(\n    div(cls := "header-button-container")(\n      // ... contenuto della vista\n    ),\n    div()(\n      if model.isTestMode then testModeView(model) else normalModeView(model)\n    )\n  )\n'})}),"\n",(0,a.jsx)(i.h2,{id:"gestione-di-handler-comandi-ed-eventi",children:"Gestione di Handler, Comandi ed Eventi"}),"\n",(0,a.jsx)(i.p,{children:"Il progetto utilizza un sistema di comandi ed eventi per gestire la logica dell'applicazione. Questo approccio separa chiaramente le intenzioni (comandi) dalle conseguenze (eventi)."}),"\n",(0,a.jsx)(i.h3,{id:"handler",children:"Handler"}),"\n",(0,a.jsxs)(i.p,{children:["L'",(0,a.jsx)(i.code,{children:"SLHandler"})," \xe8 responsabile di processare i comandi e produrre gli eventi corrispondenti:"]}),"\n",(0,a.jsx)(i.pre,{children:(0,a.jsx)(i.code,{className:"language-scala",children:"class SLHandler(transportFacade: TransportFacade){\n  def handle(command: SLCommand): IO[SLEvent] = command match {\n    case SLCommand.LoadStations =>\n      transportFacade.loadSLStations.map(SLEvent.StationsLoaded(_))\n    case SLCommand.GetDepartures(stationId, filter) =>\n      transportFacade.getSLDepartures(stationId, filter).map {\n        case Some(departures) => SLEvent.DeparturesLoaded(departures)\n        case None => SLEvent.DeparturesLoaded(List.empty)\n      }\n  }\n}\n"})}),"\n",(0,a.jsx)(i.h3,{id:"comandi-ed-eventi",children:"Comandi ed Eventi"}),"\n",(0,a.jsx)(i.p,{children:"I comandi e gli eventi sono definiti come ADTs (Algebraic Data Types):"}),"\n",(0,a.jsx)(i.pre,{children:(0,a.jsx)(i.code,{className:"language-scala",children:"enum SLCommand extends Command:\n    case LoadStations\n    case GetDepartures(stationId: String, filter: TransportType)\n\nenum SLEvent extends AppEvent:\n  case StationsLoaded(stations: Either[String, List[Station]])\n  case DeparturesLoaded(departures: List[Departure])\n  case NoOp\n"})}),"\n",(0,a.jsx)(i.h2,{id:"uso-di-cats-effect-per-le-richieste-api",children:"Uso di Cats Effect per le Richieste API"}),"\n",(0,a.jsx)(i.p,{children:"Il progetto utilizza Cats Effect per gestire le operazioni asincrone, in particolare per le richieste API. Questo approccio permette di scrivere codice asincrono in modo composizionale e type-safe."}),"\n",(0,a.jsxs)(i.p,{children:["Esempio di utilizzo di Cats Effect nella ",(0,a.jsx)(i.code,{children:"SLApi"}),":"]}),"\n",(0,a.jsx)(i.pre,{children:(0,a.jsx)(i.code,{className:"language-scala",children:'import cats.effect.IO\nimport sttp.client4.*\nimport sttp.client4.circe.*\nimport io.circe.generic.auto.*\n\nclass SLApi extends TransportApi:\n  private val backend = FetchBackend()\n  \n  def loadStations(): IO[Either[String, List[Station]]] = IO.fromFuture {\n    IO {\n      val request = basicRequest.get(uri"$stopsUrl")\n        .response(asJson[List[SLStation]])\n      request.send(backend).map { response =>\n        response.body match {\n          case Right(stations) => Right(stations.map(convertToStation))\n          case Left(error) => Left(error.getMessage)\n        }\n      }\n    }\n  }\n'})}),"\n",(0,a.jsxs)(i.p,{children:["Questo codice utilizza ",(0,a.jsx)(i.code,{children:"IO.fromFuture"})," per wrappare la chiamata asincrona in un'operazione ",(0,a.jsx)(i.code,{children:"IO"}),", permettendo una gestione sicura e composizionale dell'effetto."]}),"\n",(0,a.jsx)(i.h2,{id:"meccanismi-avanzati-di-scala",children:"Meccanismi Avanzati di Scala"}),"\n",(0,a.jsx)(i.h3,{id:"pattern-matching",children:"Pattern Matching"}),"\n",(0,a.jsx)(i.p,{children:"Il pattern matching \xe8 ampiamente utilizzato, ad esempio nella gestione dei comandi:"}),"\n",(0,a.jsx)(i.pre,{children:(0,a.jsx)(i.code,{className:"language-scala",children:"def handleCommand(cmd: Command, model: Model): (Model, Cmd[IO, Msg]) =\n  cmd match\n    case slCmd: SLCommand =>\n      (model, Cmd.Run(slCommandHandler.handle(slCmd).map(Msg.HandleEvent.apply)))\n    case _ =>\n      (model, Cmd.None)\n"})}),"\n",(0,a.jsx)(i.h3,{id:"higher-order-functions",children:"Higher-Order Functions"}),"\n",(0,a.jsx)(i.p,{children:"Le funzioni di ordine superiore sono utilizzate per la gestione degli aggiornamenti:"}),"\n",(0,a.jsx)(i.pre,{children:(0,a.jsx)(i.code,{className:"language-scala",children:"def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {\n  case Msg.ExecuteCommand(cmd) => handleCommand(cmd, model)\n  case Msg.HandleEvent(event) => handleEvent(event, model)\n  // ... altri casi\n}\n"})}),"\n",(0,a.jsx)(i.h3,{id:"impliciti",children:"Impliciti"}),"\n",(0,a.jsx)(i.p,{children:"Gli impliciti sono utilizzati per fornire il backend HTTP e i decoder JSON:"}),"\n",(0,a.jsx)(i.pre,{children:(0,a.jsx)(i.code,{className:"language-scala",children:"implicit val backend = FetchBackend()\nimplicit val stationDecoder: Decoder[SLStation] = ...\n"})}),"\n",(0,a.jsx)(i.h3,{id:"type-classes",children:"Type Classes"}),"\n",(0,a.jsx)(i.p,{children:"Le type class sono utilizzate per definire comportamenti generici, come la conversione da DTO a entit\xe0 di dominio:"}),"\n",(0,a.jsx)(i.pre,{children:(0,a.jsx)(i.code,{className:"language-scala",children:"trait Converter[A, B] {\n  def convert(a: A): B\n}\n\nimplicit val stationConverter: Converter[SLStation, Station] = ...\n"})}),"\n",(0,a.jsx)(i.h4,{id:"todo-add-more-details-about-the-implementation-show-merging-of-data-sources",children:"TODO: Add more details about the implementation, show merging of data sources"})]})}function m(e={}){const{wrapper:i}={...(0,t.R)(),...e.components};return i?(0,a.jsx)(i,{...e,children:(0,a.jsx)(c,{...e})}):c(e)}}}]);
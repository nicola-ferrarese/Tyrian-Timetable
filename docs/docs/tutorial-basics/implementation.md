---
sidebar_position: 6
sidebar_label: Implementation
---


# Implementazione

## Caratteristiche Principali di Tyrian

Tyrian è un framework Scala.js che implementa il pattern Model-View-Update (MVU). Le principali caratteristiche utilizzate in questo progetto sono:

1. **Gestione dello Stato**: Tyrian gestisce lo stato dell'applicazione in modo funzionale e immutabile.
2. **Ciclo di Aggiornamento**: Utilizza un ciclo di aggiornamento per gestire gli eventi e modificare lo stato.
3. **Rendering Dichiarativo**: Permette di definire la vista in modo dichiarativo.

Esempio di definizione del modello e della vista in Tyrian:

```scala
case class Model(
  slStations: Either[String, List[Station]],
  slDepartures: Option[List[Departure]],
  selectedStation: Station,
  // ... 
)

def view(model: Model): Html[Msg] =
  div(cls := "TyrianContent")(
    div(cls := "header-button-container")(
      // ...
    ),
    div()(
      model.slStations match {
        case Right(stations) => stationList(stations)
        case Left(error) => div(cls := "error-message")(error)
      }
    )
  )
```
### Aggiornamenti del Modello in Tyrian
Tyrian gestisce gli aggiornamenti del modello in modo puro e immutabile. La funzione update prende il modello corrente e un messaggio, e restituisce una tupla contenente il modello aggiornato e un comando da eseguire:

```scala
def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {
case Msg.ExecuteCommand(cmd) => handleCommand(cmd, model)
case Msg.HandleEvent(event) => handleEvent(event, model)
// ... 
}
```
Questo pattern assicura che tutti i cambiamenti di stato siano espliciti e tracciabili. La classe Model tipicamente include metodi helper per aggiornare parti specifiche dello stato:

```scala
case class Model(
slStations: Either[String, List[Station]],
slDepartures: Option[List[Departure]],
// ... 
) {
def updateStations(stations: Either[String, List[Station]]): Model =
this.copy(slStations = stations)

def updateDepartures(departures: List[Departure]): Model =
this.copy(slDepartures = Some(departures))
// ... 
}
```
Questi metodi restituiscono sempre una nuova istanza di Model usando copy, mantenendo l'immutabilità. Questo approccio facilita il ragionamento sui cambiamenti di stato e aiuta a prevenire effetti collaterali.


## Gestione di Handler, Comandi ed Eventi

Il progetto utilizza un sistema di comandi ed eventi per gestire la logica dell'applicazione. Questo approccio separa chiaramente le intenzioni (comandi) dalle conseguenze (eventi).

### Handler

L'`ApiHandler` è responsabile di processare i comandi e produrre gli eventi corrispondenti:

```scala
class ApiHandler(transportFacade: TransportFacade) {
  def handle(command: ApiCommand): IO[ApiEvent] = command match {
    case ApiCommand.LoadStations =>
      transportFacade.loadStations().map(ApiEvent.StationsLoaded(_))
    case ApiCommand.GetDepartures(stationId, filter) =>
      transportFacade.getDepartures(stationId, filter).map {
        case Some(departures) => ApiEvent.DeparturesLoaded(departures)
        case None =>
          println(s"Error loading departures for station $stationId");
          ApiEvent.DeparturesLoaded(List.empty)
      }
  }
}
```

### Comandi ed Eventi

```scala
trait AppEvent

enum ApiEvent extends AppEvent {
  case StationsLoaded(stations: Either[String, List[Station]])
  case DeparturesLoaded(departures: List[Departure])
}

enum TyEvent extends AppEvent:
    case stationSelected(station: Station)
    case inputUpdated(input: String)
    case TransportFilterUpdated(filter: TransportType)
    case getStationName(stationId: String)
    case UpdateDepartures
    case NoOp
```
```scala
trait Command

enum ApiCommand extends Command {
  case LoadStations
  case GetDepartures(stationId: String, filter: TransportType)
}
```
## Pattern Matching

Il pattern matching è ampiamente utilizzato, ad esempio nella gestione dei comandi:

```scala
private def handleCommand(cmd: Command, model: Model): (Model, Cmd[IO, Msg]) =
  cmd match
    case apiCommand: ApiCommand => (  
        model,Cmd.Run(
                apiCommandHandler.handle(apiCommand).map(Msg.HandleEvent.apply)))
    case _ =>  (model, Cmd.None)
```

## Operazioni Monadiche e Elaborazione Parallela
Il progetto fa un uso estensivo di monadi, in particolare IO di Cats Effect, per gestire operazioni asincrone ed effetti collaterali in modo funzionale puro.

### Uso di Cats Effect per le Richieste API

Il progetto utilizza Cats Effect per gestire le operazioni asincrone, in particolare per le richieste API. Questo approccio permette di scrivere codice asincrono in modo composizionale e type-safe.

per adattarsi alle Api di ResRobot, è stato necessario gestire piu richieste in parallelo, per fare cio si è utilizzato il metodo `traverse` di Cats Effect.

```scala
class RRApi extends TransportApi:
  private val backend = FetchBackend()

override def loadStations(): IO[Either[String, List[Station]]] = {
  val paramMap = Map(<params-map>) 

  // Build a list of requests for each station coordinate
  val requests: List[
    Request[Either[ResponseException[String, Exception], RRStationResponse]]
  ] =
    stationCoordinates.map { case (lat, lon) =>
      val fullParamMap = paramMap ++ Map(
        "originCoordLat"  -> lat.toString,
        "originCoordLong" -> lon.toString
      )
      basicRequest
        .get(uri"$baseUrl/location.nearbystops?$fullParamMap")
        .response(asJson[RRStationResponse])
    }
  // Send requests in parallel and collect responses
  requests.traverse(request => IO.fromFuture(IO(backend.send(request)))).map {
    responses =>
      responses.traverse(_.body.left.map(_.getMessage)).map {
        stationResponses =>
          stationResponses.flatMap { response =>
            response.stopLocationOrCoordLocation
              .map(_.StopLocation)
              .map(convertToStation)
          }.distinct
      }
  }
}
```
Questo codice utilizza `IO.fromFuture` per wrappare la chiamata asincrona in un'operazione `IO`, permettendo una gestione sicura e composizionale dell'effetto.

### Multiple Api e ParMapN
La classe TransportFacade dimostra l'uso di IO e l'elaborazione parallela per gestire multiple fonti di dati:

```scala
import cats.effect.IO
import cats.implicits.*

class TransportFacade() {
  val SLApi = new SLApi()
  val RRApi  = new ResRobotApi()
  
  def loadStations(): IO[Either[String, List[Station]]] =
    (loadSLStations, loadResRobotStations).parMapN { (slResult, rrResult) =>
      for {
        slStations <- slResult
        rrStations <- rrResult
      } yield slStations ++ rrStations
    }

  def getDepartures(stationId: String,filter: TransportType): IO[Option[List[Departure]]] =
    ( getSLDepartures(stationId, filter),
      getResRobotDepartures(stationId, filter)
    ).parMapN {
      case (Some(slDepartures), Some(rrDepartures)) =>
        Some(slDepartures ++ rrDepartures)
      case (Some(slDepartures), None) => Some(slDepartures)
      case (None, Some(rrDepartures)) => Some(rrDepartures)
      case _                          => None
    }
// ...
}
```
Qui, parMapN viene utilizzato per eseguire chiamate API in parallelo, migliorando le prestazioni. I risultati vengono poi combinati usando for-comprehension e pattern matching.

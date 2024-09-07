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
  // ... altri campi
)

def view(model: Model): Html[Msg] =
  div(cls := "TyrianContent")(
    div(cls := "header-button-container")(
      // ... contenuto della vista
    ),
    div()(
      if model.isTestMode then testModeView(model) else normalModeView(model)
    )
  )
```

## Gestione di Handler, Comandi ed Eventi

Il progetto utilizza un sistema di comandi ed eventi per gestire la logica dell'applicazione. Questo approccio separa chiaramente le intenzioni (comandi) dalle conseguenze (eventi).

### Handler

L'`SLHandler` è responsabile di processare i comandi e produrre gli eventi corrispondenti:

```scala
class SLHandler(transportFacade: TransportFacade){
  def handle(command: SLCommand): IO[SLEvent] = command match {
    case SLCommand.LoadStations =>
      transportFacade.loadSLStations.map(SLEvent.StationsLoaded(_))
    case SLCommand.GetDepartures(stationId, filter) =>
      transportFacade.getSLDepartures(stationId, filter).map {
        case Some(departures) => SLEvent.DeparturesLoaded(departures)
        case None => SLEvent.DeparturesLoaded(List.empty)
      }
  }
}
```

### Comandi ed Eventi

I comandi e gli eventi sono definiti come ADTs (Algebraic Data Types):

```scala
enum SLCommand extends Command:
    case LoadStations
    case GetDepartures(stationId: String, filter: TransportType)

enum SLEvent extends AppEvent:
  case StationsLoaded(stations: Either[String, List[Station]])
  case DeparturesLoaded(departures: List[Departure])
  case NoOp
```

## Uso di Cats Effect per le Richieste API

Il progetto utilizza Cats Effect per gestire le operazioni asincrone, in particolare per le richieste API. Questo approccio permette di scrivere codice asincrono in modo composizionale e type-safe.

Esempio di utilizzo di Cats Effect nella `SLApi`:

```scala
import cats.effect.IO
import sttp.client4.*
import sttp.client4.circe.*
import io.circe.generic.auto.*

class SLApi extends TransportApi:
  private val backend = FetchBackend()
  
  def loadStations(): IO[Either[String, List[Station]]] = IO.fromFuture {
    IO {
      val request = basicRequest.get(uri"$stopsUrl")
        .response(asJson[List[SLStation]])
      request.send(backend).map { response =>
        response.body match {
          case Right(stations) => Right(stations.map(convertToStation))
          case Left(error) => Left(error.getMessage)
        }
      }
    }
  }
```

Questo codice utilizza `IO.fromFuture` per wrappare la chiamata asincrona in un'operazione `IO`, permettendo una gestione sicura e composizionale dell'effetto.

## Meccanismi Avanzati di Scala

### Pattern Matching

Il pattern matching è ampiamente utilizzato, ad esempio nella gestione dei comandi:

```scala
def handleCommand(cmd: Command, model: Model): (Model, Cmd[IO, Msg]) =
  cmd match
    case slCmd: SLCommand =>
      (model, Cmd.Run(slCommandHandler.handle(slCmd).map(Msg.HandleEvent.apply)))
    case _ =>
      (model, Cmd.None)
```

### Higher-Order Functions

Le funzioni di ordine superiore sono utilizzate per la gestione degli aggiornamenti:

```scala
def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = {
  case Msg.ExecuteCommand(cmd) => handleCommand(cmd, model)
  case Msg.HandleEvent(event) => handleEvent(event, model)
  // ... altri casi
}
```

### Impliciti

Gli impliciti sono utilizzati per fornire il backend HTTP e i decoder JSON:

```scala
implicit val backend = FetchBackend()
implicit val stationDecoder: Decoder[SLStation] = ...
```

### Type Classes

Le type class sono utilizzate per definire comportamenti generici, come la conversione da DTO a entità di dominio:

```scala
trait Converter[A, B] {
  def convert(a: A): B
}

implicit val stationConverter: Converter[SLStation, Station] = ...
```

#### TODO: Add more details about the implementation, show merging of data sources

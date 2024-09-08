---
sidebar_position: 3
sidebar_label: Architectural Design
---

# Design Architetturale

## Architettura Complessiva
L'applicazione Stockholm Transit Tracker segue un'architettura a layer. Questa struttura permette una chiara separazione delle responsabilità, facilitando la manutenibilità, la testabilità e l'estensibilità del sistema.
### Layer dell'Applicazione

- _Presentation Layer_: Gestisce l'interfaccia utente e le interazioni. 
    - Componenti principali: TransportApp, view function
    - Tecnologie: Tyrian, Scala.js


- _Application Layer_: Gestisce la logica di business e i casi d'uso.
  - Componenti principali: ApiHandler, Model
  - Responsabilità: Processare i comandi, gestire gli eventi, aggiornare lo stato dell'applicazione


- _Domain Layer_: Contiene la logica di business core e le entità del dominio.
  - Componenti principali: Station, Departure, TransportType
  - Responsabilità: Definire le strutture dati e le regole di business fondamentali


- _Infrastructure Layer_: Si occupa dei servizi esterni e delle fonti di dati.
    - Componenti principali: SLApi (Stockholm Lokaltrafik) , RRApi (ResRobot), TransportFacade
    - Responsabilità: Comunicazione con API esterne, gestione dei dati persistenti

## Interazioni tra i Layer

Le interazioni tra i layer seguono un flusso specifico:

1. Il Presentation Layer cattura le azioni dell'utente e le traduce in comandi.
2. I comandi vengono inviati all'Application Layer, che li processa utilizzando l'SLHandler.
3. L'SLHandler interagisce con il Domain Layer per eseguire la logica di business e con l'Infrastructure Layer per accedere ai dati esterni.
4. I risultati vengono restituiti all'Application Layer sotto forma di eventi.
5. L'Application Layer aggiorna il Model in base agli eventi.
6. Il Presentation Layer viene notificato dei cambiamenti e aggiorna la vista di conseguenza.

### Diagramma delle Interazioni

```mermaid
sequenceDiagram
    participant PL as Presentation Layer
    participant AL as Application Layer
    participant DL as Domain Layer
    participant IL as Infrastructure Layer
    
    PL->>AL: Invia comando (es. LoadStations)
    AL->>IL: Richiede dati
    IL->>IL: Chiama API esterne
    IL-->>AL: Restituisce dati grezzi
    AL->>DL: Converte in entità di dominio
    DL-->>AL: Restituisce entità
    AL->>AL: Genera evento (es. StationsLoaded)
    AL-->>PL: Notifica cambiamento di stato
    PL->>PL: Aggiorna vista
```

## Diagramma Architetturale di Alto Livello

```mermaid
graph TD
    A[TransportApp] --> B[Model]
    C[ApiHandler]
    A --> C
    C --> D[TransportFacade]
    D --> E[SLApi]
    D --> L[RRApi]
    B --> G[Stations]
    B --> H[Departures]
    B --> I[Filters, options, etc.]
```

Questo diagramma mostra le principali componenti dell'applicazione e le loro relazioni.

## Pattern Architetturali Utilizzati

- **_Model-View-Update (MVU)_**
Implementato attraverso Tyrian, questo pattern separa la logica di stato (Model), la presentazione (View) e l'aggiornamento dello stato (Update).

- **_Command-Query Responsibility Segregation (CQRS)_**
Separazione tra comandi (ApiCommands) che modificano lo stato e query che leggono lo stato, implementato attraverso la struttura degli eventi e dei comandi.

- **_Event Sourcing_**
Utilizzo di eventi (ApiEvent e TyEvents) per rappresentare cambiamenti nello stato dell'applicazione, in mondo distinto
tra eventi relativi ai dati esterni e eventi relativi all'interfaccia utente e al Framework.

- **_Facade Pattern_**
  TransportFacade agisce come una facciata, semplificando l'interfaccia per l'accesso ai servizi di trasporto.

- **_Repository Pattern_**
SLApi e RRApi fungono da repository per l'accesso ai dati esterni, astratti attraverso l'interfaccia TransportFacade.
## Componenti Principali del Sistema

- **TransportApp**: Componente principale che gestisce il ciclo di vita dell'applicazione.
- **Model**: Rappresenta lo stato dell'applicazione.
- **ApiHandler**: Gestisce i comandi SL e produce eventi corrispondenti.
- **TransportFacade**: Astrae l'accesso ai servizi di trasporto.
- **SLApi**: Interfaccia per l'accesso ai dati esterni del servizio SL.
- **RRApi**: Interfaccia per l'accesso ai dati esterni del servizio ResRobot.


## Interazioni tra i Layer

Le interazioni tra i layer seguono un flusso specifico:

1. Il Presentation Layer cattura le azioni dell'utente e le traduce in comandi.
2. I comandi vengono inviati all'Application Layer, che li processa utilizzando l'SLHandler.
3. L'SLHandler interagisce con il Domain Layer per eseguire la logica di business e con l'Infrastructure Layer per accedere ai dati esterni.
4. I risultati vengono restituiti all'Application Layer sotto forma di eventi.
5. L'Application Layer aggiorna il Model in base agli eventi.
6. Il Presentation Layer viene notificato dei cambiamenti e aggiorna la vista di conseguenza.

### Diagramma delle Interazioni

```mermaid
sequenceDiagram
    participant PL as Presentation Layer
    participant AL as Application Layer
    participant DL as Domain Layer
    participant IL as Infrastructure Layer
    
    PL->>AL: Invia comando (es. LoadStations)
    AL->>IL: Richiede dati
    IL->>IL: Chiama API esterne
    IL-->>AL: Restituisce dati grezzi
    AL->>DL: Converte in entità di dominio
    DL-->>AL: Restituisce entità
    AL->>AL: Genera evento (es. StationsLoaded)
    AL-->>PL: Notifica cambiamento di stato
    PL->>PL: Aggiorna vista
```

## Gestione degli Eventi e dei Comandi

```mermaid
classDiagram
    class Command {
        <<interface>>
    }
    class ApiCommand {
        <<enumeration>>
        LoadStations
        inputUpdated(String input)
        GetDepartures(String stationId, TransportType filter)
    }
    class AppEvent {
        <<interface>>
    }
    class ApiEvent {
        <<enumeration>>
        StationsLoaded(Either[String, List[Station]] stations)
        DeparturesLoaded(List[Departure] departures)
    }
    class TyEvent {
        <<enumeration>>
        inputUpdated(String input)
        stationSelected(Station station)
        TransportFilterUpdated(TransportType filter)
        inputUpdated(String input)
    }

    Command <|-- ApiCommand
    AppEvent <|-- ApiEvent
    AppEvent <|-- TyEvent
```
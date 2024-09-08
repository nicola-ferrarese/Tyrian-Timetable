---
sidebar_position: 4
sidebar_label: Detailed Design
---

# Design Dettagliato

Il design dettagliato dell'applicazione Stockholm Transit Tracker si basa su quattro layer principali, seguendo i principi della Clean Architecture. Ogni layer ha responsabilità specifiche e interagisce con gli altri in modo definito.

### Componenti Principali del Sistema

- **TransportApp**: Componente principale che gestisce il ciclo di vita dell'applicazione.
- **Model**: Rappresenta lo stato dell'applicazione.
- **ApiHandler**: Gestisce i comandi SL e produce eventi corrispondenti.
- **TransportFacade**: Astrae l'accesso ai servizi di trasporto.
- **SLApi**: Interfaccia per l'accesso ai dati esterni del servizio SL.
- **RRApi**: Interfaccia per l'accesso ai dati esterni del servizio ResRobot.

### Interazioni tra Componenti

```mermaid
sequenceDiagram
    participant TransportApp
    participant Model
    participant ApiHandler
    participant TransportFacade
    participant SLApi

    TransportApp->>ApiHandler: Esegue ApiCommand
    ApiHandler->>TransportFacade: Richiede dati
    TransportFacade->>SLApi: Chiama API esterna
    TransportFacade->>RRApi: Chiama API esterna
    SLApi-->>TransportFacade: Restituisce dati
    RRApi-->>TransportFacade: Restituisce dati
    TransportFacade-->>ApiHandler: Restituisce risultato
    ApiHandler-->>TransportApp: Produce SLEvent
    TransportApp->>Model: Aggiorna stato
    TransportApp->>TransportApp: Aggiorna vista
```

## Presentation Layer

Il Presentation Layer è responsabile dell'interfaccia utente e delle interazioni con l'utente.

### Componenti Principali
- `TransportApp`: Classe principale che orchestral'intera applicazione.
- `view` function: Genera la rappresentazione HTML dell'interfaccia utente.

### Responsabilità
- Renderizzare l'interfaccia utente
- Catturare le interazioni dell'utente
- Inviare comandi al layer sottostante
- Aggiornare la vista in base ai cambiamenti di stato

### Diagramma

```mermaid
classDiagram
    class TransportApp {
        +init(Map[String, String]) (Model, Cmd[IO, Msg])
        +update(Model) Msg => (Model, Cmd[IO, Msg])
        +view(Model) Html[Msg]
        +subscriptions(Model) Sub[IO, Msg]
    }
    class Html {
        <<interface>>
    }
    TransportApp <--> TransportFacade : uses
    Model --> Html : generates
    TransportApp --> Model : contains
```

## 2. Application Layer

L'Application Layer gestisce la logica di business e coordina le interazioni tra il Presentation Layer e il Domain Layer.

### Componenti Principali
- `ApiHandler`: Processa i comandi e genera eventi relativi alle Api, reindirizzandoli alla Facade opportuna.
- `Model`: Rappresenta lo stato dell'applicazione.

### Responsabilità
- Processare i comandi ricevuti dal Presentation Layer
- Aggiornare lo stato dell'applicazione
- Generare eventi in risposta ai comandi
- Coordinare le interazioni con il Domain Layer e l'Infrastructure Layer

### Diagramma

```mermaid
classDiagram
    class TransportFacade {
        +loadSLStations() IO[Either[String, List[Station]]]
        +getSLDepartures(String, TransportType) IO[Option[List[Departure]]]
    }
    class ApiHandler {
        +handle(SLCommand) IO[SLEvent]
    }
    class TransportApp {
        +Either[String, List[Station]] slStations
        +Option[List[Departure]] slDepartures
        +Station selectedStation
        +TransportType slTransportTypeFilter
    }
    class ApiCommand {
        <<enumeration>>
        LoadStations
        GetDepartures(String, TransportType)
    }
    class ApiEvent {
        <<enumeration>>
        StationsLoaded(Either[String, List[Station]])
        DeparturesLoaded(List[Departure])
    }
    TransportFacade --> SLApi : uses
    TransportFacade --> RRApi : uses
    ApiHandler <--> TransportFacade: uses 
    ApiHandler <--> TransportApp : sends events
    ApiHandler --> ApiCommand : processes
    ApiHandler --> ApiEvent : generates
    TransportApp --> Stations : contains
    TransportApp --> Departures : contains
```

## 3. Domain Layer

Il Domain Layer contiene la logica di business core e le entità del dominio.

### Componenti Principali
- `models`: modello di dominio
  - `Station`: Rappresenta una stazione di trasporto.
  - `Departure`: Rappresenta una partenza.
  - `TransportType`: Enumerazione per distinguere tra diversi tipi di trasporto.
- `Events`: Eventi relativi al dominio.
  - Specifici per ogni diversa responsabilità del dominio (ApiEvent, TyrianEvent).

### Responsabilità
- Definire le strutture dati fondamentali del dominio
- Implementare la logica di business core
- Fornire un'interfaccia per le operazioni di dominio

```mermaid
classDiagram
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
        +String Operator
    }
    class TransportType {
        <<enumeration>>
        +Bus
        +Metro
        +Train
        +Tram
    }
    class ApiEvent {
        <<enumeration>>
        +StationsLoaded(Either[String, List[Station]])
        +DeparturesLoaded(List[Departure])
    }
    class TyEvent {
        <<enumeration>>
         +stationSelected(station: Station)
         +inputUpdated(input: String)
         +TransportFilterUpdated(filter: TransportType)
         +getStationName(stationId: String)
         +UpdateDepartures
         +NoOp
    }
    ApiEvent --> AppEvent : extends
    TyEvent --> AppEvent : extends
```

## 4. Infrastructure Layer

L'Infrastructure Layer si occupa delle interazioni con servizi esterni e della gestione dei dati persistenti.

### Componenti Principali
- `SLApi`: Interfaccia per la comunicazione con l'API Stockholm Local.
- `RRApi`: Interfaccia per la comunicazione con l'API ResRobot.
- `TransportFacade`: Astrae l'accesso ai servizi di trasporto.

### Responsabilità
- Comunicare con API esterne
- Gestire la persistenza dei dati
- Convertire i dati esterni nel formato del dominio
- Fornire un'interfaccia pulita per l'accesso ai dati esterni

### Diagramma

```mermaid
classDiagram
    class TransportApi {
        <<interface>>
        +loadStations() IO[Either[String, List[Station]]]
        +loadDepartures(String stationId) IO[Option[List[Departure]]]
    }
    class SLApi {
        +loadStations() IO[Either[String, List[Station]]]
        +loadDepartures(String, TransportType) IO[Option[List[Departure]]]
    }
    class RRApi {
        +loadStations() IO[Either[String, List[Station]]]
        +loadDepartures(String, TransportType) IO[Option[List[Departure]]]
    }
    class TransportFacade {
        -SLApi slApi
        +loadSLStations() IO[Either[String, List[Station]]]
        +getSLDepartures(String, TransportType) IO[Option[List[Departure]]]
    }
    TransportApi <|.. SLApi
    TransportApi <|.. RRApi
    TransportFacade --> SLApi : uses
    TransportFacade --> RRApi : uses
```





Utilizzo di DTO per l'Astrazione delle Sorgenti Dati
Un aspetto chiave dell'Infrastructure Layer è l'utilizzo di Data Transfer Objects (DTO) nell' applicazione **SLStation** e **SLDeparture**. Questi DTO agiscono come intermediari tra le API esterne e il modello di dominio dell'applicazione.
```mermaid
classDiagram
class SL_Station {
+String id
+String name
}
class SL_Departure {
+String id
+String transportType
+String destination
+String scheduledTime
+String status
}
class RR_Station {
    +extId String
    +name String
}
class RR_Departure {
    +direction String
    +time String
    +date String
    +ProductAtStop RRProduct
}
class RRProduct {
    +line String
    +operator String
    +catOut String (category)
}
    
    
class Station {
+String id
+String name
+TransportType transportType
}
class Departure {
+String line
+String destination
+TransportType transportType
+LocalDateTime scheduledTime
+LocalDateTime expectedTime
+String waitingTime
}
SL_Station ..> Station : Converted to
SL_Departure ..> Departure : Converted to
RR_Station ..> Station : Converted to
RR_Departure ..> Departure : Converted to
RRProduct ..> RR_Departure : included in
```

L'utilizzo di questi DTO offre diversi vantaggi:

- **_Disaccoppiamento_**: I DTO isolano il modello di dominio dalle strutture dati specifiche delle API esterne.
- **_Flessibilità_**: Permettono di integrare facilmente nuove fonti di dati senza modificare il modello di dominio.
- **_Unificazione_**: Consentono di unificare dati provenienti da diverse sorgenti (es. SL, altre compagnie di trasporto) in un unico modello coerente.
- **_Evoluzione Indipendente_**: Il modello di dominio può evolvere indipendentemente dai cambiamenti nelle API esterne.


---
sidebar_position: 4
sidebar_label: Detailed Design
---

# Design Dettagliato

Il design dettagliato dell'applicazione Stockholm Transit Tracker si basa su quattro layer principali, seguendo i principi della Clean Architecture. Ogni layer ha responsabilità specifiche e interagisce con gli altri in modo definito.

## 1. Presentation Layer

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
    TransportApp --> Html : generates
```

## 2. Application Layer

L'Application Layer gestisce la logica di business e coordina le interazioni tra il Presentation Layer e il Domain Layer.

### Componenti Principali
- `SLHandler`: Processa i comandi e genera eventi relativi ai dati SL.
- `Model`: Rappresenta lo stato dell'applicazione.

### Responsabilità
- Processare i comandi ricevuti dal Presentation Layer
- Aggiornare lo stato dell'applicazione
- Generare eventi in risposta ai comandi
- Coordinare le interazioni con il Domain Layer e l'Infrastructure Layer

### Diagramma

```mermaid
classDiagram
    class SLHandler {
        -TransportFacade transportFacade
        +handle(SLCommand) IO[SLEvent]
    }
    class Model {
        +Either[String, List[Station]] slStations
        +Option[List[Departure]] slDepartures
        +Station selectedStation
        +TransportType slTransportTypeFilter
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
    SLHandler --> SLCommand : processes
    SLHandler --> SLEvent : generates
    Model --> Station : contains
    Model --> Departure : contains
```

## 3. Domain Layer

Il Domain Layer contiene la logica di business core e le entità del dominio.

### Componenti Principali
- `Station`: Rappresenta una stazione di trasporto.
- `Departure`: Rappresenta una partenza.
- `TransportType`: Enumeration dei tipi di trasporto.

### Responsabilità
- Definire le strutture dati fondamentali del dominio
- Implementare la logica di business core
- Fornire un'interfaccia per le operazioni di dominio

### Diagramma

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
    Departure --> TransportType : uses
```

## 4. Infrastructure Layer

L'Infrastructure Layer si occupa delle interazioni con servizi esterni e della gestione dei dati persistenti.

### Componenti Principali
- `SLApi`: Interfaccia per la comunicazione con l'API Stockholm Local.
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
    class TransportFacade {
        -SLApi slApi
        +loadSLStations() IO[Either[String, List[Station]]]
        +getSLDepartures(String, TransportType) IO[Option[List[Departure]]]
    }
    TransportApi <|.. SLApi
    TransportFacade --> SLApi : uses
```

Utilizzo di DTO per l'Astrazione delle Sorgenti Dati
Un aspetto chiave dell'Infrastructure Layer è l'utilizzo di Data Transfer Objects (DTO) nell' applicazione **SLStation** e **SLDeparture**. Questi DTO agiscono come intermediari tra le API esterne e il modello di dominio dell'applicazione.
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
+String line
+String destination
+TransportType transportType
+LocalDateTime scheduledTime
+LocalDateTime expectedTime
+String waitingTime
}
SL_Station ..> Station : Converted to
SL_Departure ..> Departure : Converted to
```

L'utilizzo di questi DTO offre diversi vantaggi:

- **_Disaccoppiamento_**: I DTO isolano il modello di dominio dalle strutture dati specifiche delle API esterne.
- **_Flessibilità_**: Permettono di integrare facilmente nuove fonti di dati senza modificare il modello di dominio.
- **_Unificazione_**: Consentono di unificare dati provenienti da diverse sorgenti (es. SL, altre compagnie di trasporto) in un unico modello coerente.
- **_Evoluzione Indipendente_**: Il modello di dominio può evolvere indipendentemente dai cambiamenti nelle API esterne.

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
    IL->>IL: Chiama API esterna
    IL-->>AL: Restituisce dati grezzi
    AL->>DL: Converte in entità di dominio
    DL-->>AL: Restituisce entità
    AL->>AL: Genera evento (es. StationsLoaded)
    AL-->>PL: Notifica cambiamento di stato
    PL->>PL: Aggiorna vista
```

Questa struttura a layer garantisce una separazione chiara delle responsabilità, facilitando la manutenzione, il testing e l'estensione dell'applicazione. Ogni layer ha un ruolo ben definito e comunica con gli altri attraverso interfacce chiaramente definite, promuovendo un design modulare e flessibile.
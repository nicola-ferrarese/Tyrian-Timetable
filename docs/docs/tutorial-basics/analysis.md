---
sidebar_position: 1
sidebar_label: Analysis
---

# Dominio

Il dominio dell'applicazione è incentrato sul trasporto pubblico. L'obiettivo principale è fornire informazioni in tempo reale su partenze e arrivi, migliorando l'esperienza degli utenti nell'utilizzo dei trasporti pubblici, fornendo una soluzione
adattabile e compatibile a diverse API .

### Analisi del Dominio
Lo sviluppo è partito da un'analisi del dominio, utilizzando tecniche di Domain-Driven Design (DDD) per identificare le entità chiave, le loro relazioni e le operazioni fondamentali del sistema. Questo processo ha portato alla definizione di un modello di dominio che cattura i concetti principali del trasporto pubblico e le interazioni tra di essi.

## Concetti fondamentali

### Stazione (Station)
Rappresenta un punto di partenza o arrivo per i mezzi di trasporto. 

### Partenza (Departure)
Indica un viaggio in partenza da una stazione specifica, con informazioni su orario, destinazione, stato e operatore che effettua la corsa.


### Tipo di Trasporto (TransportType)
Enumeration che distingue tra diversi mezzi di trasporto.

## Strutture dati principali

### Station
```scala
case class Station(id: String, name: String, transportType: TransportType)
```

### Departure
```scala
case class Departure(
  id: String,
  transportType: TransportType,
  destination: String,
  scheduledTime: LocalDateTime,
  expectedTime: LocalDateTime,
  status: DepartureStatus,
  operator: String  
)
```


## Operazioni principali

### Caricamento stazioni
Recupera l'elenco delle stazioni disponibili per un determinato tipo di trasporto.

### Ricerca partenze
Ottiene le partenze programmate da una specifica stazione, con possibilità di filtraggio per tipo di trasporto.

### Aggiornamento in tempo reale
Aggiorna periodicamente le informazioni su partenze e arrivi per garantire dati sempre attuali.

## Interazioni con sistemi esterni

### API di trasporto pubblico
L'applicazione si integra con diverse API esterne per ottenere dati in tempo reale su stazioni, partenze e arrivi.
Lo scopo è di unificare le informazioni provenienti da diverse fonti e renderle accessibili in un'unica interfaccia utente.

## Sfide tecniche

- Gestione delle operazioni asincrone e degli effetti collaterali introdotte dall'interazione con le API esterne
- Integrazione di dati provenienti da diverse fonti e con formati diversi
- Gestione delle inconsistenze e degli errori nei dati esterni
- Mantenimento dell'accuratezza delle informazioni in tempo reale

## Framework e Tecnologie Chiave
### Tyrian
Tyrian è un framework Scala.js per la creazione di applicazioni web single-page (SPA) reattive. Nel contesto di questo progetto, Tyrian gioca un ruolo fondamentale:

- **Architettura Model-View-Update (MVU)**: Tyrian implementa il pattern MVU, che facilita la gestione dello stato dell'applicazione e la sua sincronizzazione con l'interfaccia utente.
- **Componenti Reattivi**: Permette di creare componenti UI reattivi che si aggiornano automaticamente in risposta ai cambiamenti di stato.
- **Gestione degli Eventi**: Offre un sistema robusto per la gestione degli eventi utente e la loro traduzione in azioni che modificano lo stato dell'applicazione.
- **Supporto per Effetti Collaterali**: Fornisce meccanismi per gestire operazioni asincrone e effetti collaterali in modo funzionale e type-safe.
- **Integrazione con Scala.js**: Sfrutta la potenza di Scala.js per compilare il codice Scala in JavaScript, consentendo l'esecuzione dell'applicazione direttamente nel browser.

L'utilizzo di Tyrian nel progetto ha permesso di creare un'interfaccia utente reattiva e performante, mantenendo al contempo la type-safety e l'espressività di Scala. La sua integrazione con il modello di dominio.
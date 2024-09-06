---
sidebar_position: 2
sidebar_label: Requirements
---

# Requisiti

## Requisiti di Business
- Fornire informazioni in tempo reale su partenze e arrivi dei mezzi di trasporto pubblico
- Migliorare l'esperienza degli utenti nell'utilizzo dei trasporti pubblici
- Ridurre i tempi di attesa degli utenti fornendo informazioni accurate
- Supportare diversi fonti di dati e API di trasporto pubblico, uniformando l'accesso alle informazioni

## Modello di Dominio
- _Station_: rappresenta una fermata o un aeroporto
- _Departure_: rappresenta una partenza programmata
- _Arrival_: rappresenta un arrivo programmato (principalmente per aerei)
- _Route_: rappresenta un percorso tra due stazioni
- _TransportType_: enumeration per distinguere tra diversi tipi di trasporto

## Requisiti Funzionali

### Requisiti Utente
- Visualizzare le partenze in tempo reale da una specifica stazione
- Cercare stazioni per nome
- Filtrare le partenze per tipo di trasporto
- Visualizzare informazioni dettagliate su una specifica partenza

### Requisiti di Sistema
- Integrare dati da diverse API di trasporto pubblico
- Aggiornare automaticamente le informazioni sulle partenze ogni 30 secondi
- Gestire errori di connessione alle API esterne
- Supportare la scalabilità per gestire un elevato numero di richieste contemporanee

## Requisiti Non Funzionali
- Performance: tempo di risposta dell'applicazione inferiore a 2 secondi
- Affidabilità: disponibilità del sistema del 99.9%
- Scalabilità: supporto fino a 10.000 utenti concorrenti
- Sicurezza: protezione dei dati personali degli utenti
- Usabilità: interfaccia intuitiva e responsive per dispositivi mobili e desktop

## Requisiti di Implementazione
- Scala 3.x
- Tyrian indigo 0.11.x
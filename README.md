## Stockholm Transit Tracker
[![Open in Github Pages](https://img.shields.io/badge/Open%20in-Github%20Pages-purple?logo=github)](https://nicola-ferrarese.github.io/Tyrian-Timetable/)
### Overview
Transit Tracker is an experimental project built with Scala and Tyrian, designed to show real-time public transportation data in Stockholm SL network. 
### Technology Stack

- **_Scala_**: The primary programming language used for both backend and frontend logic.
- **_Tyrian_**: A pure functional library for building interactive web applications in Scala.
- **_Trafiklab API_**: Provides real-time public transport data for Stockholm.

### Purpose
The main goals of this project are:

 - To gain hands-on experience with Scala in a web development context.
- To explore the capabilities of the Tyrian framework for building reactive web applications.
- To demonstrate integration with external APIs (Trafiklab) in a functional programming paradigm.

### Getting Started
To run this project locally, follow these steps:
- Ensure you have sbt (Scala Build Tool) and yarn installed on your system.
- Clone this repository to your local machine.
- Navigate to the project directory in your terminal.
- Run the following commands:

```bash
sbt clean fastLinkJS
yard install
yarn start
```
Open your web browser and navigate to the address provided by the yarn start command (typically http://localhost:1234).

### Acknowledgements
Special thanks to [Trafiklab](https://www.trafiklab.se/api/trafiklab-apis/sl/) for providing acess to their free API.

**_Note_**: _This project is not affiliated with or endorsed by SL (Storstockholms Lokaltrafik) or Trafiklab. It is an independent, experimental project created for learning purposes._
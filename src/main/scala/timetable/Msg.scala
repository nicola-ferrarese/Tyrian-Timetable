package timetable
import api.models.{Departure, Stop}

enum Msg:
  //Timetable messages
  case DataReceived(data: List[Departure])
  case DataFetchFailed(error: String)
  //Stop messages
  case StopsReceived(stops: List[Stop])
  case StopsFetchFailed(error: String)
  //model messages
  case InitialStopId(stopId: String)
  case SetCurrentStop(stop: Stop)
  case UpdateUrl(stopId: String)
  case UpdateSearchTerm(term: String)
  case ToggleSearchVisibility(visible: Boolean)
  case FetchDataTick
  case Logger(msg: String)
  case NoOp
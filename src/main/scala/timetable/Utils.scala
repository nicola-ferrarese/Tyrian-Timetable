package timetable

object Utils:
  def parseQueryParams(searchOption: Option[String]): Map[String, String] =
    searchOption match
      case Some(search) =>
        println(s"Parsing search string: $search")
        search.stripPrefix("?")
          .split("&")
          .flatMap { param =>
            val parts = param.split("=")
            if (parts.length == 2) Some(parts(0) -> parts(1)) else None
          }
          .toMap
      case None =>
        println("No search string found")
        Map.empty



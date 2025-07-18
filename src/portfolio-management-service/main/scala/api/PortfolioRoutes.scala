package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class PortfolioRoutes {
  val routes: Route =
    pathPrefix("portfolio") {
      get {
        complete("OK")
      }
    }
}

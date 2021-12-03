package routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives.{path, _}
import akka.http.scaladsl.server._
import services.Stripper

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success}

@Singleton
class Routes @Inject() (stripper: Stripper) {

  lazy val routes: Route = {
    path("metadata-removal") {
      path("clean") {
        path(Segment) { fileName =>
          get {
            onComplete(stripper.clean(fileName)) {
              case Success(value) => complete(value)
              case Failure(exception) => complete(InternalServerError, exception.getMessage)
            }
          }
        }
      }
      path("reset"){
        get {
          onComplete(stripper.reset) { _ => complete(NoContent)}
        }
      }
    }
  }

}

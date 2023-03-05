package server.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import server.models.CreateQueueRequest
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import sql_scripts.QueueScriptGenerator.getCreateQueueQuery


object Routes {
  val requestHandler: Route = {
    import FailFastCirceSupport._

    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello, world!"))
      }
    } ~ path("create-queue") {
      post {
        entity(as[CreateQueueRequest]) { requestBody =>
          println("name: "+ requestBody.name)
          println("max_retries: "+ requestBody.max_retries)
          println("visibility_timeout_secs: "+ requestBody.visibility_timeout_secs)
          getCreateQueueQuery(name = requestBody.name, max_retries = requestBody.max_retries, visibility_timeout_secs = requestBody.visibility_timeout_secs)
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"You have told: ${requestBody}"))
        }
      }
    }
  }
}

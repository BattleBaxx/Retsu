package server.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import server.models.{AddMessageRequest, CreateQueueRequest}
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import sql_scripts.CreateQueueQuery.createQueue
import sql_scripts.GetQueueIDQuery.getQueueID
import sql_scripts.InsertMessageQuery.insertMessage

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt


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
          println("name: " + requestBody.name)
          println("max_retries: " + requestBody.max_retries)
          println("visibility_timeout_secs: " + requestBody.visibility_timeout_secs)
          createQueue(name = requestBody.name, max_retries = requestBody.max_retries, visibility_timeout_secs = requestBody.visibility_timeout_secs)
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"You have told: ${requestBody}"))
        }
      }
    } ~ path("add-message") {
      post {
        entity(as[AddMessageRequest]) { requestBody =>

          val queueIdFuture = getQueueID(name=requestBody.name)
          val queueID = Await.result(queueIdFuture, 10.seconds)

          val tableName = requestBody.name+"_messages"

          insertMessage(tableName = tableName, queueID = queueID, body = requestBody.body, retries = requestBody.retries)
          complete(HttpResponse(StatusCodes.OK, entity = s"Message added to queue ${requestBody.name}."))

        }
      }
    }
  }
}

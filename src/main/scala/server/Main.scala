package server

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import server.routes.Routes.requestHandler
import com.typesafe.config.{Config, ConfigFactory}

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext

import service.DatabaseService
import javax.xml.crypto.Data


object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "lowlevel")
    val config: Config = ConfigFactory.load()

    // needed for the future map/flatmap in the end
    implicit val executionContext: ExecutionContext = system.executionContext

    val db = DatabaseService.getDB();

    val bindingFuture = Http().newServerAt(config.getString("server.hostname"), config.getInt("server.port")).bindSync(requestHandler)
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
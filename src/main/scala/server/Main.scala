package server

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import server.routes.Routes.requestHandler
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext

import scala.io.StdIn


object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "lowlevel")
    val config: Config = ConfigFactory.load()

    // needed for the future map/flatmap in the end
    implicit val executionContext: ExecutionContext = system.executionContext


    val bindingFuture = Http().newServerAt(config.getString("server.hostname"), config.getInt("server.port")).bind(requestHandler)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
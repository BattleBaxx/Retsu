package sql_scripts

import service.DatabaseService.getDB
import slick.jdbc.PostgresProfile.api._
import scala.util.{Failure, Success}

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object GetQueueIDQuery {
    def getQueueID(name: String): Future[String] = {

        val db = getDB()
        val getQueueIDQuery = sql"""SELECT id, name FROM queues WHERE name='#$name';"""
        val queueTableFuture = db.run(getQueueIDQuery.as[String])
        queueTableFuture.map(queueIDList =>
            queueIDList.headOption match {
                case Some((id)) => id
                case None => throw new Exception("Queue not found.")
            }
        )

    }
}

package sql_scripts


import service.DatabaseService.getDB
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object ProcessMessageQuery{

    def processMessage(tableName: String, queueID: String, messageID: String): Unit = {
        val uuid = UUID.randomUUID().toString
        val db = getDB()
        val updateQuery = sql"""UPDATE #$tableName SET in_flight = TRUE WHERE id = '#$messageID';"""
        db.run(updateQuery.asUpdate)
        val insertQuery = sql"""INSERT INTO inflight_mesaages (id, queue_id, message_id) VALUES ('#$uuid', '#$queueID', '#$messageID');"""
        val insertFuture = db.run(insertQuery.asUpdate)
        insertFuture.onComplete{
            case Success(_) =>
                println(s"Successfully inserted message in flight table")
            case Failure(exception) =>
                println(s"Failed to insert message in flight table because of $exception")
        }
    }
}
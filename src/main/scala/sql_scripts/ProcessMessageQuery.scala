package sql_scripts


import service.DatabaseService.getDB
import slick.jdbc.PostgresProfile.api._

import java.util.{Optional, UUID}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

object ProcessMessageQuery{

    def processMessage(tableName: String, queueID: String, messageID: String): Option[String] = {
        val uuid = UUID.randomUUID().toString
        val db = getDB()
        val updateQuery = sql"""UPDATE #$tableName SET in_flight = true WHERE id = '#$messageID';"""
        db.run(updateQuery.asUpdate)
        val insertQuery = sql"""INSERT INTO inflight_mesaages (id, queue_id, message_id) VALUES ('#$uuid', '#$queueID', '#$messageID');"""
        val insertFuture = db.run(insertQuery.asUpdate)
        Try {
            val _ = Await.result(insertFuture, Duration.Inf)
            uuid
        } match {
            case Success(value) => Some(value)
            case Failure(exception) =>
                println(s"Failed to insert message in flight table because of $exception")
                None
        }
    }
}
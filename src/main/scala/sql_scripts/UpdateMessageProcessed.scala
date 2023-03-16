package sql_scripts


import service.DatabaseService.getDB
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}


object UpdateMessageProcessed {
    def updateMessageProcessed(tableName: String, messageID: String): Unit = {
        val db = getDB()
        val updateQuery = sql"""UPDATE #$tableName SET processed = TRUE WHERE id = '#$messageID';"""
        val updateFuture = db.run(updateQuery.asUpdate)

        Try {
            val _ = Await.result(updateFuture, Duration.Inf)
        } match {
            case Success(_) =>
                println(s"Update processed in table: $tableName and message: $messageID.")
            case Failure(exception) =>
                println(s"Failed to insert message in flight table because of $exception.")
        }

    }
}

package sql_scripts


import service.DatabaseService.getDB
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object InsertMessageQuery{

    def insertMessage(tableName: String, queueID: String, body: String, retries: Integer): Unit = {
        val uuid = UUID.randomUUID().toString
        val db = getDB()
        val string_retries = retries.toString
        val insertQuery = sql"""INSERT INTO #$tableName (id, queue_id, body, retries) VALUES ('#$uuid', '#$queueID', '#$body', '#$string_retries');"""
        val insertFuture = db.run(insertQuery.asUpdate)
        insertFuture.onComplete{
            case Success(_) =>
                println("Successfully inserted message: " + body + " in table: " + tableName)
            case Failure(exception) =>
                println("Fail to insert message: " + body + " in table: " + tableName + " because of " + exception)
        }
    }
}
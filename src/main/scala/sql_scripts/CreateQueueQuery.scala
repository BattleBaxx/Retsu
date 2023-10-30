package sql_scripts

import service.DatabaseService.getDB
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object CreateQueueQuery {
    def createQueue(name: String, max_retries: Integer, visibility_timeout_secs: Integer): Future[Unit] = {
        val tableNameMessages = s""""public"."${name}_messages""""
        val new_max_retries = max_retries.toString()
        val new_visibility_timeout_secs = visibility_timeout_secs.toString()
        val uuid = UUID.randomUUID().toString
        val db = getDB()

        val insertQuery = sql"""INSERT INTO queues (id, name, max_retries, visibility_timeout_secs) VALUES ('#$uuid', '#$name', '#$new_max_retries', '#$new_visibility_timeout_secs');"""
        val insertResult = db.run(insertQuery.asUpdate)

        val createMessagesTableQuery = sqlu"""CREATE TABLE #$tableNameMessages (id uuid PRIMARY KEY, queue_id uuid NOT NULL, body VARCHAR (50) UNIQUE NOT NULL, created_at TIMESTAMP DEFAULT now(), processed BOOLEAN NOT NULL DEFAULT FALSE, in_flight BOOLEAN NOT NULL DEFAULT FALSE, retries INT DEFAULT 0, CONSTRAINT fk_queues FOREIGN KEY (queue_id) REFERENCES queues(id));"""
        val createTableResult = db.run(createMessagesTableQuery)


        for {
            _ <- insertResult
            _ <- createTableResult
        } yield {
            println("Tables created successfully")
        }
    }
}

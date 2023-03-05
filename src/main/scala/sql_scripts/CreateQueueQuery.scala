package sql_scripts

import service.DatabaseService.getDB
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object QueueScriptGenerator {
    def getCreateQueueQuery(name: String, max_retries: Integer, visibility_timeout_secs: Integer): Future[Unit] = {
        val tableNameMessages = s""""public"."${name}_messages""""
        val tableNameInflightMessages = s""""public"."${name}_inflight_messages""""
        val new_max_retries = max_retries.toString()
        val new_visibility_timeout_secs = visibility_timeout_secs.toString()
        val uuid = UUID.randomUUID().toString
        val db = getDB()

        val insertQuery = sql"""INSERT INTO queues (id, name, max_retries, visibility_timeout_secs) VALUES ('#$uuid', '#$name', '#$new_max_retries', '#$new_visibility_timeout_secs');"""
        val insertResult = db.run(insertQuery.asUpdate)

        val createMessagesTableQuery = sqlu"""CREATE TABLE #$tableNameMessages (id uuid PRIMARY KEY, queue_id uuid NOT NULL, body VARCHAR (50) UNIQUE NOT NULL, created_at TIMESTAMP DEFAULT now(), processed BOOLEAN NOT NULL DEFAULT FALSE, in_flight BOOLEAN NOT NULL DEFAULT FALSE, retries INT NOT NULL, CONSTRAINT fk_queues FOREIGN KEY (queue_id) REFERENCES queues(id));"""
        val createTableResult = db.run(createMessagesTableQuery)

        val createMessagesInflightQuery = sqlu"""CREATE TABLE #$tableNameInflightMessages (message_id uuid PRIMARY KEY, queue_messages uuid NOT NULL, start_time TIMESTAMP DEFAULT now(), end_time TIMESTAMP, end_of_life BOOLEAN DEFAULT FALSE, CONSTRAINT fk_queue_messages FOREIGN KEY (queue_messages) REFERENCES #$tableNameMessages(id));"""
        val createTableResult2 = db.run(createMessagesInflightQuery)

        for {
            _ <- insertResult
            _ <- createTableResult
            _ <- createTableResult2
        } yield {
            println("Tables created successfully")
        }
    }
}

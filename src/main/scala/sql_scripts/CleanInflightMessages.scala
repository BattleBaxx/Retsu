package sql_scripts

import service.DatabaseService.getDB
import slick.jdbc.PostgresProfile.api._
import scala.util.{Failure, Success}

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object CleanInflightMessages {
    def CleanInflightMessages(): Unit = {

        val db = getDB()
        val cur_ts = None;
        val getInflightMessage = sql"""
        SELECT q.name, im.message_id FROM inflight_mesaages im
        JOIN queues q ON im.queue_id = q.id
        WHERE extract(epoch from im.created_at)::bigint + q.visibility_timeout_secs >= $cur_ts;
        """

        val inflightMessagesFuture = db.run(getInflightMessage.as[String])
        inflightMessagesFuture = inflightMessagesFuture.map(r => List(r._1, r._2)..to[List])

        // construct a map to store all messages for a queue, and update the queue 
        inflightMessagesFuture.map { inflightMessagesList =>
        for (inflightMessage <- inflightMessagesList) {
            val queueName = inflightMessage(0)
            val messageID = inflightMessage(1)

            val inflightMessagesForQueue = inflightMessagesByQueue.getOrElse(queueName, Nil)
            val updatedInflightMessagesForQueue = messageID :: inflightMessagesForQueue
            inflightMessagesByQueue(queueName) = updatedInflightMessagesForQueue
        }

          inflightMessagesByQueue.foreach { case (tableName, messageIDs) =>
          val messageIDsString = messageIDs.mkString("', '")
          val updateMessagesTableQuery = s"UPDATE ${tableName}_messages SET processed = true WHERE message_id IN ('$messageIDsString')"
          val updateMessagesTableFuture = db.run(getInflightMessage)
          
          updateMessagesTableFuture.onComplete{
            case Success(_) =>
                println(s"Successfully clean inflight messages...")
            case Failure(exception) =>
                println(s"Failed to clean inflight messages because of $exception")
        }
        }
    }

    }
}

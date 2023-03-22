package sql_scripts

import slick.jdbc.PostgresProfile.api._
import service.DatabaseService.getDB
import scala.util.{Failure, Success}

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.HashMap
import cats.instances.queue


object CleanInflightMessages extends App {
    
    def CleanInflightMessages(db: slick.jdbc.PostgresProfile.backend.DatabaseDef): Unit = {
        val getInflightMessage = sql"""
            SELECT im.id as im_id, q.name, im.message_id FROM inflight_mesaages im
            JOIN queues q ON im.queue_id = q.id
            WHERE extract(epoch from im.created_at)::bigint + (q.visibility_timeout_secs::bigint * 1000) <= extract(epoch from now())::bigint
            AND im.processed = false
            AND im.deleted = false;
        """

        val inflightMessagesFuture = db.run(getInflightMessage.as[(String, String, String)])
        val inflightMessagesListFuture = inflightMessagesFuture.map { vector =>
            vector.map { r =>
            List(r._1.toString, r._2.toString, r._3.toString)
            }.toList
        }

        var inflight_mesaages_ids: List[String] = List();
        val inflightMessagesByQueue = new HashMap[String, List[String]]
        val inflightMessagesByQueueFuture = inflightMessagesListFuture.map { inflightMessagesList =>
            for (inflightMessage <- inflightMessagesList) {
            val imID: String = inflightMessage(0)
            inflight_mesaages_ids = inflight_mesaages_ids.appended(imID)

            val queueName = inflightMessage(1)
            val messageID = inflightMessage(2)
            val inflightMessagesForQueue = inflightMessagesByQueue.getOrElse(queueName, Nil)
            val updatedInflightMessagesForQueue = messageID :: inflightMessagesForQueue
            inflightMessagesByQueue(queueName) = updatedInflightMessagesForQueue
            }
            inflightMessagesByQueue
        }

        inflightMessagesByQueueFuture.flatMap { inflightMessagesByQueue =>
            Future.sequence {
            inflightMessagesByQueue.map { case (tableName, messageIDs) =>
                val messageIDsString = messageIDs.mkString("', '")
                val sqlTableName = tableName + "_messages"
                val updateMessagesTableQuery = sqlu"""
                UPDATE #${sqlTableName} SET processed = true WHERE id IN ('#${messageIDsString}')
                """
                db.run(updateMessagesTableQuery).andThen {
                case Success(_) =>
                    println(s"Successfully cleaned inflight messages from table $tableName")
                case Failure(exception) =>
                    println(s"Failed to clean inflight messages from table $tableName because of $exception")
                }
            }
            }
        }.andThen {
            case Success(_) => {
                if (inflight_mesaages_ids.size != 0){
                    val messageIDsString = inflight_mesaages_ids.mkString("', '")
                    val deleteInflightMessagesQuery = sqlu"""UPDATE inflight_mesaages SET deleted = true WHERE id IN ('#${messageIDsString}')"""
                    db.run(deleteInflightMessagesQuery).andThen {
                    case Success(_) =>
                        println(s"Successfully cleaned inflight messages")
                    case Failure(exception) =>
                        println(s"Failed to clean inflight messages because of $exception")
                    }
                }

            }

            case Failure(exception) =>
                println(s"Failed to clean all inflight messages because of $exception")
            }
        }
}

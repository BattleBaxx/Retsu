package sql_scripts

import service.DatabaseService.getDB
import slick.jdbc.PostgresProfile.api._
import scala.util.{Failure, Success}

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.HashMap
import cats.instances.queue


object CleanInflightMessages extends App {
    print("calling inflight")
    CleanInflightMessages()

    def CleanInflightMessages(): Unit = {
    val db = getDB()
    val getInflightMessage = sql"""
        SELECT q.name, im.message_id FROM inflight_mesaages im
        JOIN queues q ON im.queue_id = q.id
        WHERE extract(epoch from im.created_at)::bigint + (q.visibility_timeout_secs::bigint * 1000) <= extract(epoch from now())::bigint;
    """
    
    println(getInflightMessage)
    println("Running")
    val inflightMessagesFuture = db.run(getInflightMessage.as[(String, String)])
    val inflightMessagesListFuture = inflightMessagesFuture.map { vector =>
        vector.map { r =>
        List(r._1.toString, r._2.toString)
        }.toList
    }

    print(inflightMessagesListFuture)

    val inflightMessagesByQueue = new HashMap[String, List[String]]
    val inflightMessagesByQueueFuture = inflightMessagesListFuture.map { inflightMessagesList =>
        println("Inside Future then")
        
        print(inflightMessagesList)
        for (inflightMessage <- inflightMessagesList) {
        val queueName = inflightMessage(0)
        val messageID = inflightMessage(1)
        val inflightMessagesForQueue = inflightMessagesByQueue.getOrElse(queueName, Nil)
        println(queueName)
        println(messageID)
        val updatedInflightMessagesForQueue = messageID :: inflightMessagesForQueue
        inflightMessagesByQueue(queueName) = updatedInflightMessagesForQueue
        }
        inflightMessagesByQueue
    }

    println(inflightMessagesByQueue)
    inflightMessagesByQueueFuture.flatMap { inflightMessagesByQueue =>
        println("Inside ")
        Future.sequence {
        inflightMessagesByQueue.map { case (tableName, messageIDs) =>
            println(messageIDs)
            val messageIDsString = messageIDs.mkString("', '")
            val sqlTableName = tableName + "_messages"
            val updateMessagesTableQuery = sqlu"""
            UPDATE #${sqlTableName} SET processed = true WHERE id IN ('#${messageIDsString}')
            """
            print(updateMessagesTableQuery.statements)
            print("Hello")
            db.run(updateMessagesTableQuery).andThen {
            case Success(_) =>
                println(s"Successfully cleaned inflight messages from table $tableName")
            case Failure(exception) =>
                println(s"Failed to clean inflight messages from table $tableName because of $exception")
            }
        }
        }
    }.andThen {
        case Success(_) =>
        println(s"Successfully cleaned all inflight messages")
        case Failure(exception) =>
        println(s"Failed to clean all inflight messages because of $exception")
        }
    }
}

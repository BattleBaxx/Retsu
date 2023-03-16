package sql_scripts


import service.DatabaseService.getDB
import slick.jdbc.GetResult
import slick.jdbc.PostgresProfile.api._

import java.time.Instant

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object GetQueueQuery {
    implicit val getMessageResult: GetResult[(String, String, Integer, Integer, Instant)] = GetResult(r =>
        (r.nextString, r.nextString, r.nextInt, r.nextInt, r.nextTimestamp.toInstant)
    )

    def getQueue(queue_id: String): Future[(String, String, Integer, Integer, Instant)] = {
        val db = getDB()
        val getQueueQuery = sql"SELECT * FROM queues WHERE id = '#$queue_id';"
        val getQueueFuture = db.run(getQueueQuery.as[(String, String, Integer, Integer, Instant)])
        getQueueFuture.map(messageIDList =>
            messageIDList.headOption match {
                case Some((id, name, max_retries, visibility_timeout_secs, created_at)) => (id, name, max_retries, visibility_timeout_secs, created_at)
                case None => throw new Exception(s"No queue with id $queue_id.")
            }
        )
    }

}

package sql_scripts
import service.DatabaseService.getDB
import slick.jdbc.GetResult
import slick.jdbc.PostgresProfile.api._

import java.time.Instant

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object GetLatestMessageQuery {
    implicit val getMessageResult = GetResult(r =>
        (r.nextString, r.nextString, r.nextString, r.nextTimestamp.toInstant, r.nextBoolean, r.nextBoolean, r.nextInt)
    )

    def getLatestMessage(tableName: String): Future[(String, String, String, Instant, Boolean, Boolean, Int)] = {
        val db = getDB()
        val getLatestMessageIDQuery = sql"SELECT * FROM #$tableName ORDER BY created_at ASC LIMIT 1;"
        val getLatestMessageIDFuture = db.run(getLatestMessageIDQuery.as[(String, String, String, Instant, Boolean, Boolean, Int)])
        getLatestMessageIDFuture.map(messageIDList =>
            messageIDList.headOption match {
                case Some((id, queue_id, body, created_at, processed, in_flight, retries)) => (id, queue_id, body, created_at, processed, in_flight, retries)
                case None => throw new Exception("No messages in the queue.")
            }
        )
    }
}

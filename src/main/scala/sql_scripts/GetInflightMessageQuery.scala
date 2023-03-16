package sql_scripts


import service.DatabaseService.getDB
import slick.jdbc.GetResult
import slick.jdbc.PostgresProfile.api._

import java.time.Instant

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object GetInflightMessageQuery {
    implicit val getMessageResult: GetResult[(String, String, String, Instant, Boolean)] = GetResult(r =>
        (r.nextString, r.nextString, r.nextString, r.nextTimestamp.toInstant, r.nextBoolean)
    )

    def getInflightMessage(inflight_message_id: String) : Future[(String, String, String, Instant, Boolean)] = {
        val db = getDB()
        val getInflightMessageQuery = sql"SELECT * FROM inflight_mesaages WHERE id = '#$inflight_message_id';"
        val getInflightMessageFuture = db.run(getInflightMessageQuery.as[(String, String, String, Instant, Boolean)])
        getInflightMessageFuture.map(messageIDList =>
            messageIDList.headOption match {
                case Some((id, queue_id, message_id, created_at, processed)) => (id, queue_id, message_id, created_at, processed)
                case None => throw new Exception("No Inflight messages.")
            }
        )
    }

}

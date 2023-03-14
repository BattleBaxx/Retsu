package server.models

case class ProcessMessageResponse(
    message_id: String,
    inflight_message_id: String,
    queueID: String,
    body: String,
)

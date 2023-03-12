package server.models

case class ProcessMessageResponse(
    id: String,
    queueID: String,
    body: String,
)

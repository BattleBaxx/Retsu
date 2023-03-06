package server.models

case class AddMessageRequest (
    name: String,
    body: String,
    retries: Integer
)

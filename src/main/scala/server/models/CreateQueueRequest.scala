package server.models

case class CreateQueueRequest(
    name: String,
    max_retries: Integer,
    visibility_timeout_secs: Integer,
)
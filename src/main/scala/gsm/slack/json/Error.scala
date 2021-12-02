package gsm.slack.json

import io.circe.generic.JsonCodec

@JsonCodec case class Error(error: String, ok: String, responseMetadata: ResponseMetaData)

@JsonCodec case class ResponseMetaData(messages: List[String])

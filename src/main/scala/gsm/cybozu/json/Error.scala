package gsm.cybozu.json

import io.circe.generic.JsonCodec

@JsonCodec final case class Error(error: ErrorDetail)

@JsonCodec final case class ErrorDetail(errorCode: String, message: String, cause: String)

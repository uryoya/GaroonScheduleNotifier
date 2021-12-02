package gsm.cybozu.json

import io.circe.generic.JsonCodec

@JsonCodec final case class Schedule(events: List[Event], hasNext: Boolean)

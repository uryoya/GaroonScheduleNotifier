package gsm.cybozu.json

import java.time.ZonedDateTime
import io.circe.generic.JsonCodec

/** スケジュールオブジェクト
  * https://developer.cybozu.io/hc/ja/articles/115005314266
  */
@JsonCodec final case class Event(
    // 基本プロパティ
    id: String,
    eventType: EventType,
    eventMenu: String,
    subject: String,
    notes: String,
    // 開始と週雨量に関するプロパティ
    start: Option[Start],
    end: Option[End],
    isAllDay: Boolean
)

@JsonCodec final case class Start(dateTime: ZonedDateTime)
@JsonCodec final case class End(dateTime: ZonedDateTime)

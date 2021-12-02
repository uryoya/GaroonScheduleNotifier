package gsm.cybozu.json

import enumeratum._
import enumeratum.EnumEntry._
import enumeratum.values._

sealed abstract class EventType(override val entryName: String) extends EnumEntry with Product with Serializable

object EventType extends Enum[EventType] with CirceEnum[EventType] {

  val values: IndexedSeq[EventType] = findValues

  final case object Regular extends EventType("REGULAR") // 通常
  final case object Repeating extends EventType("REPEATING") // 繰り返し
  final case object AllDay extends EventType("ALL_DAY") // 全日
}

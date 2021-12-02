package gsm.cybozu.json

import gsm.cybozu.json.EventType
import io.circe.parser.decode
import io.circe.syntax._

class EventTypeSuite extends munit.FunSuite {
  import gsm.cybozu.json.EventType._

  test("JSONからEventTypeにデコードできる") {
    assertEquals(decode[EventType]("\"REGULAR\""), Right(Regular))
    assertEquals(decode[EventType]("\"REPEATING\""), Right(Repeating))
    assertEquals(decode[EventType]("\"ALL_DAY\""), Right(AllDay))
  }

  test("EventTypeからJSONにエンコードできる") {
    assertEquals((Regular: EventType).asJson.noSpaces, "\"REGULAR\"")
    assertEquals((Repeating: EventType).asJson.noSpaces, "\"REPEATING\"")
    assertEquals((AllDay: EventType).asJson.noSpaces, "\"ALL_DAY\"")
  }
}

package gsm

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent

object TodaysScheduleLambda extends RequestHandler[ScheduledEvent, String] {
  override def handleRequest(event: ScheduledEvent, context: Context): String = {
    val logger = context.getLogger
    val app = new TodaysSchedule()
    app.run
    "200 OK"
  }
}

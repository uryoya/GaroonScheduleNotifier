package gsm

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent

object GaroonScheduleNotifierLambda extends RequestHandler[ScheduledEvent, String] {
  override def handleRequest(event: ScheduledEvent, context: Context): String = {
    implicit val actorSystem: ActorSystem[_] = ActorSystem(Behaviors.empty, "SingleRequest")
    val app = new GaroonScheduleNotifier()
    app.run
    actorSystem.terminate()
    "200 OK"
  }
}

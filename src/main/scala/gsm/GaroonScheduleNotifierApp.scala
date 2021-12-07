package gsm

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

object GaroonScheduleNotifierApp extends App {
  implicit val actorSystem: ActorSystem[_] = ActorSystem(Behaviors.empty, "SingleRequest")
  val app = new GaroonScheduleNotifier()
  app.run
  actorSystem.terminate()
}

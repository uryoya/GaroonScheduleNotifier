package gsm

import java.time.format.DateTimeFormatter
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent._
import scala.concurrent.duration._
import gsm.config.config
import gsm.cybozu._
import gsm.slack.SlackWebhookAPI
import io.circe.generic.JsonCodec
import io.circe.syntax._

object GaroonScheduleNotifier {
  @JsonCodec final case class SlackWebhookMessage(plan: String, detail: String, when: String)
}

class GaroonScheduleNotifier(implicit val actorSystem: ActorSystem[_]) {
  private[this] val logger = actorSystem.log
  private[this] implicit val ec: ExecutionContextExecutor = actorSystem.executionContext

  def run: Unit = {
    val account = GaroonAPI.GaroonAccountCredential(config.cybozu.loginName, config.cybozu.password)
    val system = GaroonAPI.GaroonSystemCredential(config.cybozu.host, config.cybozu.installId)
    val garoonApi = new GaroonAPI(system, account)
    val slackApi = new SlackWebhookAPI(config.slack.webhookUrl)

    val now = java.time.ZonedDateTime.now
    val today = now.toLocalDate
    val zone = now.getZone
    val start = java.time.ZonedDateTime.of(today.atTime(java.time.LocalTime.MIN), zone)
    val end = java.time.ZonedDateTime.of(today.atTime(java.time.LocalTime.MAX), zone)
    logger.info(s"get schedule for $start ~ $end")

    val result = for {
      events <- garoonApi.getEvents(start, end).flatMap {
        case Right(events) => Future.successful(events)
        case Left(error)   => println(error); Future.failed(error)
      }
      messages = render(events)
      _ <- Future.sequence(messages.map { message =>
        logger.info(s"send message to slack: $message")
        slackApi.post(message)
      })
    } yield ()

    Await.result(result, 100.seconds)
  }

  private[this] def render(events: List[json.Event]): List[String] = {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    if (events.isEmpty)
      List(GaroonScheduleNotifier.SlackWebhookMessage("", "", "今日の予定はありません").asJson.noSpaces)
    else
      events
        .sortBy(_.start.map(_.dateTime.toEpochSecond).getOrElse(0: Long)) // FIXME なんかうまくいってない
        .map { event =>
          val when = event.eventType match {
            case cybozu.json.EventType.AllDay => "全日"
            case _ =>
              val start = event.start.map(_.dateTime.format(formatter)).getOrElse("")
              val end = event.end.map(_.dateTime.format(formatter)).getOrElse("")
              s"$start ~ $end"
          }
          GaroonScheduleNotifier.SlackWebhookMessage(event.eventMenu, event.subject, when).asJson.noSpaces
        }
  }
}

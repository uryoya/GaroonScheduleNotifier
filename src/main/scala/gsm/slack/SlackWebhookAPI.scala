package gsm.slack

import scala.concurrent._
import scala.concurrent.duration._
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

object SlackWebhookAPI {
  // Exception
  sealed abstract class SlackWebhookAPIException(message: String, cause: Throwable = null)
      extends Exception(message, cause)
  class APIFailedException(message: String, cause: Throwable = null) extends SlackWebhookAPIException(message, cause)
  class JSONDecodeException(message: String, cause: Throwable = null) extends SlackWebhookAPIException(message, cause)
}

class SlackWebhookAPI(webhookUrl: String) {
  import io.circe.parser.decode
  import SlackWebhookAPI._

  def post(
      message: String
  )(implicit actorSystem: ActorSystem[_]): Future[Either[SlackWebhookAPI.SlackWebhookAPIException, Unit]] = {
    implicit val ec = actorSystem.executionContext
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = webhookUrl,
      entity = HttpEntity(ContentTypes.`application/json`, message)
    )

    Http()
      .singleRequest(request)
      .flatMap { response =>
        response.entity
          .toStrict(10.seconds)
          .map(_.data.utf8String)
          .map(body => (response, body))
      }
      .map {
        case (HttpResponse(StatusCodes.OK, _, _, _), _) => Right(())
        case (response, body) =>
          decode[json.Error](body) match {
            case Right(error) =>
              Left(new APIFailedException(s"HTTP ${response.status}, error message: $error"))
            case Left(e) =>
              Left(new JSONDecodeException(s"HTTP ${response.status} and error message decode failed", e))
          }
      }
  }
}

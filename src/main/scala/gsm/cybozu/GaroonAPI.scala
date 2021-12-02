package gsm.cybozu

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import gsm.cybozu.GaroonAPI.{APIFailedException, JSONDecodeException}

object GaroonAPI {
  case class GaroonAccountCredential(loginName: String, password: String) {
    def xCybozuAuthorization: String = {
      val base64Encoder = java.util.Base64.getEncoder
      base64Encoder.encodeToString(s"$loginName:$password".getBytes)
    }
  }

  case class GaroonSystemCredential(host: String, installId: String)

  // Exception
  sealed abstract class GaroonAPIException(message: String, cause: Throwable = null) extends Exception(message, cause)
  class APIFailedException(message: String, cause: Throwable = null) extends GaroonAPIException(message, cause)
  class JSONDecodeException(message: String, cause: Throwable = null) extends GaroonAPIException(message, cause)
}

class GaroonAPI(
    systemCredential: GaroonAPI.GaroonSystemCredential,
    accountCredential: GaroonAPI.GaroonAccountCredential
) {
  import io.circe.parser.decode
  import GaroonAPI._

  def getEvents(
      rangeStart: ZonedDateTime,
      rangeEnd: ZonedDateTime
  )(implicit actorSystem: ActorSystem[_]): Future[Either[GaroonAPIException, List[json.Event]]] = {
    implicit val ec = actorSystem.executionContext

    val header = headers.RawHeader("X-Cybozu-Authorization", accountCredential.xCybozuAuthorization)
    val uri = Uri(
      s"https://${systemCredential.host}/cgi-bin/${systemCredential.installId}/grn.cgi/api/v1/schedule/events"
    )
      .withQuery(
        Uri.Query(
          "rangeStart" -> rangeStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
          "rangeEnd" -> rangeEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
      )
    val request =
      HttpRequest(
        method = HttpMethods.GET,
        headers = List(header),
        uri = uri
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
        case (HttpResponse(StatusCodes.OK, _, _, _), body) =>
          decode[json.Schedule](body) match {
            case Right(schedule) =>
              Right(schedule.events)
            case Left(e) =>
              println(body)
              Left(new JSONDecodeException(s"API request was success but json decode failed: ${e.getMessage}", e))
          }
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

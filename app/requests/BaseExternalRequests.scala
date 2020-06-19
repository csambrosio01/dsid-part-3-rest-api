package requests

import javax.inject.Inject
import play.api.Logging
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

abstract class BaseExternalRequests @Inject()(
                                               ws: WSClient
                                             )
                                             (
                                               implicit ec: ExecutionContext
                                             )
  extends Logging {

  val service = "external"

  def toResult()
              (
                result: WSResponse
              ): JsValue = {
    result.status match {
      case 200 | 201 => result.json
      case 400 =>
        logger.error(s"$service service return bad request - ${result.body}")
        val message = if (result.body.isEmpty) s"acquirer.$service.bad_request" else result.body
        throw new Exception(message)
      case 404 =>
        logger.warn(s"$service service return resource not found - ${result.body}")
        val message = if (result.body.isEmpty) s"acquirer.$service.url.not_found" else result.body
        throw new Exception(message)
      case 500 =>
        logger.error(s"$service service return internal server error - ${result.body}")
        val message = if (result.body.isEmpty) s"acquirer.$service.internal_server_error" else result.body
        throw new Exception(message)
      case _ =>
        logger.error(s"$service service return invalid status - ${result.status}: ${result.body}")
        val message = if (result.body.isEmpty) s"acquirer.$service.${result.status}" else result.body
        throw new Exception(message)
    }
  }

  protected def prepareFullRequest(
                                    url: String,
                                    timeout: Long
                                  ): WSRequest = {
    ws.url(s"$url")
      .withRequestTimeout(Duration.apply(timeout, java.util.concurrent.TimeUnit.SECONDS))
  }

  def verifyResult(request: Future[WSResponse]): Future[JsValue] = {
    request.transform(
      toResult(),
      b => {
        logger.error(s"$service service return internal server error - ${b.getMessage}", b)
        throw new Exception(b.getMessage)
      }
    )
  }
}

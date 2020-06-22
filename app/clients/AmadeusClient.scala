package clients

import configurations.ServiceConfig
import exception.AccessTokenException
import javax.inject.Inject
import play.api.libs.ws.WSClient
import requests.BaseExternalRequests

import scala.concurrent.{ExecutionContext, Future}

class AmadeusClient @Inject()(
                               serviceConfig: ServiceConfig,
                               ws: WSClient
                             )
                             (
                               implicit ec: ExecutionContext
                             ) extends BaseExternalRequests(ws) {

  val url: String = serviceConfig.amadeusUrl

  def getAccessToken: Future[String] = {
    val response = prepareFullRequest(url + "/v1/security/oauth2/token", 10)
      .addHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .post("grant_type=client_credentials&client_id=" + serviceConfig.amadeusApiKey + "&client_secret=" + serviceConfig.amadeusApiSecret)

    verifyResult(response)
      .map { result =>
        val accessToken = (result \ "access_token").as[String]
        s"Bearer $accessToken"
      }
      .recover {
        case _ => throw AccessTokenException("amadeus.access_token.error")
      }
  }
}

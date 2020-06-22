package services

import clients.AmadeusClient
import exception.{AccessTokenException, NotFoundException}
import javax.inject.Inject
import model.FlightDestination._
import model.{FlightDestination, FlightDestinationResult}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}
import requests.BaseExternalRequests

import scala.concurrent.{ExecutionContext, Future}

class FlightService @Inject()(
                               client: AmadeusClient,
                               ws: WSClient
                             )
                             (
                               implicit ec: ExecutionContext
                             )
  extends BaseExternalRequests(ws) {

  override val service = "amadeus"

  private def prepareAmadeusRequest(url: String): Future[WSRequest] = {
    client.getAccessToken
      .map { accessToken =>
        super.prepareFullRequest(client.url + url, 10 * 60)
          .addHttpHeaders(play.api.http.HeaderNames.AUTHORIZATION -> accessToken)
      }
  }

  def searchFlightsDestinations(
                                 origin: String,
                                 departureDate: Option[String],
                                 oneWay: Option[Boolean],
                                 duration: Option[String],
                                 nonStop: Option[Boolean],
                                 maxPrice: Option[Int],
                                 viewBy: Option[String]
                               ): Future[Seq[FlightDestination]] = {
    val response = prepareAmadeusRequest("/shopping/flight-destinations")
      .flatMap { request =>
        val parameters = Seq("origin" -> Option(origin),
          "departureDate" -> departureDate,
          "oneWay" -> oneWay,
          "duration" -> duration,
          "nonStop" -> nonStop,
          "maxPrice" -> maxPrice,
          "viewBy" -> viewBy
        )
          .collect {
            case (key, Some(value)) => key -> value.toString
          }

        request
          .addQueryStringParameters(parameters: _*)
          .get()
      }

    verifyResult(response)
      .map { result =>
        Json.fromJson[FlightDestinationResult](result).get
      }
      .map(_.data)
      .recover {
        case e: AccessTokenException => throw e
        case _ => throw NotFoundException("amadeus.flight_destination.not_found")
      }
  }
}

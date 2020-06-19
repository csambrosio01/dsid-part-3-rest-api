package services

import clients.AmadeusClient
import exception.{AccessTokenException, NotFoundException}
import javax.inject.Inject
import model.{FlightDestination, FlightDestinationResult}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import requests.BaseExternalRequests
import model.FlightDestination._

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

  def searchFlightsDestinations(
                                 origin: String,
                                 departureDate: Option[String],
                                 oneWay: Option[Boolean],
                                 duration: Option[String],
                                 nonStop: Option[Boolean],
                                 maxPrice: Option[Int],
                                 viewBy: Option[String]
                               ): Future[Seq[FlightDestination]] = {
    val response = client.getAccessToken
      .map { accessToken =>
        prepareFullRequest(client.url + "/shopping/flight-destinations", 10 * 60)
          .addQueryStringParameters("origin" -> origin)
          .addHttpHeaders(play.api.http.HeaderNames.AUTHORIZATION -> accessToken)
      }
      .flatMap { request =>
        departureDate.fold(request) { departureDate => request.addQueryStringParameters("departureDate" -> departureDate) }
        oneWay.fold(request) { oneWay => request.addQueryStringParameters("oneWay" -> oneWay.toString) }
        duration.fold(request) { duration => request.addQueryStringParameters("duration" -> duration) }
        nonStop.fold(request) { nonStop => request.addQueryStringParameters("nonStop" -> nonStop.toString) }
        maxPrice.fold(request) { maxPrice => request.addQueryStringParameters("maxPrice" -> maxPrice.toString) }
        viewBy.fold(request) { viewBy => request.addQueryStringParameters("viewBy" -> viewBy) }
        request.get()
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

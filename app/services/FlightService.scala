package services

import clients.AmadeusClient
import exception.{AccessTokenException, NotFoundException}
import javax.inject.Inject
import model.FlightDestination._
import model.amadeus.FlightOfferSearch._
import model.amadeus._
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
    val response = prepareAmadeusRequest("/v1/shopping/flight-destinations")
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
        case _ => throw NotFoundException("amadeus.flight_destination.not_found", origin)
      }
  }

  def searchFlightOffers(
                          flightOfferRequest: FlightOfferRequest
                        ): Future[Seq[FlightOfferSearch]] = {
    val response = prepareAmadeusRequest("/v2/shopping/flight-offers")
      .flatMap { request =>
        val parameters = Seq(
          "originLocationCode" -> Option(flightOfferRequest.originLocationCode),
          "destinationLocationCode" -> Option(flightOfferRequest.destinationLocationCode),
          "departureDate" -> Option(flightOfferRequest.departureDate.toString),
          "adults" -> Option(flightOfferRequest.adults.toString),
          "returnDate" -> flightOfferRequest.returnDate,
          "children" -> flightOfferRequest.children,
          "infants" -> flightOfferRequest.infants,
          "travelClass" -> flightOfferRequest.travelClass,
          "includedAirlineCodes" -> flightOfferRequest.includedAirlineCodes,
          "excludedAirlineCodes" -> flightOfferRequest.excludedAirlineCodes,
          "nonStop" -> flightOfferRequest.nonStop,
          "currencyCode" -> flightOfferRequest.currencyCode,
          "maxPrice" -> flightOfferRequest.maxPrice,
          "max" -> flightOfferRequest.max
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
        Json.fromJson[FlightOfferSearchResult](result).get
      }
      .map(_.data)
      .recover {
        case e: AccessTokenException => throw e
        case _ => throw NotFoundException("amadeus.flight_offers.not_found")
      }
  }
}

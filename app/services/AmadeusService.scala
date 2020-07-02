package services

import java.text.SimpleDateFormat
import java.util.Date

import clients.AmadeusClient
import exception.{AccessTokenException, NotFoundException}
import javax.inject.Inject
import model.amadeus.Location._
import model.amadeus._
import model.amadeus.flight.FlightDestination._
import model.amadeus.flight.FlightOfferSearch._
import model.amadeus.flight._
import model.amadeus.hotel.HotelOffers._
import model.amadeus.hotel.{HotelOfferSearchRequest, HotelOffers, HotelOffersResult}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}
import requests.BaseExternalRequests

import scala.concurrent.{ExecutionContext, Future}

class AmadeusService @Inject()(
                                client: AmadeusClient,
                                ws: WSClient
                              )
                              (
                                implicit ec: ExecutionContext
                              )
  extends BaseExternalRequests(ws) {

  override val service = "amadeus"
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val oneDayInMillis = 24 * 60 * 60 * 1000

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

  def searchFlightOffersHighlights: Future[Seq[FlightOfferSearch]] = {
    val flightOfferRequestNYCToBOS = FlightOfferRequest(
      originLocationCode = "NYC",
      destinationLocationCode = "BOS",
      departureDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      adults = 1,
      max = Some(1)
    )

    val flightOfferRequestAMSToMAD = FlightOfferRequest(
      originLocationCode = "AMS",
      destinationLocationCode = "MAD",
      departureDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      adults = 1,
      max = Some(1)
    )

    val flightOfferRequestBRUToLHR = FlightOfferRequest(
      originLocationCode = "BRU",
      destinationLocationCode = "LHR",
      departureDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      adults = 1,
      max = Some(1)
    )

    searchFlightOffers(flightOfferRequestNYCToBOS)
      .flatMap { resultNYCToBOS =>
        searchFlightOffers(flightOfferRequestAMSToMAD)
          .flatMap { resultAMSToMAD =>
            searchFlightOffers(flightOfferRequestBRUToLHR)
              .map { resultBRUToLHR =>
                resultNYCToBOS ++ resultBRUToLHR ++ resultAMSToMAD
              }
          }
      }
  }

  def searchFlightOffersHighlightsAirPage: Future[Seq[FlightOfferSearch]] = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val flightOfferRequestNYCToWAS = FlightOfferRequest(
      originLocationCode = "NYC",
      destinationLocationCode = "WAS",
      departureDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      adults = 1,
      max = Some(1)
    )

    val flightOfferRequestAMSToLHR = FlightOfferRequest(
      originLocationCode = "AMS",
      destinationLocationCode = "LHR",
      departureDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      adults = 1,
      max = Some(1)
    )

    val flightOfferRequestBRUToDAL = FlightOfferRequest(
      originLocationCode = "BRU",
      destinationLocationCode = "PAR",
      departureDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      adults = 1,
      max = Some(1)
    )

    val flightOfferRequestLASToBRU = FlightOfferRequest(
      originLocationCode = "LAS",
      destinationLocationCode = "BRU",
      departureDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      adults = 1,
      max = Some(1)
    )

    val flightOfferRequestORDToHND = FlightOfferRequest(
      originLocationCode = "ORD",
      destinationLocationCode = "HND",
      departureDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      adults = 1,
      max = Some(1)
    )

    val flightOfferRequestATLToLUR = FlightOfferRequest(
      originLocationCode = "ATL",
      destinationLocationCode = "SEA",
      departureDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      adults = 1,
      max = Some(1)
    )

    searchFlightOffers(flightOfferRequestNYCToWAS)
      .flatMap { resultNYCToWAS =>
        searchFlightOffers(flightOfferRequestAMSToLHR)
          .flatMap { resultAMSToLHR =>
            searchFlightOffers(flightOfferRequestBRUToDAL)
              .flatMap { resultBRUToDAL =>
                searchFlightOffers(flightOfferRequestLASToBRU)
                  .flatMap { resultLASToBRU =>
                    searchFlightOffers(flightOfferRequestORDToHND)
                      .flatMap { resultORDToHND =>
                        searchFlightOffers(flightOfferRequestATLToLUR)
                          .map { resultATLToLUR =>
                            resultNYCToWAS ++ resultAMSToLHR ++ resultBRUToDAL ++ resultLASToBRU ++ resultORDToHND ++ resultATLToLUR
                          }
                      }
                  }
              }
          }
      }
  }

  def searchAirportOrCity(airportOrCity: String): Future[Seq[Location]] = {
    val response = prepareAmadeusRequest("/v1/reference-data/locations")
      .flatMap { request =>
        request
          .addQueryStringParameters(
            "subType" -> "AIRPORT,CITY",
            "keyword" -> airportOrCity
          )
          .get()
      }

    verifyResult(response)
      .map { result =>
        Json.fromJson[LocationResult](result).get
      }
      .map(_.data)
      .recover {
        case e: AccessTokenException => throw e
        case _ => throw NotFoundException("amadeus.airportOrCity.not_found")
      }
  }

  def getHotelOffers(hotelSearchRequest: HotelOfferSearchRequest): Future[Seq[HotelOffers]] = {
    val response = prepareAmadeusRequest("/v2/shopping/hotel-offers")
      .flatMap { request =>
        val parameters = Seq(
          "cityCode" -> hotelSearchRequest.cityCode,
          "checkInDate" -> hotelSearchRequest.checkInDate,
          "checkOutDate" -> hotelSearchRequest.checkOutDate,
          "roomQuantity" -> hotelSearchRequest.roomQuantity,
          "adults" -> hotelSearchRequest.adults,
          "radius" -> hotelSearchRequest.radius,
          "priceRange" -> hotelSearchRequest.priceRange,
          "ratings" -> hotelSearchRequest.ratings,
          "currency" -> "USD",
          "lang" -> "pt-BR"
        )
          .collect {
            case (key, value) => key -> value.toString
          }

        request
          .addQueryStringParameters(parameters: _*)
          .get()
      }

    verifyResult(response)
      .map { result =>
        Json.fromJson[HotelOffersResult](result).get
      }
      .map(_.data)
      .recover {
        case e: AccessTokenException => throw e
        case _ => throw NotFoundException("amadeus.hotel_offers.not_found")
      }
  }

  def searchHotelOffersHighlights: Future[Seq[HotelOffers]] = {
    val hotelOfferRequestNYC = HotelOfferSearchRequest(
      cityCode = "NYC",
      checkInDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      checkOutDate = dateFormat.format(new Date(System.currentTimeMillis() + (2 * oneDayInMillis))),
      ratings = 4
    )

    val hotelOfferRequestLAS = HotelOfferSearchRequest(
      cityCode = "CHI",
      checkInDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      checkOutDate = dateFormat.format(new Date(System.currentTimeMillis() + (2 * oneDayInMillis))),
      ratings = 4
    )

    val hotelOfferRequestLON = HotelOfferSearchRequest(
      cityCode = "LON",
      checkInDate = dateFormat.format(new Date(System.currentTimeMillis() + oneDayInMillis)),
      checkOutDate = dateFormat.format(new Date(System.currentTimeMillis() + (2 * oneDayInMillis))),
      ratings = 2
    )

    getHotelOffers(hotelOfferRequestNYC)
      .flatMap { resultNYC =>
        getHotelOffers(hotelOfferRequestLAS)
          .flatMap { resultLAS =>
            getHotelOffers(hotelOfferRequestLON)
              .map { resultLON =>
                resultNYC ++ resultLAS ++ resultLON
              }
          }
      }
  }
}

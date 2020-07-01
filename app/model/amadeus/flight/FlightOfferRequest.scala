package model.amadeus.flight

import play.api.libs.json.{Format, Json, OFormat}
import utils.EnumFormatUtils

object TravelClass extends Enumeration {
  val ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST = Value
}

case class FlightOfferRequest(
                               originLocationCode: String,
                               destinationLocationCode: String,
                               departureDate: String,
                               adults: Int,
                               returnDate: Option[String] = None,
                               children: Option[Int] = None,
                               infants: Option[Int] = None,
                               travelClass: Option[TravelClass.Value] = None,
                               includedAirlineCodes: Option[String] = None,
                               excludedAirlineCodes: Option[String] = None,
                               nonStop: Option[Boolean] = None,
                               currencyCode: Option[String] = None,
                               maxPrice: Option[Int] = None,
                               max: Option[Int] = None
                             )

object FlightOfferRequest {
  implicit val travelClassFormat: Format[TravelClass.Value] = EnumFormatUtils.enumFormat(TravelClass)
  implicit val flightOfferRequestFormat: OFormat[FlightOfferRequest] = Json.format[FlightOfferRequest]
}

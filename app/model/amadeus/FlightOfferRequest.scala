package model.amadeus

import play.api.libs.json.{Format, Json, OFormat}
import utils.EnumFormatUtils

object TravelClass extends Enumeration {
  val ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST = Value
}

case class FlightOfferRequest(
                               originLocationCode: String,
                               destinationLocationCode: String,
                               departureDate: String,
                               returnDate: Option[String],
                               adults: Int,
                               children: Option[Int],
                               infants: Option[Int],
                               travelClass: Option[TravelClass.Value],
                               includedAirlineCodes: Option[String],
                               excludedAirlineCodes: Option[String],
                               nonStop: Option[Boolean],
                               currencyCode: Option[String],
                               maxPrice: Option[Int],
                               max: Option[Int]
                             )

object FlightOfferRequest {
  implicit val travelClassFormat: Format[TravelClass.Value] = EnumFormatUtils.enumFormat(TravelClass)
  implicit val flightOfferRequestFormat: OFormat[FlightOfferRequest] = Json.format[FlightOfferRequest]
}

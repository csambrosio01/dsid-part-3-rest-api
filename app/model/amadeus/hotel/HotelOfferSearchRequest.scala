package model.amadeus.hotel

import play.api.libs.json.{Json, OFormat}

case class HotelOfferSearchRequest(
                                    cityCode: String,
                                    checkInDate: String,
                                    checkOutDate: String,
                                    roomQuantity: Int = 1,
                                    adults: Int = 1,
                                    radius: Int = 5,
                                    ratings: Option[Seq[Int]],
                                    priceRange: Option[String]
                                  )

object HotelOfferSearchRequest {
  implicit val hotelOfferSearchRequestFormat: OFormat[HotelOfferSearchRequest] = Json.format[HotelOfferSearchRequest]
}

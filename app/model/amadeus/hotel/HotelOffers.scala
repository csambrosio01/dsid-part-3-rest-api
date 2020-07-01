package model.amadeus.hotel

import play.api.libs.json.{Json, OFormat}

case class HotelOffersResult(data: Seq[HotelOffers])

case class HotelOffers(
                        `type`: Option[String],
                        available: Option[Boolean],
                        hotel: Hotel,
                        offers: Seq[HotelOffer]
                      )

case class Hotel(
                  `type`: Option[String],
                  hotelId: Option[String],
                  name: Option[String],
                  rating: Option[String],
                  description: Option[TextWithLanguageType],
                  media: Option[Seq[HotelPhoto]],
                  cityCode: Option[String],
                  hotelDistance: Option[HotelDistance],
                  address: Option[AddressHotel]
                )

case class HotelPhoto(
                       uri: Option[String],
                       category: Option[String]
                     )

case class HotelDistance(
                          distance: Option[Double],
                          distanceUnit: Option[String]
                        )

case class AddressHotel(
                         lines: Option[Seq[String]],
                         postalCOde: Option[String],
                         cityName: Option[String],
                         countryCode: Option[String],
                         stateCode: Option[String]
                       )

case class HotelOffer(
                        `type`: Option[String],
                        id: Option[String],
                        checkInDate: Option[String],
                        checkOutDate: Option[String],
                        roomQuantity: Option[String],
                        description: Option[TextWithLanguageType],
                        boardType: Option[String],
                        price: HotelPrice
                     )

case class TextWithLanguageType(
                                 lang: Option[String],
                                 text: Option[String]
                               )


case class HotelPrice(
                       total: String
                     )

object HotelOffers {
  implicit val hotelPriceFormat: OFormat[HotelPrice] = Json.format[HotelPrice]
  implicit val textWithLanguageTypeFormat: OFormat[TextWithLanguageType] = Json.format[TextWithLanguageType]
  implicit val hotelOfferFormat: OFormat[HotelOffer] = Json.format[HotelOffer]
  implicit val addressHotelFormat: OFormat[AddressHotel] = Json.format[AddressHotel]
  implicit val hotelDistanceFormat: OFormat[HotelDistance] = Json.format[HotelDistance]
  implicit val hotelPhotoFormat: OFormat[HotelPhoto] = Json.format[HotelPhoto]
  implicit val hotelFormat: OFormat[Hotel] = Json.format[Hotel]
  implicit val hotelOffersFormat: OFormat[HotelOffers] = Json.format[HotelOffers]
  implicit val hotelOffersResultFormat: OFormat[HotelOffersResult] = Json.format[HotelOffersResult]
}

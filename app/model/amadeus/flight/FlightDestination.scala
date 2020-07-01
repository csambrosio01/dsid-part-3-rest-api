package model.amadeus.flight

import java.util.Date

import play.api.libs.json.{Json, OFormat}

case class FlightDestinationResult(data: Seq[FlightDestination])

case class FlightDestination(
                              `type`: String,
                              origin: String,
                              destination: String,
                              departureDate: Date,
                              returnDate: Date,
                              price: Price,
                              links: FlightLinks
                            )

case class Price(
                  total: String
                )

case class FlightLinks(
                        flightDates: String,
                        flightOffers: String
                      )

object FlightDestination {
  implicit val flightLinksFormat: OFormat[FlightLinks] = Json.format[FlightLinks]
  implicit val priceFormat: OFormat[Price] = Json.format[Price]
  implicit val flightDestinationFormat: OFormat[FlightDestination] = Json.format[FlightDestination]
  implicit val flightDestinationResultFormat: OFormat[FlightDestinationResult] = Json.format[FlightDestinationResult]
}

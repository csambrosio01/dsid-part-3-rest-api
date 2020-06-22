package model.amadeus

import play.api.libs.json.{Json, OFormat}

case class FlightOfferSearchResult(
                                    data: Seq[FlightOfferSearch]
                                  )

case class FlightOfferSearch(
                              `type`: String,
                              id: String,
                              source: String,
                              instantTicketingRequired: Boolean,
                              nonHomogeneous: Boolean,
                              oneWay: Boolean,
                              lastTicketingDate: String,
                              numberOfBookableSeats: Int,
                              itineraries: Seq[Itinerary],
                              price: SearchPrice,
                              pricingOptions: PricingOptions,
                              validatingAirlineCodes: Seq[String],
                              travelerPricings: Seq[TravelerPricing],
                              choiceProbability: Option[String]
                            )

case class Itinerary(
                      duration: String,
                      segments: Seq[Segment]
                    )

case class Segment(
                    departure: AirportInfo,
                    arrival: AirportInfo,
                    carrierCode: String,
                    number: String,
                    aircraft: Aircraft,
                    operating: Option[Operating],
                    duration: String,
                    id: String,
                    numberOfStops: Int,
                    blacklistedInEU: Boolean,
                    co2Emissions: Option[Co2Emissions]
                  )

case class Operating(
                      carrierCode: String
                    )

case class AirportInfo(
                        iataCode: String,
                        terminal: Option[String],
                        at: String
                      )

case class Aircraft(
                     code: String
                   )

case class Co2Emissions(
                         weight: Int,
                         weightUnit: String,
                         cabin: String
                       )

case class SearchPrice(
                        currency: String,
                        total: String,
                        base: String,
                        fees: Option[Seq[Fee]],
                        grandTotal: Option[String]
                      )

case class Fee(
                amount: String,
                `type`: String
              )

case class PricingOptions(
                           includedCheckedBagsOnly: Boolean,
                           fareType: Seq[String],
                           corporateCodes: Option[Seq[String]],
                           refundableFare: Option[Boolean],
                           noRestrictionFare: Option[Boolean],
                           noPenaltyFare: Option[Boolean]
                         )

case class TravelerPricing(
                            travelerId: String,
                            fareOption: String,
                            travelerType: String,
                            price: SearchPrice,
                            fareDetailsBySegment: Seq[FareDetailsBySegment]
                          )

case class FareDetailsBySegment(
                                 segmentId: String,
                                 cabin: String,
                                 fareBasis: String,
                                 `class`: String,
                                 includedCheckedBags: IncludedCheckedBags
                               )

case class IncludedCheckedBags(
                                weight: Int,
                                weightUnit: String
                              )

object FlightOfferSearch {
  implicit val includedCheckedBagsFormat: OFormat[IncludedCheckedBags] = Json.format[IncludedCheckedBags]
  implicit val co2EmissionsFormat: OFormat[Co2Emissions] = Json.format[Co2Emissions]
  implicit val operatingFormat: OFormat[Operating] = Json.format[Operating]
  implicit val aircraftFormat: OFormat[Aircraft] = Json.format[Aircraft]
  implicit val airportInfoFormat: OFormat[AirportInfo] = Json.format[AirportInfo]
  implicit val fareDetailsBySegmentFormat: OFormat[FareDetailsBySegment] = Json.format[FareDetailsBySegment]
  implicit val segmentFormat: OFormat[Segment] = Json.format[Segment]
  implicit val feeFormat: OFormat[Fee] = Json.format[Fee]
  implicit val searchPriceFormat: OFormat[SearchPrice] = Json.format[SearchPrice]
  implicit val travelerPricingFormat: OFormat[TravelerPricing] = Json.format[TravelerPricing]
  implicit val pricingOptionsFormat: OFormat[PricingOptions] = Json.format[PricingOptions]
  implicit val itineraryFormat: OFormat[Itinerary] = Json.format[Itinerary]
  implicit val flightOfferSearchFormat: OFormat[FlightOfferSearch] = Json.format[FlightOfferSearch]
  implicit val flightOfferSearchResultFormat: OFormat[FlightOfferSearchResult] = Json.format[FlightOfferSearchResult]
}



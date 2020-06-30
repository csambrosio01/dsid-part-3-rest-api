package model.amadeus

import play.api.libs.json.{Format, Json, OFormat}
import utils.EnumFormatUtils
import FlightOfferRequest.travelClassFormat

case class FlightOfferSearchResult(
                                    data: Seq[FlightOfferSearch]
                                  )

case class FlightOfferSearch(
                              `type`: String,
                              id: String,
                              source: String,
                              instantTicketingRequired: Option[Boolean],
                              disablePricing: Option[Boolean],
                              nonHomogeneous: Option[Boolean],
                              oneWay: Option[Boolean],
                              paymentCardRequired: Option[Boolean],
                              lastTicketingDate: Option[String],
                              numberOfBookableSeats: Option[Int],
                              itineraries: Seq[Itinerary],
                              price: SearchPrice,
                              pricingOptions: PricingOptions,
                              validatingAirlineCodes: Seq[String],
                              travelerPricings: Seq[TravelerPricing],
                              choiceProbability: Option[String],
                              fareRules: Option[FareRules]
                            )

case class FareRules(
                      currency: String,
                      rules: Seq[TermAndCondition]
                    )

case class TermAndCondition(
                             category: Category.Value,
                             circumstances: String,
                             notApplicable: Boolean,
                             maxPenaltyAmount: String,
                             descriptions: Seq[Description]
                           )

case class Description(
                        descriptionType: String,
                        text: String
                      )

object Category extends Enumeration {
  val REFUND, EXCHANGE, REVALIDATION, REISSUE, REBOOK, CANCELLATION = Value
}

case class Itinerary(
                      duration: Option[String],
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
                    co2Emissions: Option[Co2Emissions],
                    stops: Option[Seq[Stop]]
                  )

case class Stop(
                 iataCode: String,
                 duration: String,
                 arrivalAt: String,
                 departureAt: String
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
                         cabin: TravelClass.Value
                       )

case class SearchPrice(
                        margin: Option[String],
                        billingCurrency: Option[String],
                        additionalServices: Option[Seq[AdditionalService]],
                        currency: String,
                        total: String,
                        base: String,
                        fees: Option[Seq[Fee]],
                        grandTotal: Option[String],
                        taxes: Option[Seq[Tax]],
                        refundableTaxes: Option[String]
                      )

case class Tax(
                amount: String,
                code: String
              )

case class AdditionalService(
                              amount: String,
                              `type`: AdditionalServiceType.Value
                            )

object AdditionalServiceType extends Enumeration {
  val CHECKED_BAGS, MEALS, SEATS, OTHER_SERVICES = Value
}

case class Fee(
                amount: String,
                `type`: String
              )

object FeeType extends Enumeration {
  val TICKETING, FORM_OF_PAYMENT, SUPPLIER = Value
}

case class PricingOptions(
                           includedCheckedBagsOnly: Boolean,
                           fareType: Seq[FareType.Value],
                           corporateCodes: Option[Seq[String]],
                           refundableFare: Option[Boolean],
                           noRestrictionFare: Option[Boolean],
                           noPenaltyFare: Option[Boolean]
                         )

object FareType extends Enumeration {
  val PUBLISHED, NEGOTIATED, CORPORATE = Value
}

case class TravelerPricing(
                            travelerId: String,
                            fareOption: Option[String],
                            travelerType: String,
                            price: Option[SearchPrice],
                            fareDetailsBySegment: Seq[FareDetailsBySegment]
                          )

object FareOption extends Enumeration {
  val STANDARD, INCLUSIVE_TOUR, SPANISH_MELILLA_RESIDENT, SPANISH_CEUTA_RESIDENT,
  SPANISH_CANARY_RESIDENT, SPANISH_BALEARIC_RESIDENT, AIR_FRANCE_METROPOLITAN_DISCOUNT_PASS,
  AIR_FRANCE_DOM_DISCOUNT_PASS, AIR_FRANCE_COMBINED_DISCOUNT_PASS, AIR_FRANCE_FAMILY,
  ADULT_WITH_COMPANION, COMPANION = Value
}

case class FareDetailsBySegment(
                                 segmentId: String,
                                 cabin: Option[String],
                                 fareBasis: Option[String],
                                 brandedFare: Option[String],
                                 `class`: Option[String],
                                 isAllotment: Option[Boolean],
                                 allotmentDetails: Option[AllotmentDetail],
                                 includedCheckedBags: IncludedCheckedBags,
                                 additionalServices: Option[AdditionalServiceRequest]
                               )

case class AdditionalServiceRequest(
                                     chargeableCheckedBags: IncludedCheckedBags,
                                     chargeableSeatNumber: String,
                                     otherServices: Seq[ServiceName.Value]
                                   )

object ServiceName extends Enumeration {
  val PRIORITY_BOARDING, AIRPORT_CHECKIN = Value
}

case class AllotmentDetail(
                            tourName: String,
                            tourReference: String
                          )

case class IncludedCheckedBags(
                                weight: Option[Int],
                                weightUnit: Option[String],
                                quantity: Option[Int]
                              )

object FlightOfferSearch {
  implicit val categoryFormat: Format[Category.Value] = EnumFormatUtils.enumFormat(Category)
  implicit val additionalServiceTypeFormat: Format[AdditionalServiceType.Value] = EnumFormatUtils.enumFormat(AdditionalServiceType)
  implicit val feeTypeFormat: Format[FeeType.Value] = EnumFormatUtils.enumFormat(FeeType)
  implicit val fareTypeFormat: Format[FareType.Value] = EnumFormatUtils.enumFormat(FareType)
  implicit val fareOptionFormat: Format[FareOption.Value] = EnumFormatUtils.enumFormat(FareOption)
  implicit val serviceNameFormat: Format[ServiceName.Value] = EnumFormatUtils.enumFormat(ServiceName)
  implicit val descriptionFormat: OFormat[Description] = Json.format[Description]
  implicit val termAndConditionFormat: OFormat[TermAndCondition] = Json.format[TermAndCondition]
  implicit val fareRulesFormat: OFormat[FareRules] = Json.format[FareRules]
  implicit val taxFormat: OFormat[Tax] = Json.format[Tax]
  implicit val stopFormat: OFormat[Stop] = Json.format[Stop]
  implicit val includedCheckedBagsFormat: OFormat[IncludedCheckedBags] = Json.format[IncludedCheckedBags]
  implicit val additionalServiceFormat: OFormat[AdditionalService] = Json.format[AdditionalService]
  implicit val additionalServiceRequestFormat: OFormat[AdditionalServiceRequest] = Json.format[AdditionalServiceRequest]
  implicit val allotmentDetailFormat: OFormat[AllotmentDetail] = Json.format[AllotmentDetail]
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



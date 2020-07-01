package controllers

import javax.inject.Inject
import model.amadeus.flight.FlightDestination._
import model.amadeus.flight.FlightOfferRequest._
import model.amadeus.flight.FlightOfferSearch._
import model.amadeus.flight.{FlightDestination, FlightOfferRequest, FlightOfferSearch}
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.AmadeusService

import scala.concurrent.{ExecutionContext, Future}

class FlightController @Inject()(
                                  cc: ControllerComponents,
                                  amadeusService: AmadeusService,
                                  langs: Langs,
                                  messagesApi: MessagesApi
                                )
                                (
                                  implicit ec: ExecutionContext
                                )
  extends BaseController(cc, langs, messagesApi) {

  def getFlightDestinations(
                             origin: String,
                             departureDate: Option[String],
                             oneWay: Option[Boolean],
                             duration: Option[String],
                             nonStop: Option[Boolean],
                             maxPrice: Option[Int],
                             viewBy: Option[String]
                           ): Action[AnyContent] = Action.async { _ =>
    val futureFlightDestinations = amadeusService.searchFlightsDestinations(
      origin,
      departureDate,
      oneWay,
      duration,
      nonStop,
      maxPrice,
      viewBy
    )

    handleReturn[Seq[FlightDestination]](futureFlightDestinations)(Writes.seq(flightDestinationFormat))
  }

  def getFlightOffers: Action[AnyContent] = Action.async { implicit request =>
    request
      .body
      .asJson
      .map(_.as[FlightOfferRequest])
      .map { request: FlightOfferRequest =>
        handleReturn[Seq[FlightOfferSearch]](amadeusService.searchFlightOffers(request))(Writes.seq(flightOfferSearchFormat))
      }
      .getOrElse(Future.successful(BadRequest(Json.obj("error" -> messagesApi("pousar.bad_json")))))
  }

  def getFlightOffersHighlights: Action[AnyContent] = Action.async { _ =>
    handleReturn[Seq[FlightOfferSearch]](amadeusService.searchFlightOffersHighlights)(Writes.seq(flightOfferSearchFormat))
  }

  def getFlightOffersHighlightsAirPage: Action[AnyContent] = Action.async { _ =>
    handleReturn[Seq[FlightOfferSearch]](amadeusService.searchFlightOffersHighlightsAirPage)(Writes.seq(flightOfferSearchFormat))
  }
}

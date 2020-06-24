package controllers

import javax.inject.Inject
import model.amadeus.FlightDestination._
import model.amadeus.FlightOfferRequest._
import model.amadeus.FlightOfferSearch._
import model.amadeus.{FlightDestination, FlightOfferRequest, FlightOfferSearch}
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json.Writes
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.FlightService

import scala.concurrent.{ExecutionContext, Future}

class FlightController @Inject()(
                                  cc: ControllerComponents,
                                  flightService: FlightService,
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
    val futureFlightDestinations = flightService.searchFlightsDestinations(
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
        handleReturn[Seq[FlightOfferSearch]](flightService.searchFlightOffers(request))(Writes.seq(flightOfferSearchFormat))
      }
      .getOrElse(Future.successful(BadRequest(messagesApi("bad_json"))))
  }
}

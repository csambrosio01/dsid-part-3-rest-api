package controllers

import javax.inject.Inject
import model.FlightDestination
import model.FlightDestination._
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json.Writes
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.FlightService

import scala.concurrent.ExecutionContext

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
}

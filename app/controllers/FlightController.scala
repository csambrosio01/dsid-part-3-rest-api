package controllers

import exception.{AccessTokenException, NotFoundException}
import javax.inject.Inject
import model.FlightDestination._
import play.api.Logging
import play.api.i18n.{Lang, Langs, MessagesApi}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
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
  extends AbstractController(cc)
    with Logging {

  implicit val lang: Lang = langs.availables.head

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

    futureFlightDestinations.map { flightDestinations =>
      Ok(Json.toJson(flightDestinations)(Writes.seq(flightDestinationFormat)))
    }
      .recover {
        case e: NotFoundException =>
          NotFound(messagesApi(e.message, origin))
        case e: AccessTokenException =>
          BadRequest(messagesApi(e.message))
        case _ =>
          BadRequest(messagesApi("amadeus.generic_error"))
      }
  }
}

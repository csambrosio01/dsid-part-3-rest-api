package controllers

import javax.inject.Inject
import model.amadeus.Location
import model.amadeus.Location._
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json.Writes
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.AmadeusService

import scala.concurrent.ExecutionContext

class AirportController @Inject()(
                                   cc: ControllerComponents,
                                   amadeusService: AmadeusService,
                                   langs: Langs,
                                   messagesApi: MessagesApi
                                 )
                                 (
                                   implicit ec: ExecutionContext
                                 )
  extends BaseController(cc, langs, messagesApi) {

  def searchAirportOrCity(airportOrCity: String): Action[AnyContent] = Action.async { _ =>
    handleReturn[Seq[Location]](amadeusService.searchAirportOrCity(airportOrCity))(Writes.seq[Location])
  }
}

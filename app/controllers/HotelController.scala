package controllers

import javax.inject.Inject
import model.amadeus.hotel.HotelOffers._
import model.amadeus.hotel.{HotelOfferSearchRequest, HotelOffers}
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.AmadeusService

import scala.concurrent.{ExecutionContext, Future}

class HotelController @Inject()(
                                 cc: ControllerComponents,
                                 amadeusService: AmadeusService,
                                 langs: Langs,
                                 messagesApi: MessagesApi
                               )
                               (
                                 implicit ec: ExecutionContext
                               )
  extends BaseController(cc, langs, messagesApi) {

  def getHotelOffers: Action[AnyContent] = Action.async { implicit request =>
    request
      .body
      .asJson
      .map(_.as[HotelOfferSearchRequest])
      .map { request: HotelOfferSearchRequest =>
        handleReturn[Seq[HotelOffers]](amadeusService.getHotelOffers(request))(Writes.seq(hotelOffersFormat))
      }
      .getOrElse(Future.successful(BadRequest(Json.obj("error" -> messagesApi("pousar.bad_json")))))
  }

}

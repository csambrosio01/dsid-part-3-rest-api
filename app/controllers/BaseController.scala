package controllers

import exception.{AccessTokenException, NotFoundException}
import javax.inject.Inject
import play.api.Logging
import play.api.i18n.{Lang, Langs, MessagesApi}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Result}

import scala.concurrent.{ExecutionContext, Future}

class BaseController @Inject()(
                                cc: ControllerComponents,
                                langs: Langs,
                                messagesApi: MessagesApi
                              )
                              (
                                implicit ec: ExecutionContext
                              )
  extends AbstractController(cc)
    with Logging {

  implicit val lang: Lang = langs.availables.head

  def healthCheck: Action[AnyContent] = Action {
    Ok("I'm safe and sound")
  }

  def handleReturn[A](futureResult: Future[A])(implicit writes: Writes[A]): Future[Result] = {
    futureResult.map { result =>
      Ok(Json.toJson(result))
    }
      .recover {
        case e: NotFoundException =>
          NotFound(messagesApi(e.message, e.args))
        case e: AccessTokenException =>
          BadRequest(messagesApi(e.message))
        case _ =>
          BadRequest(messagesApi("amadeus.generic_error"))
      }
  }
}

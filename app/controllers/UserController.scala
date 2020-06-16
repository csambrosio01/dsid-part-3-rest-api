package controllers

import exception.{PasswordException, WrongCredentialsException}
import javax.inject.Inject
import model.Login._
import model.User._
import model.{CreateUser, Login}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import services.UserService
import services.session.SessionService

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject() (
                                 sessionGenerator: SessionGenerator,
                                 cc: ControllerComponents,
                                 sessionService: SessionService,
                                 userService: UserService,
                                 userAction: UserInfoAction
                               )
                               (
                                 implicit ec: ExecutionContext
                               )
  extends AbstractController(cc)
    with Logging {

  def createUser(): Action[AnyContent] = Action.async { implicit request =>
    request
      .body
      .asJson
      .map(_.as[CreateUser])
      .map { user: CreateUser =>
        val validation = for {
          userCreated <- userService.createUser(user)
          (sessionId, encryptedCookie) <- sessionGenerator.createSession(userCreated)
        } yield (sessionId, encryptedCookie)

        validation.map {
          case (sessionId, encryptedCookie) =>
            val session = request.session + (SESSION_ID -> sessionId)
            Ok("Logged in")
              .withSession(session)
              .withCookies(encryptedCookie)
        }
          .recover {
            case e: PasswordException =>
              BadRequest(e.getMessage)

            case _ =>
              BadRequest("Could not create user")
          }
      }
      .getOrElse(Future.successful(BadRequest("Bad json")))
  }
}

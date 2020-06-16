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

  def login: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request
      .body
      .asJson
      .map(_.as[Login])
      .map { login =>
        val validation = for {
          user <- userService.login(login)
          (sessionId, encryptedCookie) <- sessionGenerator.createSession(user)
        } yield (user, sessionId, encryptedCookie)

        validation.map {
          case (user, sessionId, encryptedCookie) =>
            val session = request.session + (SESSION_ID -> sessionId)
            Ok(Json.toJson(user))
              .withSession(session)
              .withCookies(encryptedCookie)

          case _ => BadRequest("Could not login")
        }
          .recover {
            case e: WrongCredentialsException =>
              logger.warn(e.message)
              BadRequest(Json.obj("error" -> e.getMessage))
          }
      }
      .getOrElse(Future.successful(BadRequest("Bad json")))
  }

  def logout: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    request.session.get(SESSION_ID).foreach { sessionId =>
      sessionService.delete(sessionId)
    }

    discardingSession {
      Ok("Logged out")
    }
  }
}

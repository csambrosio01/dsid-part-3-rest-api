package services

import exception.{PasswordException, WrongCredentialsException}
import javax.inject.Inject
import model.{CreateUser, Login, User}
import persistance.user.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
                             userRepository: UserRepository
                           )
                           (
                             implicit ec: ExecutionContext
                           ) {

  def createUser(user: CreateUser): Future[User] = {
    if (user.password.matches("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#(){}?:;><=.,^~_+-\[\]])[A-Za-z\d@$!%*?&#(){}?:;><=.,^~_+-\[\]]{8,40}$""")) {
      userRepository.create(user)
    } else {
      throw PasswordException("user.create.bad_password")
    }
  }

  def login(login: Login): Future[User] = {
    userRepository
      .findUserByUsername(login.username)
      .map { user =>
        user.fold(throw WrongCredentialsException("user.login.wrong_credentials")) { user =>
          if (user.password == login.password) {
            user
          } else {
            throw WrongCredentialsException("user.login.wrong_credentials")
          }
        }
      }
  }
}

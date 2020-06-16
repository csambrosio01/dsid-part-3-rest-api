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
    if (user.password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}\\[\\]:;<>,.?/~_+-=|\\\\]).{8,40}$")) {
      userRepository.create(user)
    } else {
      throw PasswordException("Wrong password expression")
    }
  }
}
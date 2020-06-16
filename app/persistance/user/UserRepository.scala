package persistance.user

import com.google.inject.ImplementedBy
import model.{CreateUser, User}

import scala.concurrent.Future

@ImplementedBy(classOf[SqlUserRepository])
trait UserRepository {

  def create(user: CreateUser): Future[User]
}

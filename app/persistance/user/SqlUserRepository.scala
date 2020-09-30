package persistance.user

import java.sql.Timestamp

import javax.inject.Inject
import model.{CreateUser, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

private class UserTable(tag: Tag) extends Table[User](tag, "users") {

  def userId = column[Option[Long]]("user_id", O.PrimaryKey, O.AutoInc)

  def username = column[String]("username", O.Unique)

  def password = column[String]("password", O.Unique)

  def name = column[String]("name")

  def email = column[String]("email", O.Unique)

  def phoneNumber = column[String]("phone_number", O.Unique)

  def addressId = column[Long]("address_id")

  def createdAt = column[Timestamp]("created_at")

  def updatedAt = column[Timestamp]("updated_at")

  def * = (userId, username, password, name, email, phoneNumber, addressId, createdAt, updatedAt) <> ((User.apply _).tupled, User.unapply)
}

class SqlUserRepository @Inject()(
                                   protected val dbConfigProvider: DatabaseConfigProvider,
                                   addressRepository: AddressRepository
                                 )
                                 (
                                   implicit ec: ExecutionContext
                                 )
  extends HasDatabaseConfigProvider[JdbcProfile]
    with UserRepository {

  import profile.api._

  private val users = TableQuery[UserTable]

  override def create(user: CreateUser): Future[User] = {
    addressRepository.create(user.address)
      .flatMap { address =>
        val insert = users.map { u =>
          (u.username, u.password, u.name, u.email, u.phoneNumber, u.addressId)
        }
          .returning(users) += ((user.username, user.password, user.name, user.email, user.phoneNumber, address.addressId.get))

        db.run(insert)
      }
  }

  override def findUserByUsername(username: String): Future[Option[User]] = {
    val query = users.filter(_.username === username)

    db.run(query.result.headOption)
  }

  override def findUserByEmail(usernameOrEmail: String): Future[Option[User]] = {
    val query = users.filter(_.email === usernameOrEmail)

    db.run(query.result.headOption)
  }
}

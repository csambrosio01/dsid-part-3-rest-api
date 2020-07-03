package persistance.user

import javax.inject.Inject
import model.Address
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

private class AddressTable(tag: Tag) extends Table[Address](tag, "address") {

  def addressId = column[Option[Long]]("address_id", O.PrimaryKey, O.AutoInc)

  def address = column[String]("address")

  def complement = column[Option[String]]("complement")

  def neighborhood = column[String]("neighborhood")

  def city = column[String]("city")

  def state = column[String]("state")

  def zipCode = column[String]("zip_code")

  def country = column[String]("country")

  def * = (addressId, address, complement, neighborhood, city, state, zipCode, country) <> ((Address.apply _).tupled, Address.unapply)
}

class SqlAddressRepository @Inject() (
                                       protected val dbConfigProvider: DatabaseConfigProvider
                                     )
                                     (
                                       implicit ec: ExecutionContext
                                     )
  extends HasDatabaseConfigProvider[JdbcProfile]
    with AddressRepository {

  import profile.api._

  private val addresses = TableQuery[AddressTable]

  override def create(address: Address): Future[Address] = {
    val insert = addresses.map { a =>
      (a.address, a.complement, a.neighborhood, a.city, a.state, a.zipCode, a.country)
    }
      .returning(addresses) += (address.address, address.complement, address.neighborhood, address.city, address.state, address.zipCode, address.country)

    db.run(insert)
  }

  override def findById(addressId: Long): Future[Address] = {
    val query = addresses.filter(_.addressId === addressId)

    db.run(query.result.head)
  }
}

package persistance.user

import com.google.inject.ImplementedBy
import model.Address

import scala.concurrent.Future

@ImplementedBy(classOf[SqlAddressRepository])
trait AddressRepository {

  def create(address: Address): Future[Address]

  def findById(addressId: Long): Future[Address]
}

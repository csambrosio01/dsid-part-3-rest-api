package model

import java.sql.Timestamp

import play.api.libs.json.{Json, OFormat, Reads}
import format.TimestampFormat._

case class User(
                 userId: Option[Long],
                 username: String,
                 password: String,
                 name: String,
                 email: String,
                 phoneNumber: String,
                 addressId: Long,
                 createdAt: Timestamp,
                 updatedAt: Timestamp
               )

case class Address(
                    addressId: Option[Long],
                    address: String,
                    complement: Option[String],
                    neighborhood: String,
                    city: String,
                    state: String,
                    zipCode: String,
                    country: String,
                    number: String
                  )

case class CreateUser(
                       username: String,
                       password: String,
                       name: String,
                       email: String,
                       phoneNumber: String,
                       address: Address
                     )

object User {
  implicit val addressFormat: OFormat[Address] = Json.format[Address]
  implicit val userFormat: OFormat[User] = Json.format[User]
  implicit val createUserRead: Reads[CreateUser] = Json.reads[CreateUser]
}

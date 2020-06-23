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
                 createdAt: Timestamp,
                 updatedAt: Timestamp
               )

case class CreateUser(
                       username: String,
                       password: String,
                       name: String,
                       email: String,
                       phoneNumber: String
                     )

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
  implicit val createUserRead: Reads[CreateUser] = Json.reads[CreateUser]
}

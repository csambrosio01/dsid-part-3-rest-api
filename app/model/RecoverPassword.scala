package model

import play.api.libs.json.{Json, Reads}

case class RecoverPassword(
                            email: String
                          )

object RecoverPassword {
  implicit val recoverPasswordReads: Reads[RecoverPassword] = Json.reads[RecoverPassword]
}

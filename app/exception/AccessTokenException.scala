package exception

case class AccessTokenException(message: String) extends Exception(message)

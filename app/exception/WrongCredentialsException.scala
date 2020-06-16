package exception

case class WrongCredentialsException(message: String) extends Exception(message)

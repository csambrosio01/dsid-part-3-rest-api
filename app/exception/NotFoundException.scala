package exception

case class NotFoundException(message: String) extends Exception(message)

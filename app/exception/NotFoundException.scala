package exception

case class NotFoundException(message: String, args: Any*) extends Exception(message)

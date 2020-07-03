package services

import exception.{NotFoundException, PasswordException, WrongCredentialsException}
import javax.inject.Inject
import model.{AddressResponse, CreateUser, Login, User}
import persistance.user.UserRepository
import play.api.libs.ws.WSClient
import requests.BaseExternalRequests

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
                             userRepository: UserRepository,
                             ws: WSClient
                           )
                           (
                             implicit ec: ExecutionContext
                           )
  extends BaseExternalRequests(ws) {

  def createUser(user: CreateUser): Future[User] = {
    if (user.password.matches("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#(){}?:;><=.,^~_+-\[\]])[A-Za-z\d@$!%*?&#(){}?:;><=.,^~_+-\[\]]{8,40}$""")) {
      userRepository.create(user)
    } else {
      throw PasswordException("user.create.bad_password")
    }
  }

  def login(login: Login): Future[User] = {
    userRepository
      .findUserByUsername(login.username)
      .map { user =>
        user.fold(throw WrongCredentialsException("user.login.wrong_credentials")) { user =>
          if (user.password == login.password) {
            user
          } else {
            throw WrongCredentialsException("user.login.wrong_credentials")
          }
        }
      }
  }

  def getZipCode(zipCode: String): Future[AddressResponse] = {
    val postBody =
      s"""
                   <Envelope xmlns="http://schemas.xmlsoap.org/soap/envelope/">
                   <Body>
                   <consultaCEP xmlns="http://cliente.bean.master.sigep.bsb.correios.com.br/">
                   <cep xmlns="">$zipCode</cep>
                   </consultaCEP>
                   </Body>
                   </Envelope>"""

    prepareFullRequest("https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente?wsdl", 10 * 60)
      .addHttpHeaders("Content-Type" -> "text/xml")
      .post(postBody)
      .map { result =>
        if (result.status == 200) {
          val response = result.xml
          AddressResponse(
            (response \\ "end").text,
            (response \\ "bairro").text,
            (response \\ "cidade").text,
            (response \\ "uf").text,
            (response \\ "cep").text,
            "Brasil"
          )
        } else {
          throw NotFoundException("cep.not_found")
        }
      }
  }
}

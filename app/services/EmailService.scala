package services

import javax.inject.Inject
import play.api.libs.mailer.{Email, MailerClient}

import scala.concurrent.{ExecutionContext, Future}

class EmailService @Inject()(
                              mailerClient: MailerClient
                            )
                            (
                              implicit ec: ExecutionContext
                            ) {

  def sendEmail(subject: String, to: Seq[String], bodyHtml: Option[String]): Future[String] = {
    Future {
      val email = Email(
        subject = subject,
        from = "Pousar.com <no.reply.pousar@gmail.com>",
        to = to,
        bodyHtml = bodyHtml
      )

      mailerClient.send(email)
    }
  }
}

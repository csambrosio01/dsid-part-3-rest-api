package configurations

import javax.inject.Inject
import play.api.Configuration

class ServiceConfig @Inject()(
                               configuration: Configuration
                             ) {
  val amadeusApiKey = configuration.get[String]("pousar.amadeus.apiKey")
  val amadeusApiSecret = configuration.get[String]("pousar.amadeus.apiSecret")
  val amadeusUrl = configuration.get[String]("pousar.amadeus.url")
}

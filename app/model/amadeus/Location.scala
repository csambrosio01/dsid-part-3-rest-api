package model.amadeus

import play.api.libs.json.{Json, OFormat}

case class LocationResult(
                           data: Seq[Location]
                         )

/*
 {
      "type": "location",
      "subType": "CITY",
      "name": "LONDON",
      "detailedName": "LONDON/GB",
      "id": "CLON",
      "self": {
        "href": "https://test.api.amadeus.com/v1/reference-data/locations/CLON",
        "methods": [
          "GET"
        ]
      },
      "timeZoneOffset": "+01:00",
      "iataCode": "LON",
      "geoCode": {
        "latitude": 51.5,
        "longitude": -0.16666
      },
      "address": {
        "cityName": "LONDON",
        "cityCode": "LON",
        "countryName": "UNITED KINGDOM",
        "countryCode": "GB",
        "regionCode": "EUROP"
      },
      "analytics": {
        "travelers": {
          "score": 100
        }
      }
    }
 */

case class Location(
                     id: String,
                     `type`: String,
                     subType: String,
                     name: String,
                     detailedName: String,
                     iataCode: String,
                     address: Address,
                   )

case class Address(
                    cityName: String,
                    cityCode: String,
                    countryName: String,
                    countryCode: String,
                    regionCode: String,
                    stateCode: Option[String]
                  )

object Location {
  implicit val addressFormat: OFormat[Address] = Json.format[Address]
  implicit val locationFormat: OFormat[Location] = Json.format[Location]
  implicit val locationResultFormat: OFormat[LocationResult] = Json.format[LocationResult]
}

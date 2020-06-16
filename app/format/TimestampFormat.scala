package format

import java.sql.Timestamp

import play.api.libs.json.{Format, JsResult, JsValue, Json}

object TimestampFormat {
  def timestampToLong(t: Timestamp): Long = t.getTime

  def longToTimestamp(dt: Long): Timestamp = new Timestamp(dt)

  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(t: Timestamp): JsValue = Json.toJson(timestampToLong(t))

    def reads(json: JsValue): JsResult[Timestamp] = Json.fromJson[Long](json).map(longToTimestamp)
  }
}

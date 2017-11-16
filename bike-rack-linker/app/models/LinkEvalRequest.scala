package models

import play.api.libs.json._

case class LinkEvalRequest(
    source: String,
    target: String)

object LinkEvalRequest {
  implicit val taskFormat = Json.format[LinkEvalRequest]
}

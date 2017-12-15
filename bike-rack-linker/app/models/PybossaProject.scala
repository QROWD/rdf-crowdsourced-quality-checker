package models

import play.api.libs.json._

case class PybossaProject(
    id:          Option[Int],
    name:        String,
    short_name:  String,
    description: String)

object PybossaProject {
  implicit val pybossaProjectFormat = Json.format[PybossaProject]
}

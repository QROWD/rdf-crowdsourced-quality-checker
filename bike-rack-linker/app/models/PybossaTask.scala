package models

import play.api.libs.json._

case class PybossaTask(
    project_id:  Int,
    info:        List[BikeRack],
    calibration: Int            = 0,
    priority_0:  Float          = 0,
    n_answers:   Int            = 30,
    quorum:      Int            = 0)

object PybossaTask {
  implicit val pybossaTaskFormat = Json.format[PybossaTask]
}

package models

import play.api.libs.json._


case class Task(
  project_id: Int,
  info: JsValue,
  calibration: Int = 0,
  priority_0: Float = 0,
  n_answers: Int = 30,
  quorum: Int = 0
)

object Task {
  implicit val taskFormat = Json.format[Task]
}


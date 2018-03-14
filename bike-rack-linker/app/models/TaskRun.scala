package models

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._

case class TaskRun(
    answer:     String,
    user:       Double,
    task:       Double,
    created:    DateTime,
    finishTime: DateTime)

object TaskRun {
  implicit val dateTimeJsReader = JodaReads.jodaDateReads("")

  implicit val taskRunReads: Reads[TaskRun] = (
    (JsPath \ "info").read[String] and
    (JsPath \ "user_id").read[Double] and
    (JsPath \ "task_id").read[Double] and
    (JsPath \ "created").read[DateTime] and
    (JsPath \ "finish_time").read[DateTime]
  )(TaskRun.apply _)
}

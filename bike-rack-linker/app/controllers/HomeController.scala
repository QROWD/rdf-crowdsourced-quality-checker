package controllers

import scala.concurrent.Future
import scala.concurrent.duration._
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.duration._

import play.api.mvc._
import play.api.libs.ws._
import play.api.http.HttpEntity

import play.api.libs.json._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits._
import javax.inject._
import play.api._
import play.api.libs.ws._
import play.api.libs.ws.WSRequest
import play.api.mvc._
import java.net.URL
import models.Task
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
    cc:     ControllerComponents,
    ws:     WSClient,
    config: Configuration) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] ⇒
    Ok(views.html.index())
  }

  def addTask() = Action(BodyParsers.parse.json).async { request ⇒
    val tasks = request.body
    val task: Task = Task(5, tasks)
    val url: URL = new URL(config.get[String]("pybossa.server-url") + "/task")
    val pybossaRequest: WSRequest =
      ws.url(url.toString).addHttpHeaders("Content-Type" -> "application/json")
      .addQueryStringParameters("api_key" -> config.get[String]("pybossa.api-key"))
    val futureResponse: Future[WSResponse] = pybossaRequest.post(Json.toJson(task))
    futureResponse.map({i ⇒ 
        Ok(i.body[JsValue])})
  }
}

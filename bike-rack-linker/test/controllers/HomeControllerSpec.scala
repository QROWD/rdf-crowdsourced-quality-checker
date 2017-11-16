package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import scala.io.Source
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.duration._

import play.api.mvc._
import play.api.libs.ws._
import play.api.http.HttpEntity
import javax.inject._
import play.api.Configuration
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import javax.inject._
import play.api._
import play.api.mvc._
import models.Task

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  // "HomeController GET" should {

  //   // "render the index page from a new instance of controller" in {
  //   //   val controller = new HomeController(stubControllerComponents())
  //   //   val home = controller.index().apply(FakeRequest(GET, "/"))

  //   //   status(home) mustBe OK
  //   //   contentType(home) mustBe Some("text/html")
  //   //   contentAsString(home) must include ("Welcome to Play")
  //   // }

  //   // "render the index page from the application" in {
  //   //   val controller = inject[HomeController]
  //   //   val home = controller.index().apply(FakeRequest(GET, "/"))

  //   //   status(home) mustBe OK
  //   //   contentType(home) mustBe Some("text/html")
  //   //   contentAsString(home) must include ("Welcome to Play")
  //   // }

  //   "render the index page from the router" in {
  //     val request = FakeRequest(GET, "/")
  //     val home = route(app, request).get

  //     status(home) mustBe OK
  //     contentType(home) mustBe Some("text/html")
  //     contentAsString(home) must include ("Welcome to Play")
  //   }
  // }

  "HomeController POST" should {

    "accept a task" in {
      val info = Json.parse(getClass.getResourceAsStream("/unique-id-service-output.json"))
      val request = FakeRequest(POST, "/")
        .withJsonBody(info)

      val home = route(app, request).get


      status(home) mustBe OK
      // {
      //   "flash": "Tasks imported",
      //   "next": "/project/<short_name>/tasks/",
      //   "status": "success"
      // }

    }
  }

//   "HomeController POST" should {

//     "query properties" in {
//       val controller = inject[HomeController]
//       val home = controller.retrieveInfo()
//       println(home.toString())
//     }
//   }

}

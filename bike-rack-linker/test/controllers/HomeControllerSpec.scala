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

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */

class HomeControllerSpec extends PlaySpec with GuiceOneServerPerSuite with Injecting {

  "DELETE /project/{id}" should {

    "delete the project" in {

      val project = Json.parse(getClass.getResourceAsStream("/pybossa-test-project/pybossa-project.json"))
      val projectShortName = (project \ "short_name").as[String]
      val request = FakeRequest(DELETE, "/project/" + projectShortName)
      val pybossaResponse = route(app, request).get
      status(pybossaResponse) mustBe 204
    }
  }

  "POST /project" should {

    "create a project" in {
      val project = Json.parse(getClass.getResourceAsStream("/pybossa-test-project/pybossa-project.json")).as[JsObject]
      val template = Source.fromResource("pybossa-test-project/template.html").mkString
      val projectJson = project + ("info" -> Json.obj("task_presenter" -> template))
      val request = FakeRequest(POST, "/project").withJsonBody(projectJson)
      val pybossaResponse = route(app, request).get
      status(pybossaResponse) mustBe OK
    }
  }

  "POST /project/:shortName/task" should {

    "accept a task" in {
      val project = Json.parse(getClass.getResourceAsStream("/pybossa-test-project/pybossa-project.json"))
      val projectShortName = (project \ "short_name").as[String]
      val info = Json.parse(getClass.getResourceAsStream("/unique-id-service-output.json"))
      val request = FakeRequest(POST, "/project/" + projectShortName + "/task")
        .withJsonBody(info)
      val request2 = FakeRequest(POST, "/project/" + projectShortName + "/task")
        .withJsonBody(info)

      val pybossaResponse = route(app, request).get
      val pybossaResponse2 = route(app, request2).get

      status(pybossaResponse) mustBe OK
      status(pybossaResponse2) mustBe OK
    }
  }







  // "POST /resolveTask" should {

  //   "accept a task" in {
  //     val payload = Json.parse(getClass.getResourceAsStream("/pybossa-webhook.json"))
  //     val request = FakeRequest(POST, "/resolveTask").withJsonBody(payload)

  //     val pybossaResponse = route(app, request).get

  //     status(pybossaResponse) mustBe OK
  //   }
  // }
}

// class ExampleSpec extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with FirefoxFactory {

//   "The OneBrowserPerTest trait" must {
//     "provide a web driver" in {
//       go to s"http://localhost:5000/project/test/newtask?api_key=fe284c69-1b06-4d6b-97dc-f2928693f7cf"
//       click on find(id("same")).value
//       click on find(id("same")).value
//       eventually { pageTitle mustBe "PyBossa · Project: test · Contribute - PyBossa by Scifabric" }
//     }
//   }
// }

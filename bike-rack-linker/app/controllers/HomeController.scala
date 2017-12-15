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
import javax.persistence.criteria.{ Expression, Predicate }

import models.PybossaTask
import models.BikeRack
import com.amaxilatis.orion.OrionClient
import models.LinkEvalRequest
import models.PybossaProject

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
    cc:     ControllerComponents,
    ws:     WSClient,
    config: Configuration) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] ⇒
    Ok(views.html.index())
  }

  def pybossaRequest(endpoint: String): WSRequest = {
    val url: URL = new URL(config.get[String]("pybossa.server-url") + endpoint)
    ws.url(url.toString)
      .addHttpHeaders("Content-Type" -> "application/json")
      .addQueryStringParameters("api_key" -> config.get[String]("pybossa.api-key"))
  }

  def createProject() = Action(parse.json).async { request: Request[JsValue] ⇒
    pybossaRequest("project").post(request.body).map { response ⇒
      response.status match {
        case 200 ⇒ Ok(response.body)
        case _   ⇒ BadRequest(response.body)
      }
    }
  }

  def getProjectId(shortName: String): Future[Option[Int]] = {
    pybossaRequest("project").addQueryStringParameters("short_name" -> shortName).get() map {
      response ⇒ (response.json \ 0 \ "id").asOpt[Int]
    }
  }

  def deleteProject(shortName: String): Action[AnyContent] = Action.async { request ⇒
    getProjectId(shortName).flatMap {
      case Some(id) ⇒ deleteProject(id)
      case None     ⇒ Future.successful(NoContent)
    }
  }

  def deleteProject(id: Int): Future[Result] = pybossaRequest("project/" + id).delete() map {
    _.status match {
      case 204 ⇒ NoContent
      case _   ⇒ BadRequest
    }
  }

  def resolveTask() = Action(parse.json).async { request: Request[JsValue] ⇒
    (request.body \ "task_id").asOpt[Int] match {
      case Some(task_id) ⇒
        pybossaRequest("taskrun")
          .addQueryStringParameters("task_id" -> task_id.toString)
          .get()
          .map(response ⇒ Ok(response.json))
      case None ⇒ Future.successful(NoContent)
    }
  }

  def resolveAnswers(answers: List[JsValue]) = {}

  def addTask(shortName: String) = Action(parse.json).async { request ⇒
    getProjectId(shortName).flatMap {
      case Some(id) ⇒ request.body.validate[LinkEvalRequest] match {
        case s: JsSuccess[LinkEvalRequest] ⇒ {
          val linkEvalRequest: LinkEvalRequest = s.get
          val sourceBikeRack = retrieveInfo(linkEvalRequest.source)
          val targetBikeRack = retrieveInfo(linkEvalRequest.target)
          val task: PybossaTask = PybossaTask(id, List(sourceBikeRack, targetBikeRack))
          pybossaRequest("task").post(Json.toJson(task)) map {
            response ⇒ Ok(response.json)
          }
        }
        case e: JsError ⇒ Future { BadRequest(JsError.toJson(e)) }
      }
      case None ⇒ Future { NotFound }
    }
  }

  // def testRetrieveInfo() = Action {
  // }

  def subscribeOrionContextBroker() = {
    val orionClient: OrionClient = new OrionClient(
      config.get[String]("orion-client.server-url"),
      config.get[String]("orion-client.token"))
  }

  def retrieveInfo(uri: String): BikeRack = {

    import java.util.List
    import javax.persistence.EntityManager
    import javax.persistence.criteria.Root

    import org.aksw.jena_sparql_api.mapper.jpa.core.SparqlEntityManagerFactory
    import org.aksw.jena_sparql_api.mapper.util.JpaUtils
    import org.aksw.jena_sparql_api.stmt.SparqlQueryParserImpl
    import org.aksw.jena_sparql_api.update.FluentSparqlService
    import org.apache.jena.rdf.model.Model
    import org.apache.jena.rdf.model.ModelFactory
    import org.apache.jena.riot.RDFDataMgr
    import org.apache.jena.riot.RDFFormat
    import javax.persistence.criteria.CriteriaBuilder;
    import javax.persistence.criteria.CriteriaQuery;

    val emFactory: SparqlEntityManagerFactory = new SparqlEntityManagerFactory()

    emFactory.getPrefixMapping()
      .setNsPrefix("schema", "http://schema.org/")
      .setNsPrefix("dbo", "http://dbpedia.org/ontology/")
      .setNsPrefix("geo", "http://www.w3.org/2003/01/geo/")
      .setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/")
      .setNsPrefix("dbr", "http://dbpedia.org/resource/")
      .setNsPrefix("nss", "http://example.org/nss/")
      .setNsPrefix("lgdo", "http://linkedgeodata.org/ontology/")
    //
    // Classes which to register to the persistence unit
    emFactory.addScanPackageName(classOf[BikeRack].getPackage().getName())

    val dataModel: Model = RDFDataMgr.loadModel("bike-racks-with-types.nt")

    emFactory.setSparqlService(FluentSparqlService
      .from(dataModel)
      //.http("http://dbpedia.org/sparql", "http://dbpedia.org")
      .config().configQuery()
      .withParser(SparqlQueryParserImpl.create())
      .withPagination(50000)
      .end().end().create())

    val em: EntityManager = emFactory.getObject()

    val bikeRack: BikeRack = em.find(classOf[BikeRack], uri)

    return bikeRack

  }
}

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

import models.Task
import models.Company
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
    futureResponse.map({ i ⇒
      Ok(i.body[JsValue])
    })
  }

  // def testRetrieveInfo() = Action {
  // }

  def retrieveInfo() = {

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
      .setNsPrefix("dbr", "http://dbpedia.org/resource/")
      .setNsPrefix("nss", "http://example.org/nss/")
    //
    // Classes which to register to the persistence unit
    emFactory.addScanPackageName(classOf[Company].getPackage().getName())

    val dataModel: Model = RDFDataMgr.loadModel("dbpedia-companies.ttl")

    emFactory.setSparqlService(FluentSparqlService
      .from(dataModel)
      //.http("http://dbpedia.org/sparql", "http://dbpedia.org")
      .config().configQuery()
      .withParser(SparqlQueryParserImpl.create())
      .withPagination(50000)
      .end().end().create())

    val em: EntityManager = emFactory.getObject()

    import models.Company
    import java.lang.Double
    import org.aksw.jena_sparql_api.mapper.util.JpaUtils
    import javax.persistence.criteria.Root

    val avg = JpaUtils.getSingleResult(em, classOf[Double], (cb: CriteriaBuilder, cq: CriteriaQuery[Double]) ⇒ {
      def foo(cb: CriteriaBuilder, cq: CriteriaQuery[Double]) = {
        val r2 = cq.from(classOf[Company])
        cq.select(cb.avg(r2.get("numberOfLocations")))
      }
      foo(cb, cq)
    }).doubleValue
    System.out.println("Average number of locations: " + avg);

    val matches = JpaUtils
      .getResultList(em, classOf[Company], (cb: CriteriaBuilder, cq: CriteriaQuery[Company]) ⇒ {
        val r: Root[Company] = cq.from(classOf[Company])
        cq.select(r)
          .where(cb.greaterThanOrEqualTo(
            r.get("foundingYear"), new java.lang.Integer(1955)).asInstanceOf[Expression[java.lang.Boolean]])
        // .where(cb.greaterThanOrEqualTo(
        //   r.get("numberOfLocations"), new java.lang.Integer(36000)).asInstanceOf[Predicate])
      })
    println(matches.toString)
  }
}

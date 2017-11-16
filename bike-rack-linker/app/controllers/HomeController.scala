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
    request.body.validate[LinkEvalRequest] match {
      case s: JsSuccess[LinkEvalRequest] ⇒ {
        val linkEvalRequest: LinkEvalRequest = s.get

        val sourceBikeRack = retrieveInfo(linkEvalRequest.source)
        val targetBikeRack = retrieveInfo(linkEvalRequest.target)
        val task: PybossaTask = PybossaTask(
          config.get[Int]("pybossa.project.id"),
          List(sourceBikeRack, targetBikeRack))

        val url: URL = new URL(config.get[String]("pybossa.server-url") + "/task")
        val pybossaRequest: WSRequest =
          ws.url(url.toString).addHttpHeaders("Content-Type" -> "application/json")
            .addQueryStringParameters("api_key" -> config.get[String]("pybossa.api-key"))
        val futureResponse: Future[WSResponse] = pybossaRequest.post(Json.toJson(task))
        futureResponse.map({ i ⇒
          Ok(i.body[JsValue])
        })
      }
      case e: JsError ⇒ {
        Future { Ok(views.html.index()) }
      }
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
      .setNsPrefix("dbr", "http://dbpedia.org/resource/")
      .setNsPrefix("nss", "http://example.org/nss/")
      .setNsPrefix("lgdo", "http://linkedgeodata.org/ontology/")
    //
    // Classes which to register to the persistence unit
    emFactory.addScanPackageName(classOf[BikeRack].getPackage().getName())

    val dataModel: Model = RDFDataMgr.loadModel("bike-racks.nt")

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

    // import java.lang.Double
    // import org.aksw.jena_sparql_api.mapper.util.JpaUtils
    // import javax.persistence.criteria.Root

    // val avg = JpaUtils.getSingleResult(em, classOf[Double], (cb: CriteriaBuilder, cq: CriteriaQuery[Double]) ⇒ {
    //   def foo(cb: CriteriaBuilder, cq: CriteriaQuery[Double]) = {
    //     val r2 = cq.from(classOf[BikeRack])
    //     cq.select(cb.avg(r2.get("numberOfLocations")))
    //   }
    //   foo(cb, cq)
    // }).doubleValue
    // System.out.println("Average number of locations: " + avg);
  }
}

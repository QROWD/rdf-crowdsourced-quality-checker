package models

import org.aksw.jena_sparql_api.mapper.annotation.Datatype
import org.aksw.jena_sparql_api.mapper.annotation.DefaultIri
import org.aksw.jena_sparql_api.mapper.annotation.Iri
import org.aksw.jena_sparql_api.mapper.annotation.RdfType

import play.api.libs.json._

@RdfType("aksw:Task")
@DefaultIri("dbr:#{label}")
case class Task(
    @Iri("rdfs:label") project_id:            Int, // TODO Int as label?
    info:                                     JsValue, // TODO Which RDF property?
    @RdfType("aksw:Calibration") calibration: Int     = 0,
    @RdfType("aksw:Priority") priority_0:     Float   = 0,
    @RdfType("aksw:Answers") n_answers:       Int     = 30,
    @RdfType("aksw:Qourum") quorum:           Int     = 0)

object Task {
  implicit val taskFormat = Json.format[Task]
}

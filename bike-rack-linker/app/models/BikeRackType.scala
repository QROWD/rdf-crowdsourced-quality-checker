package models

import play.api.libs.json._
import org.aksw.jena_sparql_api.mapper.annotation.Datatype
import org.aksw.jena_sparql_api.mapper.annotation.DefaultIri
import org.aksw.jena_sparql_api.mapper.annotation.Iri
import org.aksw.jena_sparql_api.mapper.annotation.RdfType

import scala.annotation.meta.beanGetter
import scala.beans.BeanProperty

@RdfType("lgdo:BicycleParkingType") // TODO Default Iri?
case class BikeRackType(
    @(Iri @beanGetter)("rdfs:label")@BeanProperty var label:        String,
    @(Iri @beanGetter)("foaf:depiction")@BeanProperty var depiction:String) {
  def this() = this("", "")
}

object BikeRackType {
  implicit val bikeRackTypeFormat = Json.format[BikeRackType]
}

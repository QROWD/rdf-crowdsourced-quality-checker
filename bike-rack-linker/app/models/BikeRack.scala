package models

import play.api.libs.json._
import org.aksw.jena_sparql_api.mapper.annotation.Datatype
import org.aksw.jena_sparql_api.mapper.annotation.DefaultIri
import org.aksw.jena_sparql_api.mapper.annotation.Iri
import org.aksw.jena_sparql_api.mapper.annotation.RdfType

import scala.annotation.meta.beanGetter
import scala.beans.BeanProperty

@RdfType("lgdo:BicycleParking") // TODO Default Iri?
case class BikeRack(
    @(Iri @beanGetter)("lgdo:bicycleParking")@BeanProperty var rackType: String,
    @(Iri @beanGetter)("dbo:thumbnail")@BeanProperty var thumbnail: String) {
  def this() = this("", "")
}

object BikeRack {
  implicit val bikeRackFormat = Json.format[BikeRack]
}

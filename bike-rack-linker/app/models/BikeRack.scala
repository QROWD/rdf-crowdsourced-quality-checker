package models

import play.api.libs.json._
import org.aksw.jena_sparql_api.mapper.annotation.Datatype
import org.aksw.jena_sparql_api.mapper.annotation.DefaultIri
import org.aksw.jena_sparql_api.mapper.annotation.Iri
import org.aksw.jena_sparql_api.mapper.annotation.RdfType

import scala.annotation.meta.beanGetter
import scala.beans.BeanProperty

@RdfType("lgdo:BicycleParking")
case class BikeRack(
    @(Iri @beanGetter)("lgdo:bicycleParking")@BeanProperty var bikeRackType:BikeRackType,
    @(Iri @beanGetter)("geo:wgs84_pos#lat")@BeanProperty var lat:           Double,
    @(Iri @beanGetter)("geo:wgs84_pos#long")@BeanProperty var long:         Double,
    @(Iri @beanGetter)("lgdo:capacity")@BeanProperty var capacity:          Int,
    @(Iri @beanGetter)("dbo:thumbnail")@BeanProperty var thumbnail:         String) {
  def this() = this(new BikeRackType(), 0, 0, 0, "")
}

object BikeRack {
  implicit val bikeRackFormat = Json.format[BikeRack]
}

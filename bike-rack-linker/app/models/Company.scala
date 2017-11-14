package models

import org.aksw.jena_sparql_api.mapper.annotation.Datatype
import org.aksw.jena_sparql_api.mapper.annotation.DefaultIri
import org.aksw.jena_sparql_api.mapper.annotation.Iri
import org.aksw.jena_sparql_api.mapper.annotation.RdfType

@RdfType("dbo:Company")
@DefaultIri("dbr:#{label}")
case class Company(
    @Iri("rdfs:label") label:                                    String,
    @Iri("dbo:foundingYear")@Datatype("xsd:gYear") foundingYear: Int,
    @Iri("dbo:numberOfLocations") numberOfLocations:             Int)

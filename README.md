# rdf-crowdsourced-quality-checker

This project is a prototype for crowdsourcing the quality assessment of linked data. The creation of new Tasks for the crowd will be in a streaming manner, compared to the batched creation typically employed in these scenarios. This prototype solves a specific problem and will be generalized later.

This first use case is the deduplication of resources representing bike-racks in the Trento area. The Trento administration would like to know how many bike-racks exists. The discovery of bike-racks is crowdsourced. When a crowdworker create a new entry for a bike-rack, this resource is checked by an unique-id-service against existing resources. If this service is not confident to which existing resource this bike-rack belongs, it sends a request to a web-service this project implements. The payload of this request consists of an ID for a bike-rack resource and a list of candidates, which could be the same resource.

The resources are enriched with properties from sparql endpoint, which enables the crowdworker do decide which resources are identical. The service then creates a Pybossa task with the Pybossa RESTful API. The following image shows how an enriched task is presented to a crowdworker:

![Pybossa Task](PybossaTask2.png)

## Setup

There are two subfolders in this repository. In `bike-racks` lives the Pybossa project definition. In `bike-rack-linker` is a play application which enriches and creates pybossa tasks.

### Pybossa

1. Install the [Pybossa development server](http://docs.pybossa.com/en/latest/vagrant_pybossa.html).
1. Setup the [Pybossa command line client](https://github.com/Scifabric/pbs) (py2 only).
1. Change directory to `bike-racks` and add the project to the Pybossa Server
    1. pbs create_project
    1. pbs add_tasks --tasks-file test-data.json 
    1. pbs update_project --watch

### PybossaRDFAdapter

1. Install [Sbt 1.0](http://www.scala-sbt.org/1.0/docs/Getting-Started.html).
1. Clone the repository.
1. Define environment in `application.conf`.
1. `cd rdf-crowdsourced-quality-checker/bike-rack-linker`
1. `sbt run`

## Architecture

![SequenceDiagram](SequenceDiagram.png)

The properties are queried with the [Java-RDF Mapper module](https://github.com/SmartDataAnalytics/jena-sparql-api/tree/master/jena-sparql-api-mapper).

### Api

- to Broker
  - [linkEvaluationRequest](https://github.com/QROWD/rdf-crowdsourced-quality-checker/blob/master/bike-rack-linker/test/resources/unique-id-service-output.json)
  - publishResult
- to Quadstore
  - queryProperties

### Disambiguation Result

todo

## Todos

Are on [Trello](https://trello.com/c/cJqG1a03/5-d71-data-quality-assessment-services)

## Ressources

- [Sequence Diagramm](https://drive.google.com/file/d/0B4egcZEKnBC_XzVaazNhcFctazA/view?usp=sharing)
- [Pybossa](Pybossa)
- [tombatossals (David Rubert)](https://github.com/tombatossals)
- [Bike-Racks in Italy Ckan](http://ckan.qrowd.aksw.org/dataset/bikeracks-in-trento-from-openstreetmap)
- [LinkedGeoData mappings](https://github.com/GeoKnow/LinkedGeoData/blob/develop/linkedgeodata-core/src/main/resources/org/aksw/linkedgeodata/sql/Mappings.sql)
- [OpenStreetMap bicycle amenity](http://wiki.openstreetmap.org/wiki/Tag:amenity%3Dbicycle_parking)
- [GeoFabrik Trento area](http://download.geofabrik.de/europe/italy/nord-est.html)

### Orion Context Broker

- [Forbidden characters - Fiware-Orion](https://fiware-orion.readthedocs.io/en/master/user/forbidden_characters/index.html)
- [amaxilat/orion-client: Java Client for the Orion Context Broker Publish/Subscribe Context Broker GE](https://github.com/amaxilat/orion-client)
- [fiware/orion - Docker Hub](https://hub.docker.com/r/fiware/orion/)

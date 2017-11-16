name := """bike-rack-linker"""
organization := "QROWD"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")
/* resolvers += "amaxilatis snapshots" at "http://maven.amaxilatis.com/nexus/content/repositories/snapshots/" */

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.aksw.jena-sparql-api" % "jena-sparql-api-mapper" % "3.4.0-1"
/* libraryDependencies += "com.amaxilatis" % "orion-client" % "1.0-SNAPSHOT" */

fork in Test := false

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "QROWD.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "QROWD.binders._"

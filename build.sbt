ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.5.0"
val SlickVersion = "3.4.1"
val PostgresqlVersion = "42.5.4"
val TypeSafeConfigVersion = "1.4.2"


lazy val root = (project in file("."))
  .settings(
    name := "Retsu"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.slick" %% "slick" % SlickVersion,
  "org.postgresql" % "postgresql" % PostgresqlVersion,
  "com.typesafe" % "config" % TypeSafeConfigVersion,
  "org.slf4j" % "slf4j-api" % "2.0.5",
  "org.slf4j" % "slf4j-log4j12" % "2.0.5",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",
  "de.heikoseeberger" %% "akka-http-circe" % "1.39.2",
  "io.circe" %% "circe-generic" % "0.14.4"
)
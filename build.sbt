ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.4.0"
val SlickVersion = "3.4.1"
val slf4jnopVersion = "2.0.5"
val mySQLConnectorJava = "8.0.32"
lazy val root = (project in file("."))
  .settings(
    name := "Retsu"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.slick" %% "slick" % SlickVersion,
  "org.slf4j" % "slf4j-nop" % slf4jnopVersion,
  "mysql" % "mysql-connector-java" % mySQLConnectorJava
)
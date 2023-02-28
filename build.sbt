ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.5.0"
val SlickVersion = "3.4.1"
val PostgresqlVersion = "42.5.4"
val TypeSafeConfigVersion = "1.4.2"
val TypeSafeScalaLoggingVersion = "3.9.5"


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
  "com.typesafe.scala-logging"  %% "scala-logging" % TypeSafeScalaLoggingVersion
)
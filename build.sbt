ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.4.0"
val SlickVersion = "3.4.1"
val PostgresqlVersion = "9.3-1102-jdbc41"
val TypeSafeConfigVersion = "1.4.1"
val TypeSafeScalaLoggingVersion = "3.9.4"


lazy val root = (project in file("."))
  .settings(
    name := "Retsu"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.slick" %% "slick" % SlickVersion,
  "org.postgresql" %% "postgresql" % PostgresqlVersion,
  "com.typesafe.config" %% "config" % TypeSafeConfigVersion,
  "com.typesafe.scala-logging"  %% "scala-logging" % TypeSafeScalaLoggingVersion
)

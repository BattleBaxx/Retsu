package service

import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global

object DatabaseService {
  private val logger = LoggerFactory.getLogger(getClass)

  private val config: Config = ConfigFactory.load()
  private val dbUrl = config.getString("database.url")
  private val dbUser = config.getString("database.user")
  private val dbPassword = config.getString("database.password")
  private val db = Database.forURL(dbUrl, dbUser, dbPassword, driver = "org.postgresql.Driver")

  // Close the database connection when the application terminates
  sys.addShutdownHook(db.close())

  def getDB(): slick.jdbc.PostgresProfile.backend.DatabaseDef = {
    db;
  }
}
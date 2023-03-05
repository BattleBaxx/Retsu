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

  private val tableName = "queues"

//  private val tableExistsQuery = sql"""SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = '#$tableName')"""
  private val tableExistsQuery = sql"""SELECT table_name, table_schema FROM information_schema.tables WHERE table_schema = 'public' AND table_name = '#$tableName'"""
  private val tableExistsFuture = db.run(tableExistsQuery.as[(String,String)])
  
  tableExistsFuture.onComplete {
    case Success(tableExists) =>
      logger.debug(tableExists.toString())
      val (table_name, table_schema) =  tableExists.head
      println(table_name)
      println(table_schema)
      if (table_name == "queues") {
        logger.info(s"Queue table exists!")
      } else {
        logger.error(s"Queue table does not exist!, exiting...")
        System.exit(0)
      }
    case Failure(ex) =>
      logger.error("An error occurred while checking for the existence of the table.", ex)
      System.exit(0)
  }

  // Close the database connection when the application terminates
  sys.addShutdownHook(db.close())

  def getDB(): slick.jdbc.PostgresProfile.backend.DatabaseDef = {
    db;
  }
}
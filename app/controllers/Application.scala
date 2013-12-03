package controllers

import play.api._
import play.api.db.DB
import play.api.Play.current
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.net.URL
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import models.ShortenedURL
import scala.slick.driver.PostgresDriver.simple._
import Database.threadLocalSession
import models.ShortenedURLs

object Application extends Controller {
  implicit lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val baseUrl = current.configuration.getString("application.url")

  def ensureProtocol(s: String) = if (!s.contains("://")) "http://" + s else s

  def URL(s: String) = new URL(ensureProtocol(s))

  val urlForm = Form(
    "url" -> nonEmptyText.verifying("Not a valid URL!", s => Try(URL(s)) match {
      case Success(_) => true
      case Failure(_) => false
    }))

  def index = Action {
    Ok(views.html.index(urlForm))
  }

  def newURL = Action { implicit request =>
    urlForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(errors)),
      url => {
        val id = database withSession {
          ShortenedURLs.getOrCreate(URL(url))
        }
        Redirect(routes.Application.show(id))
      })
  }

  def getSURL(id: String, success: (ShortenedURL) => SimpleResult) = database withSession {
    ShortenedURLs.get(id)
  }.map(success).getOrElse(NotFound(s"URL $id not found."))

  def show(id: String) = Action { getSURL(id, s => Ok(views.html.show(s, baseUrl.getOrElse("")))) }

  def redirect(id: String) = Action { implicit request =>
    {
      getSURL(id, (s => {
        database withSession {
          ShortenedURLs.visit(s.id, request.headers.get("REFERER").flatMap(u => Try(URL(u)).toOption))
        }
        Found(s.url.toString)
      }))
    }
  }
}

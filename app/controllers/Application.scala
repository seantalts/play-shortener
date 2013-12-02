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
import models.ShortenedURL

object Application extends Controller {
  implicit lazy val database = Database.forDataSource(DB.getDataSource())

  val urlForm = Form(
    "url" -> nonEmptyText.verifying("Not a valid URL!", s => Try(new URL(s)) match {
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
          ShortenedURLs.getOrCreate(url)
        }
        Redirect(routes.Application.show(id))
      })
  }

  def getSURL(id: String, success: (ShortenedURL) => SimpleResult) = Action {
    try {
      val surl = database withSession {
        ShortenedURLs.get(id)
      }
      success(surl)
    } catch {
      case e: java.util.NoSuchElementException => NotFound(s"URL $id not found.")
    }
  }

  def show(id: String) = getSURL(id, s => Ok(views.html.show(s)))

  def redirect(id: String) = getSURL(id, s=> Found(s.url.toString))

}
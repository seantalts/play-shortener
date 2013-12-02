package models

import java.net.URL
import scala.slick.driver.PostgresDriver.simple._
import org.postgresql.util.PSQLException
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List

case class ShortenedURL(id: String, url: URL)

object URLMapper {
  implicit def URL2string = MappedTypeMapper.base[URL, String](
    url => url.toString,
    string => new URL(string))
  def hex2Int(s: String) = Integer.parseInt(s, 16)
  def int2Hex(i: Int) = BigInt(i).toString(16)
  implicit def hexIDMapper = MappedTypeMapper.base[String, Int](hex2Int, int2Hex)
}

object ShortenedURLs extends Table[ShortenedURL]("shortened_urls") {
  import URLMapper._

  def id = column[String]("id", O.PrimaryKey, O.AutoInc)
  def url = column[URL]("url", O.DBType("text"))
  def uidx = index("idx_url", url, unique = true)

  def * = id ~ url <> (ShortenedURL, ShortenedURL.unapply _)
  def forInsert = url returning id

  def get(url: URL)(implicit session: Session) = (for {
    u <- ShortenedURLs
    if u.url === url
  } yield u.id).first

  def get(id: String)(implicit session: Session) = (for {
    u <- ShortenedURLs
    if u.id === id
  } yield u).first

  def getOrCreate(url: URL)(implicit session: Session): String = {
    try {
      get(url)
    } catch {
      case e: NoSuchElementException => ShortenedURLs.forInsert.insert(url)
    }
  }
}
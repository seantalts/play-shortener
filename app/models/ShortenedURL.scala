package models

import java.net.URL
import scala.slick.driver.PostgresDriver.simple._
import org.postgresql.util.PSQLException

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
  def uidx = index("surls_url_idx", url, unique = true)

  def * = id ~ url <> (ShortenedURL, ShortenedURL.unapply _)
  def forInsert = url returning id

  type Q = Query[ShortenedURLs.type, ShortenedURL]

  def getFirst(modify: Q => Q)(implicit session: Session) = modify(Query(ShortenedURLs)).firstOption

  def get(url: URL)(implicit session: Session) = getFirst(_.filter(_.url === url))

  def get(id: String)(implicit session: Session) = getFirst(_.filter(_.id === id))

  def getOrCreate(url: URL)(implicit session: Session): String = get(url) match {
    case Some(su) => su.id
    case None => ShortenedURLs.forInsert.insert(url)
  }

  def visit(id: String, referrer: Option[URL] = None)(implicit session: Session) = {
    Clicks.insert(Click(hex2Int(id), referrer))
  }
}
package models

import java.net.URL
import slick.driver.PostgresDriver._
import com.github.nscala_time.time.Imports.DateTime
import java.sql.Timestamp
import java.util.Calendar
import com.github.tminglei.slickpg.date.TimestampTypeMapper

object DateMapperImplicits {
  implicit val jodaDateTimeTypeMapper = new TimestampTypeMapper(sqlTimestamp2jodaDateTime, jodaDateTime2sqlTimestamp)

  private def sqlTimestamp2jodaDateTime(ts: Timestamp): DateTime = {
    val cal = Calendar.getInstance()
    cal.setTime(ts)
    new DateTime(
      cal.get(Calendar.YEAR),
      cal.get(Calendar.MONTH),
      cal.get(Calendar.DAY_OF_MONTH),
      cal.get(Calendar.HOUR_OF_DAY),
      cal.get(Calendar.MINUTE),
      cal.get(Calendar.SECOND),
      cal.get(Calendar.MILLISECOND)
    )
  }
  private def jodaDateTime2sqlTimestamp(dt: DateTime): Timestamp = {
    val cal = Calendar.getInstance()
    cal.set(dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth, dt.getHourOfDay, dt.getMinuteOfHour, dt.getSecondOfMinute)
    cal.set(Calendar.MILLISECOND, dt.getMillisOfSecond)
    new Timestamp(cal.getTimeInMillis)
  }
}

case class Click(id: Int, referrer: Option[URL], timestamp: DateTime = DateTime.now) 
  
object Clicks extends Table[Click]("clicks") {
  import URLMapper.URL2string
  import DateMapperImplicits._

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def url_id = column[Int]("url_id")
  def uidx = index("clicks_uid_idx", url_id, unique=false)
  def referrer = column[Option[URL]]("referrer", O.DBType("text"))
  def timestamp = column[DateTime]("timestamp", O.DBType("timestamp with time zone"))
  def * = url_id ~ referrer ~ timestamp <> (Click, Click.unapply _)
}
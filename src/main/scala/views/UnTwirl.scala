package views

import play.api.templates.Html
import scala.language.implicitConversions

object UnTwirl {
  def apply(block: => Any): String = {
    val unTwirl = new UnTwirl
    block
    unTwirl.sb.toString()
  }
}

trait UnTwirlImplicits {
  implicit def sbToString(sb: StringBuilder): String = sb.toString()

  implicit def appendableToString(appendable: play.api.templates.HtmlFormat.Appendable): String = appendable.toString()

  //implicit def htmlToString(html: Html): String = html.toString()

  implicit def sbToHtml(sb: StringBuilder): Html = Html(sb.toString())

  implicit def stringToHtml(string: String): Html = Html(string)
}

class UnTwirl extends UnTwirlImplicits {
  val sb = new StringBuilder("")

  // TODO incorporate https://gist.github.com/javierfs89/eca13fa3429af26b9ac9
  def ++(s: => String=""): StringBuilder = sb.append(s)

  def unIf(predicate: Boolean)(thenClause: => String): String = if (predicate) thenClause else ""

  override def toString: String = sb.toString()
}

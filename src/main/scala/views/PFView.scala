package views

import play.api.templates.Html
import scala.language.implicitConversions

object PFView {
  def apply(block: => Any): String = {
    val pfView = new PFView{}
    pfView.++(block.toString)
    pfView.toString()
  }
}

trait PFViewImplicits {
  implicit def pfViewToHtml(pfView: PFView): String = Html(pfView.toString())

  implicit def pfViewToString(pfView: PFView): String = pfView.toString()

  implicit def sbToString(sb: StringBuilder): String = sb.toString()

  implicit def appendableToString(appendable: play.api.templates.HtmlFormat.Appendable): String = appendable.toString()

  //implicit def htmlToString(html: Html): String = html.toString()

  implicit def sbToHtml(sb: StringBuilder): Html = Html(sb.toString())

  implicit def stringToHtml(string: String): Html = Html(string)
}

trait PFView extends PFViewImplicits {
  implicit val sb = new StringBuilder("")

  // TODO incorporate https://gist.github.com/javierfs89/eca13fa3429af26b9ac9
  @inline def ++(s: => String=""): StringBuilder = sb.append(s)

  @inline def unIf(predicate: Boolean)(thenClause: => String): String = if (predicate) thenClause else ""

  @inline def If(predicate: Boolean)(thenClause: => String): String = unIf (predicate) (thenClause)

  def toHtml = Html(toString)

  override def toString: String = sb.toString()
}

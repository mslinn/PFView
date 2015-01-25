package views

import play.api.i18n.Lang
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

  /** Include a local file, using a localized version if it exists.
    * For example, specify `filePath` `blah.html` and `lang` `en-US` to search for `blah_en-US.html` with a fallback to `blah_en.html` and then `blah.html`.
    * @param filePath can be a generic i18n path.
    * @param baseDir Default is to look in the Play app's `public` directory
    * @param lang Language to consider for filePath l10n; does not need to contain a country code */
  def includeFile(filePath: String, baseDir: String = "public")(implicit lang: Lang=Lang("en")): StringBuilder = {
    import java.io.File
    import play.api.Play
    import play.api.Play.current

    // Split filePath at name and filetype to insert the language in between them
    val (fileName, fileType) = filePath.splitAt(filePath.lastIndexOf("."))

    val l10n = "_" + lang.language

    val l10nCountry =
      if (lang.country.nonEmpty) "_" + lang.language + "-" + lang.country else l10n

    // Retrieve the file with the current language & country, or just the generic language version, or just the originally specified version
    val maybeFile: Option[File] = {
      Play.getExistingFile(            s"$baseDir/$fileName$l10nCountry$fileType")
          .orElse(Play.getExistingFile(s"$baseDir/$fileName$l10n$fileType"))
          .orElse(Play.getExistingFile(s"$baseDir/$filePath"))
    }

    val x = maybeFile

    // Read the file's content and wrap it in HTML or return an error message
    val result = maybeFile.map { file: File =>
      import scala.io.Source.fromFile
      val file2 = if (file.isDirectory) new File(file, "index.html") else file
      val content = fromFile(file2).mkString
      Html(content)
    }.getOrElse(s"""PFVIew file include failed; baseDir='$baseDir'; filePath='$filePath'""")

    sb.append(result)
  }

  /** Include the contents of a URL; relative URLs are not supported.
    * @param url String representation of URL to fetch
    * @param encoding defaults to UTF-8 */
  def includeUrl(url: String, encoding: String="UTF-8"): StringBuilder =
    sb.append(try {
      io.Source.fromURL(url, encoding: String).mkString
    } catch {
      case e: Exception =>
        s"""PFVIew URL include failed; ${e.getClass.getName}: ${e.getMessage} for $url with encoding $encoding"""
    })

  @inline def unIf(predicate: Boolean)(thenClause: => String): String = if (predicate) thenClause else ""

  @inline def If(predicate: Boolean)(thenClause: => String): String = unIf (predicate) (thenClause)

  def toHtml = Html(toString)

  override def toString: String = sb.toString()
}

package views

import play.api.i18n.Lang
import play.twirl.api._
import scala.language.implicitConversions
import java.io.File
import play.api.Play
import play.api.Play.current

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

  implicit def appendableToString(appendable: HtmlFormat.Appendable): String = appendable.toString()

  //implicit def htmlToString(html: Html): String = html.toString()

  implicit def sbToHtml(sb: StringBuilder): Html = Html(sb.toString())

  implicit def stringToHtml(string: String): Html = Html(string)
}

trait PFView extends PFViewImplicits {
  implicit val sb = new StringBuilder("")

  /** Side effect: appends contents of String to PFView's StringBuffer. */
  // TODO incorporate https://gist.github.com/javierfs89/eca13fa3429af26b9ac9
  @inline def ++(s: => String=""): StringBuilder = sb.append(s)

  /** @return Some(File) for local file if it exists; cache results for relative filePaths.
    * @param baseDir directory to look for file; can be relative or absolute. Default is to look in the Play app's `public` directory */
  def includeFile(filePath: String, baseDir: String = "public", memoize: Boolean=true): StringBuilder = {
    val path: String = if (baseDir.isEmpty) filePath else {
      if (filePath.startsWith(File.separator)) s"$baseDir$filePath" else s"$baseDir${ File.separator }$filePath"
    }
    includePath(path) { case (fileName, fileType) => import play.api.Play
      import play.api.Play.current
      Play.getExistingFile(s"$fileName$fileType")
    }
  }

  /** @return contents of localized File */
  @inline def _localizedFile(lang: Lang): ((String, String)) => StringBuilder = tuple2 => {
    val (filePath: String, baseDir: String) = tuple2
    includePath(s"$baseDir${ File.separator }$filePath") { case (fileName, fileType) =>
      val l10n = "_" + lang.language
      val l10nCountry =
        if (lang.country.nonEmpty) "_" + lang.language + "-" + lang.country else l10n

      // Retrieve the File for the current language & country, or just the generic language version, or just the originally specified version
      val file = Play.getExistingFile( s"$fileName$l10nCountry$fileType")
          .orElse(Play.getExistingFile(s"$fileName$l10n$fileType"))
          .orElse(Play.getExistingFile(s"$filePath"))
      file
    }
  }

  /** @return contents of localized File */
  private def memoizedLocalizedFile(lang: Lang) = Memoize(_localizedFile(lang))

  /** @return File for a local file, or a localized version if it exists.
    * For example, specify `filePath` `blah.html` and `lang` `en-US` to search for `blah_en-US.html` with a fallback to `blah_en.html` and then `blah.html`.
    * Side effect: appends contents of file to PFView's StringBuffer.
    * @param filePath can be a generic i18n path.
    * @param baseDir Default is to look in the Play app's `public` directory
    * @param lang Language to consider for filePath l10n; does not need to contain a country code */
  def localizedFile(filePath: String, baseDir: String = "public", memoize: Boolean=true)(implicit lang: Lang=Lang("en")): StringBuilder = {
    if (filePath.startsWith(File.separator) || !memoize) _localizedFile(lang)((filePath, baseDir))
    else memoizedLocalizedFile(lang)((filePath, baseDir))
  }

  /** Include a local file if it exists. File contents are memoized if file is local.
    * Side effect: appends contents of file to PFView's StringBuffer.
    * @param path can be a generic i18n path, either absolute or relative.
    * @param pathNameToMaybeFile Function2 accepts (pathAndName, fileType) => Option[File]; performs whatever magic is required */
  def includePath(path: String)(pathNameToMaybeFile: ((String, String)) => Option[File]): StringBuilder = {
    val lastDotIndex = path.lastIndexOf(".")

    /** @return tuple containing path/filename and filetype */
    val nameType: (String, String) = if (lastDotIndex>=0) path.splitAt(lastDotIndex) else (path, "")

    val result = pathNameToMaybeFile(nameType).map { file: File =>
      val file2 = if (file.isDirectory) new File(file, "index.html") else file
      val content = scala.io.Source.fromFile(file2).mkString
      content
    }.getOrElse(s"""PFView file failed to include '$path'""")

    sb.append(result)
  }

  /** Include the contents of a URL; relative URLs are not supported.
    *
    * @param url String representation of URL to fetch
    * @param encoding defaults to UTF-8 */
  def includeUrl(url: String, encoding: String="UTF-8"): StringBuilder =
    sb.append(try {
      scala.io.Source.fromURL(url, encoding: String).mkString
    } catch {
      case e: Exception =>
        s"""PFVIew URL include failed; ${e.getClass.getName}: ${e.getMessage} for $url with encoding $encoding"""
    })

  /** Side effect: appends contents of thenClause to PFView's StringBuffer if predicate is true. */
  @inline def unIf(predicate: Boolean)(thenClause: => String): String = if (predicate) thenClause else ""

  /** Side effect: appends contents of thenClause to PFView's StringBuffer if predicate is true. */
  @inline def If(predicate: Boolean)(thenClause: => String): String = unIf (predicate) (thenClause)

  /** @return contents of StringBuffer as Html */
  def toHtml = Html(toString)

  /** @return contents of StringBuffer as String */
  override def toString: String = sb.toString()
}

package views

import java.io.File
import com.micronautics.cache._
import play.api.Environment
import play.api.i18n.Lang
import play.twirl.api._
import scala.language.implicitConversions

trait PFViewImplicits {
  implicit def pfViewToHtml(pfView: PFView): String = Html(pfView.toString())

  implicit def pfViewToString(pfView: PFView): String = pfView.toString()

  implicit def sbToString(sb: StringBuilder): String = sb.toString()

  implicit def appendableToString(appendable: HtmlFormat.Appendable): String = appendable.toString()

  //implicit def htmlToString(html: Html): String = html.toString()

  implicit def sbToHtml(sb: StringBuilder): Html = Html(sb.toString())

  implicit def stringToHtml(string: String): Html = Html(string)
}

object PFView {
  def apply(block: => Any)(implicit env: Environment): String = {
    val pfView = new PFView()(env)
    pfView.++(block.toString)
    pfView.toString()
  }
}

class PFView(implicit env: Environment) extends PFViewImplicits {
  implicit val sb = new StringBuilder("")

  /** Side effect: appends contents of String to PFView's StringBuffer. */
  // TODO incorporate https://gist.github.com/javierfs89/eca13fa3429af26b9ac9
  @inline def ++(s: => String=""): StringBuilder = sb.append(s)

  @inline private val includeFileInline = (baseDir: String, path: String) =>
    includeFileFn(path) { case (fileName, fileType) =>
      env.getExistingFile(s"$baseDir${ File.separator }$fileName$fileType")
    }

  private val memoizedIncludeFile = (baseDir: String, path: String) =>
    Memoizer(includeFileInline(baseDir, path))

  /** Include a local file if it exists; cache results for relative filePaths.
    * Side effect: appends contents of file to PFView's StringBuffer.
    * @param baseDir can be relative or absolute. Default is to look in the Play app's `public` directory */
  def includeFile(filePath: String, baseDir: String = "public", memoize: Boolean=true): String = {
    val path: String = s"$baseDir${ File.separator }$filePath"
    if (filePath.startsWith(File.separator) || !memoize) includeFileInline(baseDir, path)
    else memoizedIncludeFile(baseDir, path).toString
  }

  @inline def includeLocalizedFileInline(lang: Lang): ((String, String)) => StringBuilder = tuple2 => {
    val (filePath: String, baseDir: String) = tuple2
    includeFileFn(s"$baseDir${ File.separator }$filePath") { case (fileName, fileType) =>
      val l10n = "_" + lang.language

      val l10nCountry =
        if (lang.country.nonEmpty) "_" + lang.language + "-" + lang.country else l10n

      // Retrieve the file with the current language & country, or just the generic language version, or just the originally specified version
      env.getExistingFile(            s"$baseDir/$fileName$l10nCountry$fileType")
          .orElse(env.getExistingFile(s"$baseDir/$fileName$l10n$fileType"))
          .orElse(env.getExistingFile(s"$baseDir/$filePath"))
    }
  }

  private def memoizedIncludeLocalizedFile(lang: Lang) = Memoizer(includeLocalizedFileInline(lang))

  /** Include a local file, using a localized version if it exists.
    * For example, specify `filePath` `blah.html` and `lang` `en-US` to search for `blah_en-US.html` with a fallback to `blah_en.html` and then `blah.html`.
    * Side effect: appends contents of file to PFView's StringBuffer.
    * @param filePath can be a generic i18n path.
    * @param baseDir Default is to look in the Play app's `public` directory
    * @param lang Language to consider for filePath l10n; does not need to contain a country code */
  def includeLocalizedFile(filePath: String, baseDir: String = "public", memoize: Boolean=true)(implicit lang: Lang=Lang("en")): StringBuilder = {
    if (filePath.startsWith(File.separator) || !memoize) includeLocalizedFileInline(lang)((filePath, baseDir))
    else memoizedIncludeLocalizedFile(lang)((filePath, baseDir))
  }

  /** Include a local file if it exists. File contents are memoized if file is local.
    * Side effect: appends contents of file to PFView's StringBuffer.
    * @param path can be a generic i18n path, either absolute or relative.
    * @param fn Function2 accepts (fileName, fileType) => Option[File]; performs whatever magic is required */
  def includeFileFn(path: String)(fn: ((String, String)) => Option[File]): StringBuilder = {
    val lastDotIndex = path.lastIndexOf(".")
    val nameType: (String, String) = if (lastDotIndex>=0) path.splitAt(lastDotIndex) else (path, "")
    val maybeFile: Option[File] = fn(nameType)

    val result = maybeFile.map { file: File =>
      import scala.io.Source.fromFile
      val file2 = if (file.isDirectory) new File(file, "index.html") else file
      val content = fromFile(file2).mkString
      content
    }.getOrElse(s"""PFVIew file failed to include '$path'""")

    sb.append(result)
  }

  /** Include the contents of a URL; relative URLs are not supported.
    * @param url String representation of URL to fetch
    * @param encoding defaults to UTF-8 */
  def includeUrl(url: String, encoding: String="UTF-8"): StringBuilder =
    sb.append(try {
      scala.io.Source.fromURL(url, encoding: String).mkString
    } catch {
      case e: Exception =>
        s"""PFVIew URL include failed; ${ e.getClass.getName }: ${ e.getMessage } for $url with encoding $encoding"""
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

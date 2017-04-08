package views

import org.junit.runner.RunWith
import org.scalatest.Matchers._
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Environment}
import scala.language.implicitConversions

@RunWith(classOf[JUnitRunner])
class TestPFView extends PlaySpec with BeforeAndAfterAll with BeforeAndAfter with OneAppPerSuite {
  val guiceApplicationBuilder = new GuiceApplicationBuilder()
  implicit val environment: Environment = guiceApplicationBuilder.environment
  override implicit lazy val app: Application = guiceApplicationBuilder.build()

  def dump(expected: String, actual: String): Boolean = {
    val isEqual: Boolean = expected == actual
    if (!isEqual) {
      print("          ")
      (0 to math.max(expected.length, actual.length)/10) foreach { i => print(s"${ i%10 }....|....") }
      println()
      println(s"Expected: $expected")
      println(s"Actual:   $actual")
    }
    isEqual
  }

  object nada extends PFView()

  object emptyView extends PFView() {
    ++()
  }

  object staticView extends PFView() {
    ++("static")
    ++(" view")
  }

  object dynamicView {
    def apply(suffix: String) = new PFView() {
      ++(s"Feeling $suffix?")
      ++(" Gotta go!")
    }
  }

  object nestedViews {
    def apply(msg: String="") = new PFView() {
      def repeatContent(msg: String): String = new PFView {
        implicit val env: Environment = environment
        ++(msg * 2)
      }.toString

      val repeatedContent: String = repeatContent(msg)
      ++(repeatedContent)
    }
  }

  def simple = new PFView() {
    ++("simple")
  }

  def includeFile = new PFView() {
    includeFile("blah.html", "src/test/resources/public")
  }

  def includeFileNoType = new PFView() {
    includeFile("blah", "src/test/resources/public")
  }

  def include_en = new PFView() {
    implicit val lang = play.api.i18n.Lang("en")
    includeFile("blah.html", "src/test/resources/public")
  }

  def `include_en-US` = new PFView() {
    implicit val lang = play.api.i18n.Lang("en-US")
    includeFile("blah.html", "src/test/resources/public")
  }

  def includeMissing = new PFView() {
    implicit val lang = play.api.i18n.Lang("fr")
    includeFile("blah.html", "src/test/resources/public")
  }

  def includeUrl = new PFView() {
    includeUrl("https://github.com/mslinn/PFView")
  }

  "PFView" should {
    "work" in {  // repeat tests to ensure buffer is initialized properly
      nada.toString === ""
      nada.toString === ""

      emptyView.toString === ""
      emptyView.toString === ""

      staticView.toString === "static view"
      staticView.toString === "static view"

      dynamicView("good").toString === "Feeling good? Gotta go!"
      dynamicView("bad").toString === "Feeling bad? Gotta go!"

      nestedViews("x").toString === "xx"
      nestedViews("x").toString === "xx"

      simple.toString === "simple"
      simple.toString === "simple"

      includeUrl.toString.toLowerCase should include("pull requests")
      includeUrl.toString.toLowerCase should include("pull requests")

      includeFile.toString === "This is the generic version of blah.html\n"
      includeFile.toString === "This is the generic version of blah.html\n"

      includeFileNoType.toString === "This is the content of blah\n"
      includeFileNoType.toString === "This is the content of blah\n"

      includeMissing.toString === "This is the generic version of blah.html\n"
      includeMissing.toString === "This is the generic version of blah.html\n"

      include_en.toString === "This is the en version of blah.html\n"
      include_en.toString === "This is the en version of blah.html\n"

      `include_en-US`.toString === "This is the en-US version of blah.html\n"
      `include_en-US`.toString === "This is the en-US version of blah.html\n"
      ()
    }
  }
}

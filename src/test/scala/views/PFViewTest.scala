package views

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.FakeApplication
import scala.language.implicitConversions

class PFViewTest extends PlaySpec with BeforeAndAfterAll with BeforeAndAfter with OneAppPerSuite {
  implicit override lazy val app: FakeApplication = FakeApplication()

  def dump(expected: String, actual: String): Boolean = {
    val isEqual: Boolean = expected == actual
    if (!isEqual) {
      print("          ")
      (0 to math.max(expected.length, actual.length)/10) foreach { i => print(s"${i%10}....|....") }
      println()
      println(s"Expected: $expected")
      println(s"Actual:   $actual")
    }
    isEqual
  }

  object nada extends PFView { }

  object emptyView extends PFView {
    ++()
  }

  object staticView extends PFView {
    ++("static")
    ++(" view")
  }

  object dynamicView {
    def apply(suffix: String) = new PFView {
      ++(s"Feeling $suffix?")
      ++(" Gotta go!")
    }
  }

  object nestedViews {
    def apply(msg: String="") = new PFView {
      def repeatContent(msg: String): String = new PFView {
        ++(msg * 2)
      }.toString

      val repeatedContent = repeatContent(msg)
      ++(repeatedContent)
    }
  }

  def simple = new PFView {
    ++("simple")
  }

  def includeFile = new PFView {
    includeFile("blah.html", "src/test/resources/public")
  }

  def includeFileNoType = new PFView {
    includeFile("blah", "src/test/resources/public")
  }

  def include_en = new PFView {
    implicit val lang = play.api.i18n.Lang("en")
    includeFile("blah.html", "src/test/resources/public")
  }

  def `include_en-US` = new PFView {
    implicit val lang = play.api.i18n.Lang("en-US")
    includeFile("blah.html", "src/test/resources/public")
  }

  def includeMissing = new PFView {
    implicit val lang = play.api.i18n.Lang("fr")
    includeFile("blah.html", "src/test/resources/public")
  }

  def includeUrl = new PFView {
    includeUrl("https://github.com/mslinn/PFView")
  }

  "PFView" should {
    "work" in {  // repeat tests to ensure buffer is initialized properly
      assert(nada.toString=="")
      assert(nada.toString=="")

      assert(emptyView.toString=="")
      assert(emptyView.toString=="")

      assert(staticView.toString=="static view")
      assert(staticView.toString=="static view")

      assert(dynamicView("good").toString=="Feeling good? Gotta go!")
      assert(dynamicView("bad").toString=="Feeling bad? Gotta go!")

      assert(nestedViews("x").toString=="xx")
      assert(nestedViews("x").toString=="xx")

      assert(simple.toString=="simple")
      assert(simple.toString=="simple")

      assert(includeUrl.toString.contains("Pull Requests"))
      assert(includeUrl.toString.contains("Pull Requests"))

      assert(includeFile.toString=="This is the generic version of blah.html\n")
      assert(includeFile.toString=="This is the generic version of blah.html\n")

      assert(includeFileNoType.toString=="This is the content of blah\n")
      assert(includeFileNoType.toString=="This is the content of blah\n")

      assert(includeMissing.toString=="This is the generic version of blah.html\n")
      assert(includeMissing.toString=="This is the generic version of blah.html\n")

      assert(include_en.toString=="This is the en version of blah.html\n")
      assert(include_en.toString=="This is the en version of blah.html\n")

      assert(`include_en-US`.toString=="This is the en-US version of blah.html\n")
      assert(`include_en-US`.toString=="This is the en-US version of blah.html\n")
    }
  }
}

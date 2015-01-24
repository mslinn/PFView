package views

import org.scalatest.WordSpec
import play.api.templates.Html
import scala.language.implicitConversions

class PFViewTest extends WordSpec {
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

  object empty extends PFView {
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

  "UnTwirl" should {
    "work" in {  // repeat tests to ensure buffer is initialized properly
      assert(nada.toString=="")
      assert(nada.toString=="")
      assert(empty.toString=="")
      assert(empty.toString=="")
      assert(staticView.toString=="static view")
      assert(staticView.toString=="static view")
      assert(dynamicView("good").toString=="Feeling good? Gotta go!")
      assert(dynamicView("bad").toString=="Feeling bad? Gotta go!")
      assert(nestedViews("x").toString=="xx")
      assert(nestedViews("x").toString=="xx")
      assert(simple.toString=="simple")
      assert(simple.toString=="simple")
    }
  }
}

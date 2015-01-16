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

  /** Only use this pattern in a single-threaded environment */
  object ick extends PFView {
    ++("ick")
  }

  object blah extends PFView {
    def apply() = Html {
      ++("blah")
    }
  }

  object nested extends PFView {
    def apply(msg: String=""): Html = Html {

      /** If this method is the last thing in the apply method, it won't compile!!! */
      def content(msg: String): String = PFView {
        ++(msg * 2)
      }

      val moreContent: String = content(msg)
      ++(moreContent)
    }
  }

  def huh = new PFView {
    ++("huh")
  }

  "UnTwirl" should {
    "work" in {
      assert(nada.toString=="")
      assert(empty.toString=="")
      assert(huh.toString=="huh")
      assert(ick.toString=="ick")
      assert(blah().toString=="blah")
      assert(nested("x").toString=="xx")
    }
  }
}

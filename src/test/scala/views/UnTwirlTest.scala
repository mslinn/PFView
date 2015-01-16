package views

import org.scalatest.WordSpec
import play.api.templates.Html
import scala.language.implicitConversions

class UnTwirlTest extends WordSpec {

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

  object nada extends UnTwirl { }

  object empty extends UnTwirl {
    ++()
  }

  /** Only use this pattern in a single-threaded environment */
  object ick extends UnTwirl {
    ++("ick")
  }

  object blah extends UnTwirl {
    def apply() = Html {
      ++("blah")
    }
  }

  object adminGroupDetails extends UnTwirl {
    def apply(msg: String=""): Html = Html {

      /** If this method is the last thing in the apply method, it won't compile!!! */
      def content(msg: String): String = UnTwirl {
        ++(msg * 2)
      }

      val groupContent: String = content(msg)
      ++(groupContent)
    }
  }

  def huh = new UnTwirl {
    ++("huh")
  }

  "UnTwirl" should {
    "work" in {
      assert(nada.toString=="")
      assert(empty.toString=="")
      assert(huh.toString=="huh")
      assert(ick.toString=="ick")
      assert(blah().toString=="blah")
      assert(adminGroupDetails("x").toString=="xx")
    }
  }
}

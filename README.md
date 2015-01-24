![PFView logo](https://raw.githubusercontent.com/mslinn/PFView/master/images/pfview_355x148.png "PFView Logo")

`PFView` is a drop-in replacement for Play Framework's [Twirl template language](https://github.com/playframework/twirlhttps://github.com/playframework/twirl).
`PFView` is:

 * Simpler
 * Faster, because conversions between XML elements, `Html` and `String` can be eliminated. The job of a view is to generate a `String` that is sent to a client.
 * Testable
 * Debuggable
 * Optimizable, by defining portions as `lazy val`s which are evaluated only once instead of every time a page is parsed.
 * 100% Scala, so IDEs know how to refactor views defined by PFView

The job of a view is to work with data passed from a controller, as well as global state, and render output.
No business logic should exist in a view, however traversing data structures requires an expressive computing language.

`PFView` was created to overcome `Twirl`'s shortcomings:
 * Generates Scala code which is an unreadable mess, that is horrible to debug
 * Cannot be refactored by any available IDE
 * Components must be stored in separate files, instead of merely defining a class or method
 * DSL is less expressive than Scala, and is not a successful functional language.
 * All of a web page's contents are created dynamically; no portion can be designated as being immutable

As a result, a non-trivial `Twirl` template becomes an unholy mess that is difficult to maintain.

As well, `Twirl` has an awkward syntax and limited capabilities compared to other view templating languages, such as ASP, JSP, JSP EL, etc.
PFView is 100% Scala.

## Installing ##

Add two lines to `build.sbt`.

 * Add the `pfview` dependency:
````
"com.micronautics" %% "pfview" % "0.0.2" withSources()
````

 * Add this to the `resolvers`:
````
"micronautics/play on bintray" on "http://dl.bintray.com/micronautics/play"
````

This library has been built against Scala 2.10.4 / Play 2.2.6 and Scala 2.11.5 / Play 2.3.7.

## Working with PFView ##
### Creating an Instance ###
Create an `PFView` instance and invoke the `++` method to provide content to be appended to the PFView's instance's internal `StringBuilder` buffer.
When the PFView instance has been created, it returns the contents of the buffer as a `String` - just send that `String` to the web client.
That's all there is to it!

There are several ways of creating `PFView` instances. Examples of all of these are provided in the unit tests.

 * To define a static view, define an `object` that extends `PFView`.
````
object staticView extends PFView {
    ++("<h1>This is a test</h1>")
    ++{s"""<p>The time is now ${new java.util.Date}
          |This is another line</p>
          |${ unIf (6==9) { "Somehow 6 equals 9" } }""".stripMargin}
}
````

 * To define a Twirl-compatible dynamic view, define an `object` that does not extend `PFView`.
   The `object` needs to define a method called `apply` that returns `Html`.
````
object dynamicView {
  def apply(suffix: String): Html = new PFView {
    ++(s"Feeling $suffix?")
  }.toHtml
}
````
Of course, `apply` can be defined have as many arguments and argument lists as required, including typical play signatures such as:
`def apply(suffix: String)(implicit request: RequestHeader): Html`

 * Define a method that creates an anonymous subclass of `PFView`, which is then implicitly converted to `String`.
   This is useful for complex, dynamic content.

````
def simple = new PFView {
  ++("simple")
}
````

* `PFView` instances can be recursively nested:
````
object nestedViews {
  def apply(msg: String="") = new PFView {
    def repeatContent(msg: String): String = new PFView {
      ++(msg * 2)
    }.toString

    val repeatedContent = repeatContent(msg)
    ++(repeatedContent)
  }
}
````

### Methods ###
The following methods are provided by `PFView`:

 * `++` - adds content to the buffer
 * `If` - a convenience method; `If (condition) { thenClause }` is equivalent to `if (condition) thenClause else ""`.
This method is useful within string interpolation. Unlike Twirl's `@if` expression, spaces can exist anywhere in an `If` expression.

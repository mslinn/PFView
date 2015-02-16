![PFView logo](https://raw.githubusercontent.com/mslinn/PFView/master/images/pfview_355x148.png "PFView Logo")

This project is sponsored by [Micronautics Research Corporation](http://www.micronauticsresearch.com/),
the company that delivers online Scala and Play training via [ScalaCourses.com](http://www.ScalaCourses.com).
You can learn exactly how this project works by taking the [Introduction to Scala](http://www.ScalaCourses.com/showCourse/40),
[Intermediate Scala](http://www.ScalaCourses.com/showCourse/45) and [Introduction to Play](http://www.ScalaCourses.com/showCourse/39) courses.

`PFView` is a drop-in replacement for Play Framework's [Twirl template language](https://github.com/playframework/twirlhttps://github.com/playframework/twirl).
Twirl is good for simple view templates because for simple pages, the HTML structure is left mostly intact.
For more complex pages, `PFView` is:

 * Simpler to write
 * Faster, because conversions between XML elements, `Html` and `String` can be eliminated. The job of a view is to generate a `String` that is sent to a client.
 * Testable
 * Debuggable
 * Optimizable, by defining portions as `lazy val`s which are evaluated only once instead of every time a page is parsed.
 * 100% Scala, so IDEs know how to refactor views defined by PFView

The job of a view is to work with data passed from a controller, as well as global state, and render output.
No business logic should exist in a view, however traversing data structures requires an expressive computing language.

`PFView` was created to overcome `Twirl`'s shortcomings:
 * Generates Scala code which is an unreadable mess, which is horrible to debug
 * Cannot be refactored by any available IDE
 * Components must be stored in separate files, instead of merely defining a class or method
 * DSL is less expressive than Scala, and is not a successful functional language.
 * All of a web page's contents are created dynamically; no portion can be designated as being immutable

As a result, a non-trivial `Twirl` template becomes an unholy mess that is difficult to maintain.

As well, `Twirl` has an awkward syntax and limited capabilities compared to other view templating languages, such as ASP, JSP, JSP EL, etc.
PFView is 100% Scala.

When Adobe Flex was popular, it was common to initially write view templates in [MXML](http://en.wikipedia.org/wiki/MXML),
then rewrite them in [ActionScript](http://en.wikipedia.org/wiki/ActionScript) as complexity increased.
MXML is to Twirl as ActionScript is to PFView.

## Installing ##

Add two lines to `build.sbt`.

 * Add the `pfview` dependency:
````
"com.micronautics" %% "pfview" % "0.0.3" withSources()
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

 * To define a static view, define an `object` that extends `PFView`. This should only be done when assigning the result to a lazy val.
````
object staticView extends PFView {
    ++("<h1>This is a test</h1>")
    ++{s"""<p>The time is now ${new java.util.Date}
          |This is another line</p>
          |${ unIf (6==9) { "Somehow 6 equals 9" } }""".stripMargin}
}
````

### Methods ###
The following methods are provided by `PFView`:

 * `++` - appends content to the buffer
 * `If` - a convenience method for conditionally appending content to the buffer.
`If (condition) { thenClause }` is equivalent to `if (condition) thenClause else ""`.
This method is useful within string interpolation. Unlike Twirl's `@if` expression, spaces can exist anywhere in an `If` expression.
 * `includeFile` - append the contents of a local file into the buffer; localized versions of files are searched for,
according to standard [i18n](http://en.wikipedia.org/wiki/Internationalization_and_localization) behavior using the value of the implicit
[Lang](https://www.playframework.com/documentation/2.3.x/api/scala/index.html#play.api.i18n.Lang) parameter.
For example, if `filePath` is specified as `blah.html` and `lang` has the value `en-US` then the file `blah_en-US.html` is searched for,
then if not found `blah_en.html` is searched for and finally `blah.html` is searched for.
````
includeFile("blah.html")
````
You can also specify the `lang` argument explicitly:
````
includeFile("blah.html")(Lang("fr"))
````
By default, files are searched for in the `public` directory. You can override this by specifying a value for `baseDir`; the value can be relative or absolute.
````
includeFile("blah.html", "/var/tmp")
````
also:
````
includeFile("blah.html", "/var/tmp")(Lang("de"))
````
 * `includeUrl` - append the contents of the web page pointed to by a URL into the buffer.
Relative URLs are not supported. The default [encoding](https://www.playframework.com/documentation/2.3.x/api/scala/index.html#play.api.mvc.Codec) is UTF-8.
For example, include this `README.md` file from its GitHub repo like this:
````
includeUrl("https://raw.githubusercontent.com/mslinn/PFView/master/README.md")
````
You can specify an alternative encoding like this:
````
includeUrl("https://raw.githubusercontent.com/mslinn/PFView/master/README.md", "iso-8859-1")
````

# PFView #

`PFView` is a replacement for Play Framework's [Twirl template language](https://github.com/playframework/twirlhttps://github.com/playframework/twirl).
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
This section is just fantasy at present - the project is not yet on a repo.

Add this to `build.sbt`:

    "com.micronautics" %% "focus" % "0.0.1" withSources()

## Working with PFView ##
### Creating an Instance ###
Create an `UnTwirl` instance and invoke the `++` method to provide content to be appended to the UnTwirl's instance's internal `StringBuilder` buffer.
When the UnTwirl instance has been created, it returns the contents of the buffer as a String - just send that String to the client.
That's all there is to it!

There are several ways of creating `UnTwirl` instances:

 * To define a view, define an `object` that extends `UnTwirl`.
    The `object` needs to define a no-argument method called `apply` which returns `Html`, for Play compatibility.
````
object blah extends UnTwirl {
  def apply() = Html {
    ++("<h1>This is a test</h1>")
    ++{s"""<p>The time is now ${new java.util.Date}
          |This is another line</p>
          |${ unIf (6==9) { "Somehow 6 equals 9" } }""".stripMargin}
  }
}
````

 * Define a method that creates an anonymous subclass of `UnTwirl`, which is then implicitly converted to String. This is useful for complex, dynamic content.

````
def content(msg: String): String = UnTwirl {
  ++(msg * 2)
}
````

* `UnTwirl` instances can be recursively nested:
````
object NestedExample extends UnTwirl {
  def apply(msg: String=""): Html = Html {
    def content(msg: String): String = UnTwirl {
      ++(msg * 2)
    }

    val groupContent: String = content(msg)
    ++(groupContent)
  }
}
````

### Methods ###
The following methods are provided by `PFView`:

 * `++` - adds content to the buffer
 * `unIf` - convenience method, equivalent to `if (condition) thenClause else ""`. Useful within string interpolation.

## AntiPatterns ##
*IMPORTANT!* - Play is a multi-threaded framework. Views must either contain references to singleton objects, or reference variables on the stack or heap.

 * Defining an object that extends `UnTwirl` that does not use immutable objects in a multithreading environment is asking for trouble.

````
object ick extends UnTwirl {
  ++("ick")
}
````

Use `lazy vals` instead to ensure the expression is evaluated only once:

````
object ick {
  lazy val content = UnTwirl {
    ++("ick")
  }
  content
}
````


import sbt.Keys._

name := "PFView"

version := "0.0.5"

organization := "com.micronautics"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", "1.8",
  "-target", "1.8",
  "-g:vars"
)

scalacOptions ++= (
  scalaVersion {
    case sv if sv.startsWith("2.10") => Nil
    case _ => List(
      "-target:jvm-1.8",
      "-Ywarn-unused"
    )
  }.value ++ Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Ywarn-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture",
    "-Xlint"
  )
)

scalaVersion := "2.12.1"

crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.1")

resolvers += "micronautics/scala on bintray" at "http://dl.bintray.com/micronautics/scala"

libraryDependencies ++= Seq(
  "com.micronautics" %% "scalacourses-utils" % "0.2.20" withSources(),
  "ch.qos.logback"   %  "logback-classic"    % "1.2.1" % Test withSources()
)
libraryDependencies ++= scalaVersion {
  case sv if sv.startsWith("2.12") =>
    Seq(
      "com.typesafe.play"      %% "play"               % "2.6.0-M1" % Provided,
      "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M2" % Test
    )
  case sv if sv.startsWith("2.11") =>
    Seq(
      "com.typesafe.play"      %% "play"               % "2.5.12" % Provided,
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1"  % Test
    )

  case sv if sv.startsWith("2.10") =>
    Seq(
      "com.typesafe.play" %% "play" % "2.2.6" % Provided,
      "org.scalatestplus" %% "play" % "1.0.0" % Test
    )
}.value

bintrayOrganization := Some("micronautics")
bintrayRepository := "play"
publishArtifact in Test := false

publishArtifact in Test := false

// define the statements initially evaluated when entering 'console', 'console-quick' but not 'console-project'
initialCommands in console := """
                                |
                                |import java.net.URL
                                |import java.text.DateFormat
                                |import java.util.Locale
                                |import play.api.Play.current
                                |import play.Logger
                                |""".stripMargin

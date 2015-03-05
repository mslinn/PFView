import bintray.Keys._

name := """PFView"""

version := "0.0.3"

organization := "com.micronautics"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.7", "-unchecked",
    "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint")

scalaVersion := "2.10.5"
//scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.10.5", "2.11.6")

libraryDependencies <++= scalaVersion {
  case sv if sv.startsWith("2.11") =>
    Seq(
      "com.typesafe.play" %% "play"      % "2.3.8" % "provided",
      "org.scalatestplus" %% "play"      % "1.2.0" % "test"
    )

  case sv if sv.startsWith("2.10") =>
    Seq(
      "com.typesafe.play" %% "play"      % "2.2.6" % "provided",
      "org.scalatestplus" %% "play"      % "1.0.0" % "test"
    )
}
bintrayPublishSettings
bintrayOrganization in bintray := Some("micronautics")
repository in bintray := "play"

publishArtifact in Test := false

com.typesafe.sbt.SbtGit.versionWithGit

// define the statements initially evaluated when entering 'console', 'console-quick' but not 'console-project'
initialCommands in console := """ // make app resources accessible
   |Thread.currentThread.setContextClassLoader(getClass.getClassLoader)
   |new play.core.StaticApplication(new java.io.File("."))
   |
   |//import play.api.{ DefaultApplication, Mode, Play }
   |//val applicationPath = new java.io.File(".")
   |//val classLoader = this.getClass.getClassLoader
   |//val sources = None
   |//val applicationMode = Mode.Dev
   |//Play.start(new DefaultApplication(applicationPath, classLoader, sources, applicationMode))
   |
   |import java.net.URL
   |import java.text.DateFormat
   |import java.util.Locale
   |import play.api.Play.current
   |import play.Logger
   |""".stripMargin

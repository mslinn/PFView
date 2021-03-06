import sbt.Keys._

name := "PFView"

version := "0.0.6"

organization := "com.micronautics"

licenses += ("MIT", url("https://opensource.org/licenses/MIT"))

javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", "1.8",
  "-target", "1.8",
  "-g:vars"
)

scalacOptions ++= (
  scalaVersion {
    case sv if sv.startsWith("2.10") => List(
      "-target:jvm-1.7"
    )
    case _ => List(
      "-target:jvm-1.8",
      "-Ywarn-unused"
    )
  }.value ++ Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xlint"
  )
)

scalaVersion := "2.12.11"

crossScalaVersions := Seq("2.10.6", "2.11.12", "2.12.11", "2.13.2")

resolvers += "micronautics/scala on bintray" at "https://dl.bintray.com/micronautics/scala"

ThisBuild / turbo := true

libraryDependencies ++= Seq(
  "ch.qos.logback"           %  "logback-classic"    % "1.2.1"  % Test withSources(),
  "com.micronautics"         %% "scalacourses-utils" % "0.2.20" withSources(),
  "com.google.code.findbugs" %  "jsr305"             % "3.0.2"  withSources() force()
)
libraryDependencies ++= scalaVersion {
  case sv if sv.startsWith("2.13") =>
    // todo write me
    Nil

  case sv if sv.startsWith("2.12") =>
    val playVer = "2.6.2"
    Seq(
      "com.typesafe.play"      %% "play"               % playVer % Provided,
//      "com.typesafe.play"      %% "play-crypto"        % playVer % Provided,
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
    )
  case sv if sv.startsWith("2.11") =>
    Seq(
      "com.typesafe.play"      %% "play"               % "2.5.16" % Provided,
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0"  % Test
    )

  case sv if sv.startsWith("2.10") =>
    Seq(
      "com.typesafe.play" %% "play" % "2.2.6" % Provided,
      "org.scalatestplus" %% "play" % "1.0.0" % Test
    )
}.value

// define the statements initially evaluated when entering 'console', 'console-quick' but not 'console-project'
initialCommands in console := """import java.net.URL
                                |import java.text.DateFormat
                                |import java.util.Locale
                                |import play.api.Play.current
                                |import play.Logger
                                |""".stripMargin

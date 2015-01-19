import bintray.Keys._

name := """PFView"""

version := "0.0.1"

organization := "com.micronautics"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.7", "-unchecked",
    "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint")

scalaVersion := "2.10.4"
//scalaVersion := "2.11.5"

crossScalaVersions := Seq("2.10.4", "2.11.5")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play"      % "2.2.6" % "provided",
  //"com.typesafe.play" %% "play"      % "2.3.7" % "provided",
  "org.scalatest"     %% "scalatest" % "2.2.1" % "test"
)

bintrayPublishSettings
bintrayOrganization in bintray := Some("micronautics")
repository in bintray := "play"

publishArtifact in Test := false

com.typesafe.sbt.SbtGit.versionWithGit

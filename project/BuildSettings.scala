import sbt.Keys._
import Dependencies._

object BuildSettings {
  lazy val settings = Seq(
    name := "dsid-part-3",
    organization := "com.usp",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.2",
    libraryDependencies ++= dependencies
  )
}

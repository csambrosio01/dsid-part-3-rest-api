import Dependencies._
import com.heroku.sbt.HerokuPlugin.autoImport.{herokuAppName, herokuJdkVersion}
import sbt.Keys._
import sbt._

object BuildSettings {

  object Basic {
    lazy val settings = Seq(
      name := "dsid-part-3",
      organization := "com.usp",
      version := "1.0-SNAPSHOT",
      scalaVersion := "2.13.2",
      libraryDependencies ++= dependencies
    )
  }

  object Publish {

    lazy val settings = Seq(
      herokuAppName in Compile := "api-pousar",
      herokuJdkVersion in Compile := "11"
    )
  }

}

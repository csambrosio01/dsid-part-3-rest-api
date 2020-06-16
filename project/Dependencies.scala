import sbt._
import play.sbt.PlayImport._

object Dependencies {
  private val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

  val dependencies = Seq(
    guice, scalaTest
  )
}

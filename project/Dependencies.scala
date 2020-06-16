import sbt._
import play.sbt.PlayImport._
import play.core.PlayVersion.akkaVersion

object Dependencies {
  private val scalaTest = "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

  //Slick Dependencies
  private val playSlick = "com.typesafe.play" %% "play-slick" % "5.0.0"
  private val playSlickEvolutions = "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
  private val postgres = "org.postgresql" % "postgresql" % "42.2.14"

  //Session
  private val kalium = "org.abstractj.kalium" % "kalium" % "0.8.0"
  private val akkaDistributedData = "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion
  private val akkaClusterTyped = "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion

  val dependencies = Seq(
    guice, scalaTest, playSlick, playSlickEvolutions, postgres, kalium, akkaDistributedData, akkaClusterTyped
  )
}

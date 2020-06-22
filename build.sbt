
lazy val root = (project in file(".")).enablePlugins(PlayScala)
  .settings(BuildSettings.Basic.settings: _*)
  .settings(BuildSettings.Publish.settings: _*)

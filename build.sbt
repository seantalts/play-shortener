name := "ur"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "postgresql" % "postgresql" % "8.4-702.jdbc4",
  "com.typesafe.play" %% "play-slick" % "0.5.0.8"
)

val appDependencies = Seq(
  jdbc,
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "postgresql" % "postgresql" % "9.3-1100.jdbc4",
  "com.typesafe.play" %% "play-slick" % "0.5.0.8"
)

play.Project.playScalaSettings

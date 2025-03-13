ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.3"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test
libraryDependencies += "org.jmdns" % "jmdns" % "3.6.0"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.17"

lazy val root = (project in file("."))
  .settings(
    name := "GameServer",
    idePackagePrefix := Some("org.bbk.gameserver")
  )
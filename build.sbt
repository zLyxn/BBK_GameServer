ThisBuild / version := "0.2.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.3"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test

lazy val root = (project in file("."))
  .settings(
    name := "GameServer",
    idePackagePrefix := Some("org.bbk.gameserver")
  )
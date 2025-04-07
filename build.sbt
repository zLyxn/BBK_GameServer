ThisBuild / version := "1.0-PRERELEASE-2"

ThisBuild / scalaVersion := "3.6.3"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test

lazy val root = (project in file("."))
  .settings(
    name := "GameServer",
    idePackagePrefix := Some("org.bbk.gameserver")
  )
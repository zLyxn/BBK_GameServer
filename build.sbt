ThisBuild / version := "1.0-PRERELEASE-2"

ThisBuild / scalaVersion := "3.6.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.18"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test

lazy val root = (project in file("."))
  .settings(
    name := "GameServer",
    idePackagePrefix := Some("org.bbk.gameserver")
  )
Global / excludeLintKeys += idePackagePrefix
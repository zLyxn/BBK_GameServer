ThisBuild / version := "1.0-PRERELEASE-2"

ThisBuild / scalaVersion := "3.6.3"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.18"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
// https://mvnrepository.com/artifact/org.scala-lang/scala-reflect
libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.13.17-M1"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test

lazy val root = (project in file("."))
  .settings(
    name := "GameServer",
    idePackagePrefix := Some("org.bbk.gameserver")
  )
Global / excludeLintKeys += idePackagePrefix

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Wunused:all",      // warn about ALL unused things (imports, variables, etc.)
  "-Wvalue-discard"    // warn about ignored non-Unit results
)


enablePlugins(ScalafixPlugin)
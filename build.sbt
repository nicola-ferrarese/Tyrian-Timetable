import org.scalajs.linker.interface.ModuleKind
import scala.sys.process._

ThisBuild / scalaVersion := "3.4.2"
ThisBuild / version := "0.1.0-SNAPSHOT"

val tyrianVersion = "0.11.0"
lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "departure-board-app",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "tyrian-io" % tyrianVersion,
      "org.scalatest" %%% "scalatest" % "3.2.15" % Test,
      "com.lihaoyi" %%% "utest" % "0.8.4" % Test,
      "org.typelevel" %%% "cats-effect" % "3.2.9",
      "com.softwaremill.sttp.client4" %%% "core" % "4.0.0-M17",
      "com.softwaremill.sttp.client4" %%% "circe" % "4.0.0-M17",
      "io.circe" %%% "circe-generic" % "0.14.5",
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    scalacOptions ++= Seq(
      "-explain",
      "-Xmax-inlines:100"
    ),
    // For running Scala.js tests
    Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
  )
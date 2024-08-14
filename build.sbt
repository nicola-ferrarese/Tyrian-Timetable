import scala.sys.process._
import scala.language.postfixOps

import sbtwelcome._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

Compile / resourceDirectory := baseDirectory.value / "assets"

lazy val tyriantimetable =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin)
    .settings( // Normal settings
      name         := "tyriantimetable",
      version      := "0.0.1",
      scalaVersion := "3.4.1",
      organization := "timetable",
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "tyrian-io" % "0.11.0",
        "org.scalameta"   %%% "munit"     % "0.7.29" % Test,
        "org.scalatest" %%% "scalatest" % "3.2.15" % Test,
        "com.softwaremill.sttp.client4" %%% "core" % "4.0.0-M17",
        "com.softwaremill.sttp.client4" %%% "circe" % "4.0.0-M17",
        "io.circe" %%% "circe-core" % "0.14.5",
        "io.circe" %%% "circe-generic" % "0.14.5",
        "io.circe" %%% "circe-parser" % "0.14.5"
      ),
      testFrameworks += new TestFramework("munit.Framework"),
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      scalafixOnCompile := true,
      semanticdbEnabled := true,
      semanticdbVersion := scalafixSemanticdb.revision,
      autoAPIMappings   := true
    )
    .settings( // Launch VSCode when you type `code` in the sbt terminal
      code := {
        val command = Seq("code", ".")
        val run = sys.props("os.name").toLowerCase match {
          case x if x contains "windows" => Seq("cmd", "/C") ++ command
          case _                         => command
        }
        run.!
      }
    )
    .settings( // Welcome message
      logo := List(
        "",
        "tyrian-timetable (v" + version.value + ")",
        "",
        "> Please Note: By default tyrianapp.js expects you to run fastLinkJS.",
        ">              To use fullOptJS, edit tyrianapp.js replacing '-fastopt'",
        ">              with '-opt'.",
        ""
      ).mkString("\n"),
      usefulTasks := Seq(
        UsefulTask("fastLinkJS", "Rebuild the JS (use during development)").noAlias,
        UsefulTask("fullLinkJS", "Rebuild the JS and optimise (use in production)").noAlias,
        UsefulTask("code", "Launch VSCode").noAlias
      ),
      logoColor        := scala.Console.MAGENTA,
      aliasColor       := scala.Console.BLUE,
      commandColor     := scala.Console.CYAN,
      descriptionColor := scala.Console.WHITE
    )

lazy val code =
  taskKey[Unit]("Launch VSCode in the current directory")

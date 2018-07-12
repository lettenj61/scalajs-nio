val scalaV = "2.12.4"
val libV = "0.1.0-SNAPSHOT"

lazy val sharedSettings = Seq(
  organization := "com.github.lettenj61",
  version := libV,
  scalaVersion := scalaV,
  scalacOptions in Compile ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    // "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ypartial-unification",
    "-Ywarn-dead-code",
    "-Ywarn-extra-implicit",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused:implicits",
    "-Ywarn-unused:imports",
    "-Ywarn-unused:locals",
    "-Ywarn-unused:params",
    "-Ywarn-unused:patvars",
    "-Ywarn-unused:privates",
    "-Ywarn-value-discard",
    "-P:scalajs:sjsDefinedByDefault"
  ),
)

lazy val io = project
  .enablePlugins(ScalaJSPlugin)
  .settings(sharedSettings)
  .settings(
    name := "scalajs-nio",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "utest" % "0.6.3" % "test"
    ),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    scalaJSLinkerConfig ~= {
      _.withSourceMap(false).withModuleKind(ModuleKind.CommonJSModule)
    }
  )

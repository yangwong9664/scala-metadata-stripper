
name := "yang-backend-template"

scalaVersion := "2.12.14"

val akkaVersion     = "2.6.8"
val akkaHttpVersion = "10.2.6"

//%% will find the version specific to scala version, otherwise use %
libraryDependencies ++= {
  Seq(
    "org.apache.commons" % "commons-imaging" % "1.0-alpha2",
    "commons-io" % "commons-io" % "2.11.0",
    "org.ghost4j" % "ghost4j" % "1.0.1",
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % "3.3.7",
    "com.google.inject" % "guice" % "5.0.1",
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    "com.lihaoyi" %% "sourcecode" % "0.2.7"
  )
}

scalacOptions ++= Seq("-unchecked",
  "-deprecation",
  "-feature",
  "-encoding","UTF-8",
  "-explaintypes",
  "-language:higherKinds",
  "-Ypartial-unification",
  "-Ywarn-infer-any",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates"
)

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    coverageMinimum := 1,
    coverageHighlighting := true,
    dependencyUpdatesFailBuild := true,
    coverageFailOnMinimum := true,
    scalastyleFailOnError := true,
    assembly / assemblyJarName := "yang-backend-template.jar",
    Compile / scalaSource := baseDirectory.value / "src",
    Compile / resourceDirectory := baseDirectory.value / "conf",
    Test / scalaSource := baseDirectory.value / "test",
    IntegrationTest / scalaSource := baseDirectory.value / "it",
    Test / parallelExecution := false,
    IntegrationTest / parallelExecution := false,
    IntegrationTest / fork := true,
    Test / fork := true,
    Test / coverageEnabled := true,
    compile / coverageEnabled := true,
    IntegrationTest / coverageEnabled := true
  )
  .enablePlugins(SbtPlugin)

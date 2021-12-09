ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.uryoya"
ThisBuild / organizationName := "Ryoya URANO"

val AkkaVersion = "2.6.17"
val CirceVersion = "0.14.1"
val EnumeratumVersion = "1.7.0"

lazy val root = (project in file("."))
  .settings(
    name := "GaroonScheduleNotifier",
    libraryDependencies ++= Seq(
      // aws
      "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
      "com.amazonaws" % "aws-lambda-java-events" % "3.11.0",
      // akka
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.7",
      // http
      "com.typesafe.akka" %% "akka-http" % "10.2.7",
      // json
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "com.beachape" %% "enumeratum" % EnumeratumVersion,
      "com.beachape" %% "enumeratum-circe" % EnumeratumVersion,
      // config
      "com.github.pureconfig" %% "pureconfig" % "0.17.1",
      // test
      "org.scalameta" %% "munit" % "0.7.29" % Test
    ),
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    )
  )

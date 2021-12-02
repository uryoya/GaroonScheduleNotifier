ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.uryoya"
ThisBuild / organizationName := "Ryoya URANO"

lazy val root = (project in file("."))
  .settings(
    name := "GaroonScheduleNotifer",
    libraryDependencies ++= Seq(
      // aws
      "com.amazonaws" % "aws-lambda-java-core" % "1.2.1",
      "com.amazonaws" % "aws-lambda-java-events" % "3.11.0",
      // http
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.17",
      "com.typesafe.akka" %% "akka-stream" % "2.6.17",
      "com.typesafe.akka" %% "akka-http" % "10.2.7",
      // json
      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1",
      "com.beachape" %% "enumeratum" % "1.7.0",
      "com.beachape" %% "enumeratum-circe" % "1.7.0",
      // config
      "com.github.pureconfig" %% "pureconfig" % "0.17.1",
      // test
      "org.scalameta" %% "munit" % "0.7.29" % Test
    ),
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    )
  )

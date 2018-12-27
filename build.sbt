name := "itto-csv"

version := "0.0.1"
organization := "com.github.gekomad"
scalaVersion := "2.12.8"
scalacOptions += "-Ypartial-unification"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

//cats
libraryDependencies += "org.typelevel" %% "cats-core" % "1.5.0"

//shapeless
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

//test
libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.10.1" % Test

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "org.apache.commons" % "commons-csv" % "1.6" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
testOptions in Test +=
  Tests.Argument(
    TestFrameworks.ScalaCheck,
    "-minSuccessfulTests", "5000"
  )

publishTo := sonatypePublishTo.value
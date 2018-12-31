name := "itto-csv"

version := "0.0.3" //TODO anche nel readme e bumpversion
organization := "com.github.gekomad"
scalaVersion := "2.12.8"
scalacOptions += "-Ypartial-unification"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

//cats
libraryDependencies += "org.typelevel" %% "cats-core" % "1.5.0"

//shapeless
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

//scala-regex-collection
libraryDependencies += "com.github.gekomad" %% "scala-regex-collection" % "0.0.1"

//test
libraryDependencies += "com.storm-enroute"  %% "scalameter" % "0.10"   % Test
libraryDependencies += "org.scalatest"      %% "scalatest"  % "3.0.5"  % Test
libraryDependencies += "org.apache.commons" % "commons-csv" % "1.6"    % Test
libraryDependencies += "org.scalacheck"     %% "scalacheck" % "1.14.0" % Test
testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-minSuccessfulTests", "5000")

crossScalaVersions := Seq("2.11.12", "2.12.6", "2.12.8")

publishTo := sonatypePublishTo.value

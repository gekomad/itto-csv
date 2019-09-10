name := "itto-csv"

version := "1.0.0"
organization := "com.github.gekomad"
scalaVersion := "2.13.0"

crossScalaVersions := Seq("2.11.12", "2.12.6", "2.12.8", "2.12.9", "2.13.0")

scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, 13)) => Seq("-unchecked", "-deprecation", "-feature")
  case _             => Seq("-unchecked", "-deprecation", "-feature", "-Ypartial-unification")
})

//cats
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

//shapeless
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

//scala-regex-collection
libraryDependencies += "com.github.gekomad" %% "scala-regex-collection" % "1.0.0"

libraryDependencies += "com.storm-enroute"  %% "scalameter" % "0.19"   % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0-RC2" % Test
libraryDependencies += "org.apache.commons" % "commons-csv" % "1.7"          % Test
libraryDependencies += "org.scalacheck"     %% "scalacheck" % "1.14.0"     % Test
testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-minSuccessfulTests", "1000")

publishTo := sonatypePublishTo.value

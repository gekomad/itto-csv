name := "itto-csv"

version := "1.0.0-RC1"
organization := "com.github.gekomad"
scalaVersion := "2.13.0-RC2"

crossScalaVersions := Seq("2.11.12", "2.12.6", "2.12.8", "2.13.0-RC2")

scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, 13)) => Seq("-unchecked", "-deprecation", "-feature")
  case _             => Seq("-unchecked", "-deprecation", "-feature", "-Ypartial-unification")
})

//cats
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0-M2"

//shapeless
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

//scala-regex-collection
libraryDependencies += "com.github.gekomad" %% "scala-regex-collection" % "1.0.0-RC1"

//libraryDependencies += "com.storm-enroute"  %% "scalameter" % "0.10"   % Test TODO
libraryDependencies += "org.scalatest"      %% "scalatest"  % "3.1.0-SNAP11" % Test
libraryDependencies += "org.apache.commons" % "commons-csv" % "1.6"          % Test
libraryDependencies += "org.scalacheck"     %% "scalacheck" % "1.14.0"       % Test
testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-minSuccessfulTests", "5000")

publishTo := sonatypePublishTo.value

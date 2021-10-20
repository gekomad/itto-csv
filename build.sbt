name := "itto-csv"

version := "2.0.0"
organization := "com.github.gekomad"

scalaVersion := "3.1.0"

crossScalaVersions := Seq("3.0.2")

val fs2Version = "3.1.6"

libraryDependencies += "com.github.gekomad" %% "scala-regex-collection" % "2.0.0"
libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version
libraryDependencies += "co.fs2" %% "fs2-io" % fs2Version
libraryDependencies += "org.apache.commons" % "commons-csv" % "1.9.0" % Test
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test

//sonatype
publishTo := sonatypePublishTo.value

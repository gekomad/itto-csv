name := "itto-csv"

version      := "2.1.0"
organization := "com.github.gekomad"

scalaVersion := "3.5.2"

val fs2Version = "3.11.0"

libraryDependencies += "com.github.gekomad" %% "scala-regex-collection" % "2.0.0"
libraryDependencies += "co.fs2"             %% "fs2-core"               % fs2Version
libraryDependencies += "co.fs2"             %% "fs2-io"                 % fs2Version
libraryDependencies += "org.apache.commons"  % "commons-csv"            % "1.12.0" % Test
libraryDependencies += "org.scalameta"      %% "munit"                  % "1.0.2"  % Test

scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Wunused:implicits",
  "-Wunused:explicits",
  "-Wunused:imports",
  "-Wunused:locals",
  "-Wunused:params",
  "-Wunused:privates",
  "-Xfatal-warnings"
)

testFrameworks += new TestFramework("munit.Framework")

//sonatype

publishTo := sonatypePublishToBundle.value
logLevel  := Level.Debug

pomExtra :=
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
    <developers>
      <developer>
        <id>gekomad</id>
        <name>Giuseppe Cannella</name>
        <url>https://github.com/gekomad</url>
      </developer>
    </developers>
    <scm>
      <url>https://github.com/gekomad/itto-csv</url>
      <connection>scm:git:https://github.com/gekomad/itto-csv</connection>
    </scm>
    <url>https://github.com/gekomad/itto-csv</url>

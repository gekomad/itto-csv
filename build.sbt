name := "itto-csv"
version := "1.1.1"
organization := "com.github.gekomad"
scalaVersion := "2.13.1"

val catsVersion = "2.1.0"

crossScalaVersions := Seq("2.12.9", "2.13.1")

val options = Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding", "UTF-8", // Specify character encoding used by source files.
  "-language:postfixOps",
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-explaintypes", // Explain type errors in more detail.
  "-Xfatal-warnings" // Fail the compilation if there are any warnings.
)

scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, 13)) => options
  case _             => options ++ Seq("-Ypartial-unification", "-language:higherKinds")
})

//cats
libraryDependencies += "org.typelevel"      %% "cats-core"              % catsVersion
libraryDependencies += "co.fs2"             %% "fs2-core"               % catsVersion
libraryDependencies += "co.fs2"             %% "fs2-io"                 % catsVersion

//shapeless
libraryDependencies += "com.chuusai"        %% "shapeless"              % "2.3.3"

//scala-regex-collection
libraryDependencies += "com.github.gekomad" %% "scala-regex-collection" % "1.0.1"

//test
libraryDependencies += "com.storm-enroute"  %% "scalameter"             % "0.19"      % Test
libraryDependencies += "org.scalatest"      %% "scalatest"              % "3.2.0-M2"  % Test
libraryDependencies += "org.apache.commons"  % "commons-csv"            % "1.7"       % Test
libraryDependencies += "org.scalacheck"     %% "scalacheck"             % "1.14.3"    % Test

testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-minSuccessfulTests", "1000")

//sonatype
publishTo := sonatypePublishTo.value

//microsite
enablePlugins(GhpagesPlugin)
enablePlugins(MicrositesPlugin)
micrositeBaseUrl := "/itto-csv"
git.remoteRepo := "https://github.com/gekomad/itto-csv.git"
micrositeGithubOwner := "gekomad"
micrositeGithubRepo := "itto-csv.git"
micrositeGitterChannel := false
micrositeShareOnSocial := false
micrositeGithubLinks := false
micrositeTheme := "pattern"
import microsites.CdnDirectives

micrositeCDNDirectives := CdnDirectives(
  jsList = List(
    "https://cdnjs.cloudflare.com/ajax/libs/ag-grid/7.0.2/ag-grid.min.js",
    "https://cdnjs.cloudflare.com/ajax/libs/ajaxify/6.6.0/ajaxify.min.js"
  ),
  cssList = List(
    "https://cdnjs.cloudflare.com/ajax/libs/animate.css/3.5.2/animate.css",
    "https://cdnjs.cloudflare.com/ajax/libs/cssgram/0.1.12/1977.min.css",
    "https://cdnjs.cloudflare.com/ajax/libs/cssgram/0.1.12/brooklyn.css"
  )
)

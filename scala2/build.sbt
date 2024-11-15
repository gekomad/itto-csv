name         := "itto-csv"
version      := "2.1.0"
organization := "com.github.gekomad"
//scalaVersion := "2.12.20"
scalaVersion := "2.13.15"
val fs2Version = "3.11.0"
scalacOptions ++= {
  if (scalaVersion.value.startsWith("2.12")) {
    Seq("-Ypartial-unification")
  } else {
    Seq.empty
  }
}
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:postfixOps",
  "-feature",
  "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                   // Wrap field accessors to throw an exception on uninitialized access.
  "-Xlint:private-shadow",         // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",            // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",  // A local type parameter shadows a type already in scope.
  "-Ywarn-dead-code",              // Warn when dead code is identified.
  "-Ywarn-extra-implicit",         // Warn when more than one implicit parameter section is defined.
  "-Xlint:missing-interpolator",   // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-unit",           // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",        // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-explaintypes",                 // Explain type errors in more detail.
  "-Xfatal-warnings"
)

//cats

libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version
libraryDependencies += "co.fs2" %% "fs2-io"   % fs2Version

//shapeless
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.12"

//scala-regex-collection
libraryDependencies += "com.github.gekomad" %% "scala-regex-collection" % "2.0.1"

//test
libraryDependencies += "com.storm-enroute" %% "scalameter"  % "0.21"   % Test
libraryDependencies += "org.scalameta"     %% "munit"       % "1.0.2"  % Test
libraryDependencies += "org.apache.commons" % "commons-csv" % "1.12.0" % Test
libraryDependencies += "org.scalacheck"    %% "scalacheck"  % "1.18.1" % Test
testFrameworks += new TestFramework("munit.Framework")
Test / testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-minSuccessfulTests", "1000")

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

//microsite
//enablePlugins(GhpagesPlugin)
//enablePlugins(MicrositesPlugin)
//micrositeBaseUrl := "/itto-csv"
//git.remoteRepo := "https://github.com/gekomad/itto-csv.git"
//micrositeGithubOwner := "gekomad"
//micrositeGithubRepo := "itto-csv.git"
//micrositeGitterChannel := false
//micrositeShareOnSocial := false
//micrositeGithubLinks := false
//micrositeTheme := "pattern"
//import microsites.CdnDirectives
//
//micrositeCDNDirectives := CdnDirectives(
//  jsList = List(
//    "https://cdnjs.cloudflare.com/ajax/libs/ag-grid/7.0.2/ag-grid.min.js",
//    "https://cdnjs.cloudflare.com/ajax/libs/ajaxify/6.6.0/ajaxify.min.js"
//  ),
//  cssList = List(
//    "https://cdnjs.cloudflare.com/ajax/libs/animate.css/3.5.2/animate.css",
//    "https://cdnjs.cloudflare.com/ajax/libs/cssgram/0.1.12/1977.min.css",
//    "https://cdnjs.cloudflare.com/ajax/libs/cssgram/0.1.12/brooklyn.css"
//  )
//)

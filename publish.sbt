import xerial.sbt.Sonatype._

publishMavenStyle := true

sonatypeProfileName := "com.github.gekomad"
sonatypeProjectHosting := Some(
  GitHubHosting(user = "gekomad", repository = "itto-csv", email = "giuseppe.cannella@gmail.com")
)
developers := List(
  Developer(id = "gekomad",
            name = "Giuseppe",
            email = "giuseppe.cannella@gmail.com",
            url = url("https://github.com/gekomad")
  )
)
licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

publishConfiguration := publishConfiguration.value.withOverwrite(true)
sonatypeLogLevel := "DEBUG"

//sonatype

publishTo := sonatypePublishToBundle.value

logLevel := Level.Debug
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots".at(nexus + "content/repositories/snapshots/"))
  else
    Some("releases".at(nexus + "content/repositories/releases/"))
}

lazy val root = (project in file(".")).aggregate(scala2, scala3)

lazy val scala2 = project in file("scala2")

lazy val scala3 = project in file("scala3")

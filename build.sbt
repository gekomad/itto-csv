lazy val `itto-csv` = (project in file(".")).aggregate(scala2, scala3, scala2Js, scala3Js)

lazy val scala2 = project in file("scala2")

lazy val scala3 = project in file("scala3")

lazy val scala2Js = project in file("scala2_js")

lazy val scala3Js = project in file("scala3_js")

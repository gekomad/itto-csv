---
layout: docs
title: Get Header
permalink: docs/get-header/
---


### Get Header
It's possible to get the header starting from the chosen class

```scala
case class Foo(i: Int, d: Double, s: Option[String], b: Boolean)
import com.github.gekomad.ittocsv.core.Header._

{
  implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
  assert(csvHeader[Foo] == "i,d,s,b")
}

{
  implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default.withDelimiter('|').withForceQuote(true)
  assert(csvHeader[Foo] == """"i"|"d"|"s"|"b"""")
}

```
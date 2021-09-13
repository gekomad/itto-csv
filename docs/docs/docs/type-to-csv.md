---
layout: docs
title: Type to CSV
permalink: docs/type-to-csv/
---

### Type to CSV

Trasform a case class to CSV

```scala
import com.github.gekomad.ittocsv.core.ToCsv._
given IttoCSVFormat = IttoCSVFormat.default

case class Bar(a: String, b: Int)
assert(toCsv(Bar("侍", 42)) == "侍,42")
```
```scala
import com.github.gekomad.ittocsv.core.ToCsv._
given IttoCSVFormat = IttoCSVFormat.default
case class Baz(x: String)
case class Foo(a: Int, c: Baz)
case class Xyz(a: String, b: Int, c: Foo)

assert(toCsvFlat(Xyz("hello", 3, Foo(1, Baz("hi, dude")))) == "hello,3,1,\"hi, dude\"")
```
```scala
import com.github.gekomad.ittocsv.core.ToCsv._
given IttoCSVFormat = IttoCSVFormat.default
assert(toCsv(List(1.1, 2.1, 3.1)) == "1.1,2.1,3.1")
```
---
layout: docs
title: CSV to list
permalink: docs/csv-to-list/
---

### CSV to List

Trasform a CSV string to a list of type

```scala
import com.github.gekomad.ittocsv.core.FromCsv._
import com.github.gekomad.ittocsv.core.ParseFailure
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

val a: List[Either[ParseFailure, Double]] = fromCsvL[Double]("1.1,2.1,3.1")
val b: List[Either[ParseFailure, Double]] = fromCsvL[Double]("1.1,abc,3.1")

assert(a == List(Right(1.1), Right(2.1), Right(3.1)))
assert(b == List(Right(1.1), Left(ParseFailure("abc is not Double")), Right(3.1)))
```

### CSV  to List of Type
Trasform a CSV string to a List[Either[NonEmptyList[ParseFailure], Foo]]

```scala
import com.github.gekomad.ittocsv.core.FromCsv._
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

case class Bar(a: String, b: Int)

assert(fromCsv[Bar]("abc,42") == List(Right(Bar("abc", 42))))
assert(fromCsv[Bar]("abc,42\r\nfoo,24") == List(Right(Bar("abc", 42)), Right(Bar("foo", 24))))

case class Foo(v: String, a: List[Int])
assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", List(1, 2, 3)))))
```

### CSV to List of type with LocalDateTime
Trasform a CSV string to List[Either[NonEmptyList[ParseFailure], Foo]]

```scala
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import com.github.gekomad.ittocsv.core.Conversions._
import com.github.gekomad.ittocsv.core.FromCsv._
case class Foo(a: Int, b: java.time.LocalDateTime)

implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

val l = fromCsv[Foo]("1,2000-12-31T11:21:19") // List[Either[NonEmptyList[ParseFailure], Foo]]
assert(l == List(Right(Foo(1, java.time.LocalDateTime.parse("2000-12-31T11:21:19", java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)))))
```

### CSV to List of type with **custom** LocalDateTime
Trasform a CSV string to `List[Either[NonEmptyList[ParseFailure], Foo]]`

 ```scala
case class Foo(a: Int, b: java.time.LocalDateTime)

import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import com.github.gekomad.ittocsv.core.FromCsv._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.github.gekomad.ittocsv.core.ParseFailure
implicit val csvFormat = IttoCSVFormat.default

implicit def localDateTimeToCsv: String => Either[ParseFailure, LocalDateTime] = {
  case s => scala.util.Try {
    Right(LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0")))
  }.getOrElse(Left(ParseFailure(s"Not a LocalDataTime $s")))
}

val l = fromCsv[Foo]("1,2000-12-31 11:21:19.0") // List[Either[NonEmptyList[ParseFailure], Foo]]
assert(l == List(Right(Foo(1, LocalDateTime.parse("2000-12-31 11:21:19.0", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))))))
 ```
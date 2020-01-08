---
layout: docs
title: List to CSV
permalink: docs/list-to-csv/
---

### List of Type to CSV
Trasform a list of case class to a CSV multirows

```scala
import com.github.gekomad.ittocsv.core.ToCsv._
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

case class Bar(a: String, b: Int)
assert(toCsvL(List(Bar("Bar", 42),Bar("Foo", 24))) == "a,b\r\nBar,42\r\nFoo,24")
```


### List of type with LocalDateTime to CSV

```scala
import com.github.gekomad.ittocsv.core.ToCsv._
import java.time.LocalDateTime
import com.github.gekomad.ittocsv.core.CsvStringEncoder
import java.time.format.DateTimeFormatter
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default.withPrintHeader(false)

val myFormatter=DateTimeFormatter.ofPattern("yyyy ** MM ** dd HH ++ mm ++ ss")

implicit def localDateTimeEncoder(implicit csvFormat: com.github.gekomad.ittocsv.parser.IttoCSVFormat): CsvStringEncoder[LocalDateTime] =
  (value: LocalDateTime) => value.format(myFormatter)

case class Bar(a: String, b: Long, c: java.time.LocalDateTime, e: Option[Int])

val myDate = java.time.LocalDateTime.parse("2000 ** 12 ** 31 11 ++ 21 ++ 19", myFormatter)

val l: List[Bar] = List(Bar("Yel,low", 3L, myDate, Some(1)), Bar("eee", 7L, myDate, None))

assert(toCsvL(l) == "\"Yel,low\",3,2000 ** 12 ** 31 11 ++ 21 ++ 19,1\r\neee,7,2000 ** 12 ** 31 11 ++ 21 ++ 19,")

```


### List of type with **custom** LocalDateTime to CSV

```scala
import java.time.LocalDateTime
import com.github.gekomad.ittocsv.core.CsvStringEncoder
import java.time.format.DateTimeFormatter
import com.github.gekomad.ittocsv.core.ToCsv._
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default.withPrintHeader(false)

implicit def localDateTimeEncoder(implicit csvFormat: com.github.gekomad.ittocsv.parser.IttoCSVFormat): CsvStringEncoder[LocalDateTime] =
  (value: LocalDateTime) => value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))

val localDateTime = LocalDateTime.parse("2000-11-11 11:11:11.0", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))

case class Bar(a: String, b: Long, c: LocalDateTime, e: Option[Int])
val l: List[Bar] = List(
  Bar("Yel,low", 3L, localDateTime, Some(1)),
  Bar("eee", 7L, localDateTime, None)
)
assert(toCsv(l) == "\"Yel,low\",3,2000-11-11 11:11:11.0,1,eee,7,2000-11-11 11:11:11.0,")
 ```

[![Build Status](https://travis-ci.com/gekomad/itto-csv.svg?branch=master)](https://travis-ci.com/gekomad/itto-csv)

Itto-CSV
=====================

侍
=
Itto-CSV is a pure scala library for working with the CSV format
## Add the library to your project
`libraryDependencies += "com.github.gekomad" %% "itto-csv" % "0.0.1"`

 ## Using Library

### Formatters
The formatter determines how CSV will be generated

```
case class IttoCSVFormat(
    delimeter: Char,
    quote: Char,
    recordSeparator: String,
    quoteEmpty: Boolean,
    forceQuote: Boolean,
    printHeader: Boolean,
    trim: Boolean)
 ```
Two formatters are available:


| Method   |    Description        |  Default Formatter| Tab formatter|
|----------|:-------------:|------:|-:|
| withDelimiter(c: Char)   |  the separator between fields | , |\t|
| withQuote(c: Char)        |    the quoteChar character   | " |"|
| withQuoteEmpty(c: Boolean)  | quotes field if empty |    false |false|
| withForceQuote(c: Boolean) | quotes all fields |    false |false|
| withPrintHeader(c: Boolean)  | if true prints the header (method toCsvL) |    false |false|
| withTrim(c: Boolean)   | trims the field |    false |false|
| withRecordSeparator(c: String) | the rows separator|    \r\n |\r\n|

It's possible to create custom formatters editing the default ones, example:
`implicit val newFormatter = default.withForceQuote(true).withRecordSeparator("\n").with.....`
  ## Use the library

### Type to CSV

Trasforming a case class to a CSV
```
import com.github.gekomad.ittocsv.core.ToCsv._
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

case class Bar(a: String, b: Int)
assert(toCsv(Bar("侍", 42)) == "侍,42")
```
```
case class Baz(x: String)
case class Foo(a: Int, c: Baz)
case class Xyz(a: String, b: Int, c: Foo)

assert(toCsv(Xyz("hello", 3, Foo(1, Baz("hi, dude")))) == "hello,3,1,\"hi, dude\"")
```
```
assert(toCsv(List(1.1, 2.1, 3.1)) == "1.1,2.1,3.1")
```
### CSV  to List of Type
Trasforming a CSV to a list of case class

 ```
 import com.github.gekomad.ittocsv.core.FromCsv._
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

case class Bar(a: String, b: Int)
assert(fromCsv[Bar]("abc,42") == List(Right(Bar("abc", 42))))
assert(fromCsv[Bar]("abc,42\r\nfoo,24") == List(Right(Bar("abc", 42)), Right(Bar("foo", 24))))

case class Foo(v: String, a: List[Int])
assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", List(1, 2, 3)))))
```

### CSV  to List
trasform a CSV to a list of case class
```
import com.github.gekomad.ittocsv.core.FromCsv._

implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

assert(fromCsvL[Double]("1.1,2.1,3.1") == List(Right(1.1), Right(2.1), Right(3.1)))
assert(fromCsvL[Double]("1.1,abc,3.1") == List(Right(1.1), Left(ParseFailure("abc is not Double")), Right(3.1)))
```
### List of Type to CSV
Trasforming a list of case class to a CSV multirows
```
import com.github.gekomad.ittocsv.core.ToCsv._
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

case class Bar(a: String, b: Int)
assert(toCsvL(List(Bar("Bar", 42),Bar("Foo", 24))) == "a,b\r\nBar,42\r\nFoo,24")
```
### Read from file

 ```

import com.github.gekomad.ittocsv.parser.files.FromFile.csvFromFile
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab

case class Bar(id: String, name: String, date: String)

val path = "/tmp/csv_with_header.csv"
val list = csvFromFile[Bar](path, skipHeader = true) // Try[Seq[Either[NonEmptyList[ParseFailure], Bar]]]

```

### Write to file

```
case class Bar(id: String, name: String)

import com.github.gekomad.ittocsv.parser.files.ToFile.csvToFile
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator("\n")
import com.github.gekomad.ittocsv.core.ToCsv._

val list = List(Bar("A1","Jack"),Bar("A2","Bob"))

val filePath = "/tmp/out.csv"
csvToFile(list, filePath)
 ```

### Get Header
It's possible to get the header starting from the chosen class

```
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
### Encode/Decode SHA1, SHA256, MD5, UUID, Email, IP,  URL
You can change the email decoder with
`implicit val emailValidator = com.github.gekomad.ittocsv.core.Types.EmailOps.validator.copy(emailRegex = yourRegex)`

and the URL decoder with
`implicit val urlValidator = com.github.gekomad.ittocsv.core.Types.UrlOps.validator.copy(urlRegex = yourRegex)`

```
import java.util.UUID
import com.github.gekomad.ittocsv.core.Types.SHAOps.SHA1
import com.github.gekomad.ittocsv.core.Conversions.toSHA1s._

import com.github.gekomad.ittocsv.core.Types.SHAOps.SHA256
import com.github.gekomad.ittocsv.core.Conversions.toSHA256s

import com.github.gekomad.ittocsv.core.Types.MD5Ops.MD5
import com.github.gekomad.ittocsv.core.Types.UrlOps.URL
import com.github.gekomad.ittocsv.core.Types.IPOps._

import com.github.gekomad.ittocsv.core.Types.EmailOps.Email

implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

case class Bar(a: String, b: SHA1, c: SHA256, d: MD5, e: UUID, f: Email, g:IP, h:IP6, i:URL)

val sha1 = SHA1("1c18da5dbf74e3fc1820469cf1f54355b7eec92d")
val uuid = UUID.fromString("1CC3CCBB-C749-3078-E050-1AACBE064651")
val md5 = MD5("23f8e84c1f4e7c8814634267bd456194")
val sha256 = SHA256("000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1")
val email = Email("daigoro@itto.com")
val ip = IP("10.168.1.108")
val ip6 = IP6("2001:db8:a0b:12f0::1")
val url = URL("http://www.aaa.cdd.com")
val bar = Bar("abc", sha1, sha256, md5, uuid, email, ip, ip6, url)

//encode
import com.github.gekomad.ittocsv.core.ToCsv._
assert(toCsv(bar) == "abc,1c18da5dbf74e3fc1820469cf1f54355b7eec92d,000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1,23f8e84c1f4e7c8814634267bd456194,1cc3ccbb-c749-3078-e050-1aacbe064651,daigoro@itto.com,10.168.1.108,2001:db8:a0b:12f0::1,http://www.aaa.cdd.com")

//decode
import com.github.gekomad.ittocsv.core.FromCsv._
assert(fromCsv[Bar]("abc,1c18da5dbf74e3fc1820469cf1f54355b7eec92d,000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1,23f8e84c1f4e7c8814634267bd456194,1CC3CCBB-C749-3078-E050-1AACBE064651,daigoro@itto.com,10.168.1.108,2001:db8:a0b:12f0::1,http://www.aaa.cdd.com") ==List(Right(bar)))
```

### Encode/Decode a complex type
View [TreeTest.scala](https://github.com/gekomad/itto-csv/blob/master/src/test/scala/TreeTest.scala)   for complete test
It's possible to generate a complex data CSV specifying the encoder/decoder. Here an example with a Tree[Int]

 ```

import com.github.gekomad.ittocsv.core.CsvStringEncoder
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

case class Foo(v: String, a: Tree[Int])

//encoding
import com.github.gekomad.ittocsv.core.ToCsv._

implicit def _f(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Tree[Int]] = createEncoder { node => csvConverter.stringToCsvField(serialize(Some(node))) }

val tree: Tree[Int] = Tree(
 1, Some(Tree(2, Some(Tree(3)))), Some(Tree(4, Some(Tree(5)), Some(Tree(6)) )))

val encoded: String = toCsv(Foo("abc", tree))

assert(encoded == "abc,\"1(2(3(,),),4(5(,),6(,)))\"")

//deserialize
import com.github.gekomad.ittocsv.core.FromCsvImpl._
import com.github.gekomad.ittocsv.core.FromCsv._

implicit def _l(implicit csvFormat: IttoCSVFormat): String => Either[ParseFailure, Tree[Int]] = (str: String) => deserialize(str, _.toInt) match {
 case None => Left(ParseFailure(s"Not a Node[Short] $str")) case Some(a) => Right(a)}

assert(fromCsv[Foo](encoded) == List(Right(Foo("abc", tree))))
 ```

### Date and Time

These types are automatically managed, anyway you can custom their formatters
 ```
java.time.LocalDateTime
java.time.LocalDate
java.time.LocalTime
java.time.OffsetDateTime
java.time.OffsetTime
java.time.ZonedDateTime
 ```

### CSV to List of type with LocalDateTime
Trasforming a CSV string to a class list; if the trasformations fails, resturns a Left with the cause of the error

 ```
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import com.github.gekomad.ittocsv.core.FromCsv._
import com.github.gekomad.ittocsv.core.FromCsv.Convert.fromStringToLocalDateTime
case class Foo(a: Int, b: java.time.LocalDateTime)

implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

val o = fromCsv[Foo]("1,2000-12-31T11:21:19")
assert(o == List(Right(Foo(1, java.time.LocalDateTime.parse("2000-12-31T11:21:19", java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)))))
```

### CSV to List of type with **custom** LocalDateTime
Trasforming a CSV string to a class list; if the trasformations fails, resturns a Left with the cause of the error

 ```
 import com.github.gekomad.ittocsv.parser.IttoCSVFormat

import com.github.gekomad.ittocsv.core.FromCsv._

case class Foo(a: Int, b: java.time.LocalDateTime)

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

implicit val csvFormat = IttoCSVFormat.default

implicit def localDateTimeToCsv: String => Either[ParseFailure, LocalDateTime] = {
  case s => try {
    Right(LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0")))
  }
  catch {
    case _: Throwable => Left(ParseFailure(s"Not a LocalDataTime $s"))
  }
}

val o = fromCsv[Foo]("1,2000-12-31 11:21:19.0")
assert(o == List(Right(Foo(1, LocalDateTime.parse("2000-12-31 11:21:19.0", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))))))
 ```

### List of type with LocalDateTime  to CSV

 ```

import com.github.gekomad.ittocsv.core.ToCsv._
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default.withPrintHeader(false)

val myFormatter=DateTimeFormatter.ofPattern("yyyy ** MM ** dd HH ++ mm ++ ss")

implicit def localDateTimeEncoder(implicit csvFormat: com.github.gekomad.ittocsv.parser.IttoCSVFormat): CsvStringEncoder[LocalDateTime] =
  (value: LocalDateTime) => value.format(myFormatter)

case class Bar(a: String, b: Long, c: java.time.LocalDateTime, e: Option[Int])

val myDate = java.time.LocalDateTime.parse("2000 ** 12 ** 31 11 ++ 21 ++ 19", myFormatter)

val l: List[Bar] = List(Bar("Yel,low", 3L, myDate, Some(1)), Bar("eee", 7L, myDate, None))

assert(toCsvL(l) == "\"Yel,low\",3,2000 ** 12 ** 31 11 ++ 21 ++ 19,1\r\neee,7,2000 ** 12 ** 31 11 ++ 21 ++ 19,")

 ```
### List of type with **custom** LocalDateTime  to CSV

 ```
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

## Spooling CSV file using FS2 Stream and Doobie

See [Doobie Recepies project](https://github.com/gekomad/doobie-recipes/blob/master/src/test/scala/Itto.scala)

## Scaladoc API
[config API](https://javadoc.io/doc/com.github.gekomad/itto-csv)

## Bugs and Feedback
For bugs, questions and discussions please use [Github Issues](https://github.com/gekomad/itto-csv/issues).

## License
Copyright 2019 Giuseppe Cannella

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
Itto-CSV [![Build Status](https://travis-ci.com/gekomad/itto-csv.svg?branch=master)](https://travis-ci.com/gekomad/itto-csv)
[![codecov.io](http://codecov.io/github/gekomad/itto-csv/coverage.svg?branch=master)](http://codecov.io/github/gekomad/itto-csv?branch=master)
[![Javadocs](https://javadoc.io/badge/com.github.gekomad/itto-csv_2.12.svg)](https://javadoc.io/doc/com.github.gekomad/itto-csv_2.12)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.gekomad/itto-csv_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.gekomad/itto-csv_2.12)
<a href="https://typelevel.org/cats/"><img src="https://typelevel.org/cats/img/cats-badge.svg" height="40px" align="right" alt="Cats friendly" /></a>
======


Itto-CSV is a pure scala library for working with the CSV format
## Add the library to your project
`libraryDependencies += "com.github.gekomad" %% "itto-csv" % "0.1.0"`

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
    trim: Boolean,
    ignoreEmptyLines: Boolean,
    quoteLowerChar: Boolean)
 ```
Two formatters are available:


| Method   |    Description        |  Default Formatter| Tab formatter|
|----------|:-------------:|------:|-:|
| withDelimiter(Char)   |  the separator between fields | , |\t|
| withQuote(Char)        |    the quoteChar character   | " |"|
| withQuoteEmpty(Boolean)  | quotes field if empty |    false |false|
| withForceQuote(Boolean) | quotes all fields |    false |false|
| withPrintHeader(Boolean)  | if true prints the header (method toCsvL) |    false |false|
| withTrim(Boolean)   | trims the field |    false |false|
| withRecordSeparator(String) | the rows separator|    \r\n |\r\n|
| withIgnoreEmptyLines(Boolean) | skips empty lines |   false |false |
| withQuoteLowerChar(Boolean) | quotes lower chars|    false |false |

It's possible to create custom formatters editing the default ones, example:
`implicit val newFormatter = default.withForceQuote(true).withRecordSeparator("\n").with.....`
  ## Use the library

### Types

You can use [defined types](#defined_types) or you can define yours

### Type to CSV

Trasforming a case class to CSV
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
import com.github.gekomad.ittocsv.core.ParseFailure
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

import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFile
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab

case class Bar(id: String, name: String, date: String)

val path = "/tmp/csv_with_header.csv"
val list = csvFromFile[Bar](path, skipHeader = true) // Try[Seq[Either[NonEmptyList[ParseFailure], Bar]]]

```

### Write to file

```
case class Bar(id: String, name: String)

import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFile
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
### Encode/Decode some types

You can change the regex pattern with

`implicit val emailValidator: Validator[Email] = com.github.gekomad.ittocsv.core.Types.implicits.validatorEmail.copy(regex = yourRegex) `


```
import java.util.UUID
import com.github.gekomad.ittocsv.core.Types.implicits._
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

case class Bar(a: String, b: SHA1, c: SHA256, d: MD5, e: UUID, f: Email, g: IP6, h: BitcoinAdd, i: URL)

val sha1       = SHA1("1c18da5dbf74e3fc1820469cf1f54355b7eec92d")
val uuid       = UUID.fromString("1CC3CCBB-C749-3078-E050-1AACBE064651")
val md5        = MD5("23f8e84c1f4e7c8814634267bd456194")
val sha256     = SHA256("000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1")
val email      = Email("daigoro@itto.com")
val ip         = IP6("2001:db8:a0b:12f0::1")
val bitcoinAdd = BitcoinAdd("3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v")
val url        = URL("http://www.aaa.cdd.com")

val bar = Bar("abc", sha1, sha256, md5, uuid, email, ip, bitcoinAdd, url)

val csvString =
  "abc,1c18da5dbf74e3fc1820469cf1f54355b7eec92d,000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1,23f8e84c1f4e7c8814634267bd456194,1cc3ccbb-c749-3078-e050-1aacbe064651,daigoro@itto.com,2001:db8:a0b:12f0::1,3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v,http://www.aaa.cdd.com"

//encode
import com.github.gekomad.ittocsv.core.ToCsv._
assert(toCsv(bar) == csvString)

//decode
import com.github.gekomad.ittocsv.core.FromCsv._
assert(fromCsv[Bar](csvString) == List(Right(bar)))
```

### Encode/Decode your own type
Using **encode** for MyType:

`implicit def _e(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[MyType] = ??? `

Example encoding N:Int to "[N]"

```
import com.github.gekomad.ittocsv.core.CsvStringEncoder
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

case class MyType(a: Int)
case class Foo(a: MyType, b: Int)

import com.github.gekomad.ittocsv.core.ToCsv._

implicit def _e(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[MyType] = createEncoder { node =>
  csvConverter.stringToCsvField(s"[${node.a}]")
}

assert(toCsv(Foo(MyType(42),99)) == "[42],99")
 ```

Using **decode** for MyType:

`implicit def _d(implicit csvFormat: IttoCSVFormat): String => Either[ParseFailure, MyType] = (str: String) => ???`

Example decoding "[N]" to N:Int
```
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import scala.util.Try
implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default
import com.github.gekomad.ittocsv.core.{ParseFailure}
import cats.data.NonEmptyList

case class MyType(a: Int)
case class Foo(a: MyType, b: Int)

import com.github.gekomad.ittocsv.core.FromCsv._

implicit def _d(implicit csvFormat: IttoCSVFormat): String => Either[ParseFailure, MyType] = { (str: String) =>
  if (str.startsWith("[") && str.endsWith("]"))
    Try(str.substring(1, str.size - 1).toInt)
      .map(f => Right(MyType(f)))
      .getOrElse(Left(ParseFailure(s"Not a MyType $str")))
  else Left(ParseFailure(s"Wrong format $str"))

}

assert(fromCsv[Foo]("[42],99") == List(Right(Foo(MyType(42),99))))
assert(fromCsv[Foo]("[x],99") == List(Left(NonEmptyList(ParseFailure("Not a MyType [x]"), Nil))))
assert(fromCsv[Foo]("42,99") == List(Left(NonEmptyList(ParseFailure("Wrong format 42"), Nil))))

```
The [TreeTest.scala](https://github.com/gekomad/itto-csv/blob/master/src/test/scala/TreeTest.scala) shows how to encode/decode a `Tree[Int]`


### CSV to List of type with LocalDateTime
Trasforming a CSV string to a class list; if the trasformations fails resturns a Left with the cause of the error

 ```
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import com.github.gekomad.ittocsv.core.FromCsv._
import com.github.gekomad.ittocsv.core.Conversions.fromStringToLocalDateTime
case class Foo(a: Int, b: java.time.LocalDateTime)

implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

val o = fromCsv[Foo]("1,2000-12-31T11:21:19")
assert(o == List(Right(Foo(1, java.time.LocalDateTime.parse("2000-12-31T11:21:19", java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)))))
```

### CSV to List of type with **custom** LocalDateTime
Trasforming a CSV string to a class list; if the trasformations fails resturns a Left with the cause of the error

 ```
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import com.github.gekomad.ittocsv.core.ParseFailure
import com.github.gekomad.ittocsv.core.FromCsv._

case class Foo(a: Int, b: java.time.LocalDateTime)

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

implicit val csvFormat = IttoCSVFormat.default

implicit def localDateTimeToCsv: String => Either[ParseFailure, LocalDateTime] = {
  case s => scala.util.Try {
    Right(LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0")))
  }.getOrElse(Left(ParseFailure(s"Not a LocalDataTime $s")))
}

val o = fromCsv[Foo]("1,2000-12-31 11:21:19.0")
assert(o == List(Right(Foo(1, LocalDateTime.parse("2000-12-31 11:21:19.0", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))))))
 ```

### List of type with LocalDateTime  to CSV

 ```

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

### List of type with **custom** LocalDateTime  to CSV

```
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

## Spooling CSV file using FS2 Stream and Doobie

See [Doobie Recepies project](https://github.com/gekomad/doobie-recipes/blob/master/src/test/scala/Itto.scala)

## Further examples

[From CSV to Type](https://github.com/gekomad/itto-csv/blob/master/src/test/scala/FromCsvTest.scala)

[From Type To CSV](https://github.com/gekomad/itto-csv/blob/master/src/test/scala/ToCsvTest.scala)

<a id="defined_types"></a>
## Defined types

Email

- [Email](https://github.com/gekomad/scala-regex-collection/wiki/Email)  ($abc@</span>def</span>.c)
- [Email1](https://github.com/gekomad/scala-regex-collection/wiki/Email1)  (abc@</span>def</span>.com)
- [Email simple](https://github.com/gekomad/scala-regex-collection/wiki/EmailSimple)  ($@</span>%</span>.$)

Ciphers

- [UUID](https://github.com/gekomad/scala-regex-collection/wiki/UUID)  (1CC3CCBB-C749-3078-E050-1AACBE064651)
- [MD5](https://github.com/gekomad/scala-regex-collection/wiki/MD5)  (23f8e84c1f4e7c8814634267bd456194)
- [SHA1](https://github.com/gekomad/scala-regex-collection/wiki/SHA1)  (1c18da5dbf74e3fc1820469cf1f54355b7eec92d)
- [SHA256](https://github.com/gekomad/scala-regex-collection/wiki/SHA256) (000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1)

URL, IP, MAC Address

- [IP](https://github.com/gekomad/scala-regex-collection/wiki/IP)  (10.192.168.1)
- [IP_6](https://github.com/gekomad/scala-regex-collection/wiki/IP_6)  (2001:db8:a0b:12f0::1)
- [URLs](https://github.com/gekomad/scala-regex-collection/wiki/URLs)  (http://</span>abc.def</span>.com)
- [Youtube](https://github.com/gekomad/scala-regex-collection/wiki/Youtube)  (https://</span>www</span>.youtube</span>.com/watch?v=9bZkp7q19f0)
- [Facebook](https://github.com/gekomad/scala-regex-collection/wiki/Facebook)  (https://</span>www</span>.facebook.</span>com/thesimpsons - https://</span>www</span>.facebook.</span>com/pages/)
- [Twitter](https://github.com/gekomad/scala-regex-collection/wiki/Twitter)  (https://</span>twitter</span>.com/rtpharry)
- [MAC Address](https://github.com/gekomad/scala-regex-collection/wiki/MACAddress)  (fE:dC:bA:98:76:54)

HEX

- [HEX](https://github.com/gekomad/scala-regex-collection/wiki/HEX)  (#F0F0F0 - 0xF0F0F0)

Bitcoin

- [Bitcon Address](https://github.com/gekomad/scala-regex-collection/wiki/Bitcon-Address)  (3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v)

Phone numbers

- [US phone number](https://github.com/gekomad/scala-regex-collection/wiki/US-phone-number)  (555-555-5555 - (416)555-3456)
- [Italian Mobile Phone](https://github.com/gekomad/scala-regex-collection/wiki/ItalianMobilePhone) (+393471234561 - 3381234561)
- [Italian Phone](https://github.com/gekomad/scala-regex-collection/wiki/ItalianPhone) (02 645566 - 02/583725 - 02-583725)

Date time

- [24 Hours time](https://github.com/gekomad/scala-regex-collection/wiki/24-Hours-time)  (23:50:00)
- [LocalDateTime](https://github.com/gekomad/scala-regex-collection/wiki/LocalDateTime)  (2000-12-31T11:21:19)
- [LocalDate](https://github.com/gekomad/scala-regex-collection/wiki/LocalDate)  (2000-12-31)
- [LocalTime](https://github.com/gekomad/scala-regex-collection/wiki/LocalTime)  (11:21:19)
- [OffsetDateTime](https://github.com/gekomad/scala-regex-collection/wiki/OffsetDateTime)  (2011-12-03T10:15:30+01:00)
- [OffsetTime](https://github.com/gekomad/scala-regex-collection/wiki/OffsetTime)  (10:15:30+01:00)
- [ZonedDateTime](https://github.com/gekomad/scala-regex-collection/wiki/ZonedDateTime)  (2016-12-02T11:15:30-05:00)
- [MDY](https://github.com/gekomad/scala-regex-collection/wiki/MDY) (1/12/1902 - 12/31/1902)
- [MDY2](https://github.com/gekomad/scala-regex-collection/wiki/MDY2)  (1-12-1902)
- [MDY3](https://github.com/gekomad/scala-regex-collection/wiki/MDY3)  (01/01/1900 - 12/31/9999)
- [MDY4](https://github.com/gekomad/scala-regex-collection/wiki/MDY4)  (01-12-1902 - 12-31-2018)
- [DMY](https://github.com/gekomad/scala-regex-collection/wiki/DMY)  (1/12/1902)
- [DMY2](https://github.com/gekomad/scala-regex-collection/wiki/DMY2)  (12-31-1902 - 1-12-1902)
- [DMY3](https://github.com/gekomad/scala-regex-collection/wiki/DMY3)  (01/12/1902 - 01/12/1902)
- [DMY4](https://github.com/gekomad/scala-regex-collection/wiki/DMY4)  (01-12-1902 - 01-12-1902)
- [Time](https://github.com/gekomad/scala-regex-collection/wiki/Time)  (8am - 8 pm - 11 PM - 8:00 am)

Crontab

- [Crontab expression](https://github.com/gekomad/scala-regex-collection/wiki/Crontab-expression) (5 4 * * *)

Codes

- [Italian fiscal code](https://github.com/gekomad/scala-regex-collection/wiki/Italian-fiscal-code) (BDAPPP14A01A001R)
- [Italian VAT code](https://github.com/gekomad/scala-regex-collection/wiki/ItalianVAT) (13297040362)
- [Italian Iban](https://github.com/gekomad/scala-regex-collection/wiki/ItalianIban) (IT28 W800 0000 2921 0064 5211 151 - IT28W8000000292100645211151)
- [US states](https://github.com/gekomad/scala-regex-collection/wiki/USstates) (FL - CA)
- [US states1](https://github.com/gekomad/scala-regex-collection/wiki/USstates1) (Connecticut - Colorado)
- [US zip code](https://github.com/gekomad/scala-regex-collection/wiki/USZipCode)  (43802)
- [US streets](https://github.com/gekomad/scala-regex-collection/wiki/USStreets)  (123 Park Ave Apt 123 New York City, NY 10002)
- [US street numbers](https://github.com/gekomad/scala-regex-collection/wiki/USStreetNumber)  (P.O. Box 432)
- [Italian zip code](https://github.com/gekomad/scala-regex-collection/wiki/ItalianZipCode) (23887)
- [German streets](https://github.com/gekomad/scala-regex-collection/wiki/GermanStreet) (Mühlenstr. 33)

Concurrency

- [USD Currency](https://github.com/gekomad/scala-regex-collection/wiki/USD-Currency)  ($1.00 - 1,500.00)
- [EUR Currency](https://github.com/gekomad/scala-regex-collection/wiki/EurCurrency)  (0,00 € - 133,89 EUR - 133,89 EURO)
- [YEN Currency](https://github.com/gekomad/scala-regex-collection/wiki/YenCurrency)  (¥1.00 - 15.00 - ¥-1213,120.00)

Strings

- [Not ASCII](https://github.com/gekomad/scala-regex-collection/wiki/NotASCII)  (テスト。)
- [Single char ASCII](https://github.com/gekomad/scala-regex-collection/wiki/SingleChar)  (A)
- [A-Z string](https://github.com/gekomad/scala-regex-collection/wiki/AZString)  (abc)
- [String and number](https://github.com/gekomad/scala-regex-collection/wiki/StringAndNumber)  (a1)
- [ASCII string](https://github.com/gekomad/scala-regex-collection/wiki/AsciiString)  (a1%)

Logs

- [Apache error](https://github.com/gekomad/scala-regex-collection/wiki/ApacheError)  ([Fri Dec 16 02:25:55 2005] [error] [client 1.2.3.4] Client sent malformed Host header)

Numbers

- [Number1](https://github.com/gekomad/scala-regex-collection/wiki/Number1)  (99.99 - 1.1 - .99)
- [Unsigned32](https://github.com/gekomad/scala-regex-collection/wiki/Unsigned32)  (0 - 122 - 4294967295)
- [Signed](https://github.com/gekomad/scala-regex-collection/wiki/Signed)  (-10 - +122 - 99999999999999999999999999)
- [Percentage](https://github.com/gekomad/scala-regex-collection/wiki/Percentage)  (10%)
- [Scientific](https://github.com/gekomad/scala-regex-collection/wiki/Scientific)  (-2.384E-03)
- [Single number](https://github.com/gekomad/scala-regex-collection/wiki/SingleNumber)  (1)
- [Celsius](https://github.com/gekomad/scala-regex-collection/wiki/Celsius)  (-2.2 °C)
- [Fahrenheit](https://github.com/gekomad/scala-regex-collection/wiki/Fahrenheit)  (-2.2 °F)

Coordinates

- [Coordinate](https://github.com/gekomad/scala-regex-collection/wiki/Coordinate)  (N90.00.00 E180.00.00)
- [Coordinate1](https://github.com/gekomad/scala-regex-collection/wiki/Coordinate1)  (45°23'36.0" N 10°33'48.0" E)
- [Coordinate2](https://github.com/gekomad/scala-regex-collection/wiki/Coordinate2)  (12:12:12.223546"N - 15:17:6"S - 12°30'23.256547"S)


## Scaladoc API
[Scala doc](https://javadoc.io/doc/com.github.gekomad/itto-csv_2.12)

## Bugs and Feedback
For bugs, questions and discussions please use [Github Issues](https://github.com/gekomad/itto-csv/issues).

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.

---
layout: docs
title: Regex
permalink: docs/regex/
---

### Encode/Decode some regex

```scala
import java.util.UUID
import com.github.gekomad.ittocsv.core.Types.implicits._
given IttoCSVFormat = IttoCSVFormat.default

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



### Encode/Decode your own regex
Using **encode** for MyType:

```scala
given Decoder[String, MyType] = (s: String) => RegexValidator[MyType](regexString).validate(s)
```

Example encoding N:Int to "[N]"  spostarlo TODO

```scala
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
given IttoCSVFormat = IttoCSVFormat.default

case class MyType(a: Int)
case class Foo(a: MyType, b: Int)

import com.github.gekomad.ittocsv.core.ToCsv._
given FieldEncoder[MyType] = customFieldEncoder[MyType](node => s"[${node.a}]")

assert(toCsv(Foo(MyType(42), 99)) == "[42],99")
```

Using **decode** for MyType: spostarlo TODO

```scala
implicit def _d(implicit csvFormat: IttoCSVFormat): String => Either[ParseFailure, MyType] = (str: String) => ???
```

Example decoding "[N]" to N:Int

```scala
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import scala.util.Try
given IttoCSVFormat = IttoCSVFormat.default
import cats.data.NonEmptyList

case class MyType(a: Int)
case class Foo(a: MyType, b: Int)

import com.github.gekomad.ittocsv.core.FromCsv._

given Decoder[String, MyType] = str => {
  if (str.startsWith("[") && str.endsWith("]"))
    Try(str.substring(1, str.length - 1).toInt)
      .map(f => Right(MyType(f)))
      .getOrElse(Left(List(s"Not a MyType $str")))
  else Left(List(s"Wrong format $str"))
}

assert(fromCsv[Foo]("[42],99") == List(Right(Foo(MyType(42), 99))))
assert(fromCsv[Foo]("[x],99") == List(Left(List("Not a MyType [x]"))))
assert(fromCsv[Foo]("42,99") == List(Left(List("Wrong format 42"))))

```

The [TreeTest.scala](https://github.com/gekomad/itto-csv/blob/master/src/test/scala/TreeTest.scala) shows how to encode/decode a `Tree[Int]`


### Defined regex

(full list [scala-regex-collection](https://github.com/gekomad/scala-regex-collection))

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

Programming

- [Comments](https://github.com/gekomad/scala-regex-collection/wiki/Comments)  (/* foo */)


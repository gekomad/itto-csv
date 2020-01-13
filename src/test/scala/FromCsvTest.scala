import cats.Id
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}

import com.github.gekomad.ittocsv.core.{Convert, ParseFailure, Schema}
import org.scalatest.funsuite.AnyFunSuite

class FromCsvTest extends AnyFunSuite {

  test("csv string to type - 1") {

    import cats.data.Validated.{Invalid, Valid}
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Header._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(a: Int, b: Double, c: String, d: Option[Boolean])

    val schema                 = Schema.of[Foo]
    val fields: List[String]   = fieldNames[Foo]
    val csv: List[String]      = List("1", "3.14", "foo", "true")
    val p: Map[String, String] = fields.zip(csv).toMap
    assert(schema.readFrom(p) == Valid(Foo(1, 3.14, "foo", Some(true))))

    def e: Map[String, String] = Map("c" -> "true", "b" -> "xx", "d" -> "true")

    assert(
      schema.readFrom(e) == Invalid(NonEmptyList(ParseFailure("a is missing"), List(ParseFailure("xx is not Double"))))
    )

  }

  test("csv string to type - 2") {

    import cats.data.Validated.{Invalid, Valid}
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Header._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(
      a: Int,
      b: Double,
      c: String,
      d: Option[Boolean],
      e: Option[String],
      f: Option[String],
      e1: Option[Double],
      f1: Option[Double],
      e2: Option[Int],
      f2: Option[Int]
    )

    val schema                 = Schema.of[Foo]
    val fields: List[String]   = fieldNames[Foo]
    val csv: List[String]      = List("1", "3.14", "foo", "", "", "hi", "", "3.3", "", "100")
    val p: Map[String, String] = fields.zip(csv).toMap
    assert(schema.readFrom(p) == Valid(Foo(1, 3.14, "foo", None, None, Some("hi"), None, Some(3.3), None, Some(100))))

  }

  test("csv string to types 3") {

    import cats.data.Validated.{Invalid, Valid}
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Header._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(a: Int, b: Char, c: String, d: Option[Boolean])

    val schema                 = Schema.of[Foo]
    val fields: List[String]   = fieldNames[Foo]
    val csv: List[String]      = List("1", "λ", "foo", "baz")
    val p: Map[String, String] = fields.zip(csv).toMap
    assert(schema.readFrom(p) == Invalid(NonEmptyList(ParseFailure("Not a Boolean for input string: baz"), List())))

  }

  test("tokenizeCsvLine to types ok") {
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Header._
    import cats.data.Validated.Valid
    import com.github.gekomad.ittocsv.util.StringUtils._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(a: Int, b: Double, c: String, d: Boolean)

    val fields: List[String]      = fieldNames[Foo]
    val csv: Option[List[String]] = tokenizeCsvLine("1,3.14,foo,true")
    csv match {
      case None => assert(false)
      case Some(g) =>
        assert(g == List("1", "3.14", "foo", "true"))
        val schema                 = Schema.of[Foo]
        val p: Map[String, String] = fields.zip(g).toMap
        assert(schema.readFrom(p) == Valid(Foo(1, 3.14, "foo", true)))
    }
  }

  test("tokenizeCsvLine to types boolean ko") {
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Header._
    import cats.data.Validated.Invalid
    import com.github.gekomad.ittocsv.util.StringUtils._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(a: Int, b: Double, c: String, d: Boolean)

    val fields: List[String]      = fieldNames[Foo]
    val csv: Option[List[String]] = tokenizeCsvLine("1,3.14,foo,bar")
    csv match {
      case None => assert(false)
      case Some(g) =>
        assert(g == List("1", "3.14", "foo", "bar"))
        val schema                 = Schema.of[Foo]
        val p: Map[String, String] = fields.zip(g).toMap
        assert(schema.readFrom(p) == Invalid(NonEmptyList(ParseFailure("bar is not Boolean"), Nil)))
    }
  }

  test("tokenizeCsvLine to types Option[Double] ko") {
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Header._
    import cats.data.Validated.Invalid
    import com.github.gekomad.ittocsv.util.StringUtils._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(a: Int, b: Double, c: String, d: Option[Double])

    val fields: List[String]      = fieldNames[Foo]
    val csv: Option[List[String]] = tokenizeCsvLine("1,3.14,foo,bar")
    csv match {
      case None => assert(false)
      case Some(g) =>
        assert(g == List("1", "3.14", "foo", "bar"))
        val schema                 = Schema.of[Foo]
        val p: Map[String, String] = fields.zip(g).toMap
        assert(schema.readFrom(p) == Invalid(NonEmptyList(ParseFailure("""Not a Double for input string: bar"""), Nil)))
    }
  }

  test("from csv SHA1") {
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.core.Types.implicits.SHA1
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.ParseFailure
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(a: String, b: SHA1)
    assert(
      fromCsv[Bar]("abc,1c18da5dbf74e3fc1820469cf1f54355b7eec92d") == List(
        Right(Bar("abc", SHA1("1c18da5dbf74e3fc1820469cf1f54355b7eec92d")))
      )
    )

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("Not a SHA1 hi"), Nil))))
  }

  test("List[Int] ok") {
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    val a: ValidatedNel[ParseFailure, Id[List[Int]]] = Convert.f[Int, Id].parse("1,2,3")
    assert(a == Valid(List(1, 2, 3)))
  }

  test("List[Int] ko") {
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    val a: ValidatedNel[ParseFailure, Id[List[Int]]] = Convert.f[Int, Id].parse("foo,bar,3")
    assert(a == Invalid(NonEmptyList(ParseFailure("Bad type on foo"), List(ParseFailure("Bad type on bar")))))
  }

  test("from csv SHA256") {
    import com.github.gekomad.ittocsv.core.Types.implicits.SHA256
    import com.github.gekomad.ittocsv.core.FromCsv._

    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(a: String, b: SHA256)
    assert(
      fromCsv[Bar]("abc,000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1") == List(
        Right(Bar("abc", SHA256("000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1")))
      )
    )

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("Not a SHA256 hi"), Nil))))

  }

  test("from csv IP") {
    import com.github.gekomad.ittocsv.core.Types.implicits.IP
    import com.github.gekomad.ittocsv.core.FromCsv._

    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(a: String, b: IP)
    assert(fromCsv[Bar]("abc,10.192.168.1") == List(Right(Bar("abc", IP("10.192.168.1")))))

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("Not a IP hi"), Nil))))
  }

  test("from csv IP6") {
    import com.github.gekomad.ittocsv.core.Types.implicits.IP6
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(a: String, b: IP6)
    assert(fromCsv[Bar]("abc,2001:db8:a0b:12f0::1") == List(Right(Bar("abc", IP6("2001:db8:a0b:12f0::1")))))

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("Not a IP6 hi"), Nil))))
  }

  test("from csv MD5") {
    import com.github.gekomad.ittocsv.core.Types.implicits.MD5
    import com.github.gekomad.ittocsv.core.FromCsv._

    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(a: String, b: MD5)
    assert(
      fromCsv[Bar]("abc,23f8e84c1f4e7c8814634267bd456194") == List(
        Right(Bar("abc", MD5("23f8e84c1f4e7c8814634267bd456194")))
      )
    )

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("Not a MD5 hi"), Nil))))
  }

  test("from csv uuid") {
    import java.util.UUID
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.Conversions.toUUIDS
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(a: String, b: UUID)

    assert(
      fromCsv[Bar]("abc,487d414d-67a6-4c2f-b95b-d811561ccd75") == List(
        Right(Bar("abc", UUID.fromString("487d414d-67a6-4c2f-b95b-d811561ccd75")))
      )
    )

    assert(
      fromCsv[Bar]("abc,xxc586e2-7cc3-4d39-a449-") == List(
        Left(NonEmptyList(ParseFailure("xxc586e2-7cc3-4d39-a449- is not UUID"), Nil))
      )
    )

  }

  test("from csv url") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: String, b: URL, c: URL1, d: URL2, e: URL3)

    assert(
      fromCsv[Bar]("abc,http://abc.def.com,http://www.aaa.com,http://www.aaa.com,https://www.google.com:8080/url?") == List(
        Right(
          Bar(
            "abc",
            URL("http://abc.def.com"),
            URL1("http://www.aaa.com"),
            URL2("http://www.aaa.com"),
            URL3("https://www.google.com:8080/url?")
          )
        )
      )
    )

    assert(
      fromCsv[Bar]("abc,www.aaa.com,abc.def.com,abc.def.com,abc.def.com") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a URL www.aaa.com"),
            List(
              ParseFailure("Not a URL1 abc.def.com"),
              ParseFailure("Not a URL2 abc.def.com"),
              ParseFailure("Not a URL3 abc.def.com")
            )
          )
        )
      )
    )
  }

  test("from csv ftp domain") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: String, b: FTP, c: FTP1, d: FTP2, e: Domain)

    assert(
      fromCsv[Bar]("abc,ftp://aaa.com,ftp://aaa.com,ftps://aaa.com,plus.google.com") == List(
        Right(
          Bar("abc", FTP("ftp://aaa.com"), FTP1("ftp://aaa.com"), FTP2("ftps://aaa.com"), Domain("plus.google.com"))
        )
      )
    )

    assert(
      fromCsv[Bar]("abc,www.aaa.com,abc.def.com,abc.def.com,abc") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a FTP www.aaa.com"),
            List(
              ParseFailure("Not a FTP1 abc.def.com"),
              ParseFailure("Not a FTP2 abc.def.com"),
              ParseFailure("Not a Domain abc")
            )
          )
        )
      )
    )
  }

  test("HEX") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: HEX, b: HEX1, c: HEX2, d: HEX3)

    assert(
      fromCsv[Bar]("F0F0F0,#F0F0F0,0xF0F0F0,0xF0F0F0") ==
        List(Right(Bar(HEX("F0F0F0"), HEX1("#F0F0F0"), HEX2("0xF0F0F0"), HEX3("0xF0F0F0"))))
    )

    assert(
      fromCsv[Bar]("aa,bb,cc,dd") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a HEX aa"),
            List(ParseFailure("Not a HEX1 bb"), ParseFailure("Not a HEX2 cc"), ParseFailure("Not a HEX3 dd"))
          )
        )
      )
    )
  }

  test("GermanStreet") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: GermanStreet)

    assert(fromCsv[Bar]("Mühlenstr. 33") == List(Right(Bar(GermanStreet("Mühlenstr. 33")))))

    assert(fromCsv[Bar]("aa") == List(Left(NonEmptyList(ParseFailure("Not a GermanStreet aa"), Nil))))
  }

  test("SingleChar") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(b: SingleChar)
    assert(fromCsv[Bar]("a") == List(Right(Bar(SingleChar("a")))))
    assert(fromCsv[Bar]("aa") == List(Left(NonEmptyList(ParseFailure("Not a SingleChar aa"), Nil))))
  }

  test("AZString") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(b: AZString)
    assert(fromCsv[Bar]("aA") == List(Right(Bar(AZString("aA")))))
    assert(fromCsv[Bar]("1") == List(Left(NonEmptyList(ParseFailure("Not a AZString 1"), Nil))))
  }

  test("StringAndNumber") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(b: StringAndNumber)
    assert(fromCsv[Bar]("aA1") == List(Right(Bar(StringAndNumber("aA1")))))
    assert(fromCsv[Bar]("$") == List(Left(NonEmptyList(ParseFailure("Not a StringAndNumber $"), Nil))))
  }

  test("AsciiString") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(b: AsciiString)
    assert(fromCsv[Bar]("aA1%") == List(Right(Bar(AsciiString("aA1%")))))
    assert(fromCsv[Bar]("テ") == List(Left(NonEmptyList(ParseFailure("Not a AsciiString テ"), Nil))))
  }

  test("SingleNumber") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    final case class Bar(b: SingleNumber)
    assert(fromCsv[Bar]("1") == List(Right(Bar(SingleNumber("1")))))
    assert(fromCsv[Bar]("11") == List(Left(NonEmptyList(ParseFailure("Not a SingleNumber 11"), Nil))))
  }

  test("MACAddress") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: MACAddress)

    assert(fromCsv[Bar]("fE:dC:bA:98:76:54") == List(Right(Bar(MACAddress("fE:dC:bA:98:76:54")))))

    assert(fromCsv[Bar]("aa") == List(Left(NonEmptyList(ParseFailure("Not a MACAddress aa"), Nil))))
  }

  test("Phones") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: USphoneNumber, b: ItalianMobilePhone, c: ItalianPhone)

    assert(
      fromCsv[Bar]("555-555-5555,+393471234561,02 645566") == List(
        Right(Bar(USphoneNumber("555-555-5555"), ItalianMobilePhone("+393471234561"), ItalianPhone("02 645566")))
      )
    )

    assert(
      fromCsv[Bar]("aa,bb,cc") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a USphoneNumber aa"),
            List(ParseFailure("Not a ItalianMobilePhone bb"), ParseFailure("Not a ItalianPhone cc"))
          )
        )
      )
    )

  }

  test("Time") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(
      a1: MDY,
      a2: MDY2,
      a3: MDY3,
      a4: MDY4,
      a11: DMY,
      a21: DMY2,
      a31: DMY3,
      a41: DMY4,
      b: Time,
      c: Time24
    )

    assert(
      fromCsv[Bar]("1/12/1902,1-12-1902,01/01/1900,01-12-1902,1/12/1902,1-12-1902,01/12/1902,01-12-1902,8am,23:50:00") == List(
        Right(
          Bar(
            MDY("1/12/1902"),
            MDY2("1-12-1902"),
            MDY3("01/01/1900"),
            MDY4("01-12-1902"),
            DMY("1/12/1902"),
            DMY2("1-12-1902"),
            DMY3("01/12/1902"),
            DMY4("01-12-1902"),
            Time("8am"),
            Time24("23:50:00")
          )
        )
      )
    )

    assert(
      fromCsv[Bar]("1,2,3,4,5,6,7,8,9,10") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a MDY 1"),
            List(
              ParseFailure("Not a MDY2 2"),
              ParseFailure("Not a MDY3 3"),
              ParseFailure("Not a MDY4 4"),
              ParseFailure("Not a DMY 5"),
              ParseFailure("Not a DMY2 6"),
              ParseFailure("Not a DMY3 7"),
              ParseFailure("Not a DMY4 8"),
              ParseFailure("Not a Time 9"),
              ParseFailure("Not a Time24 10")
            )
          )
        )
      )
    )

  }

  test("Coordinates") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default.withQuote('|')

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: Coordinate, b: Coordinate1, c: Coordinate2)

    assert(
      fromCsv[Bar]("""N90.00.00 E180.00.00,45°23'36.0" N 10°33'48.0" E,12:12:12.223546"N""") ==
        List(
          Right(
            Bar(
              Coordinate("N90.00.00 E180.00.00"),
              Coordinate1("""45°23'36.0" N 10°33'48.0" E"""),
              Coordinate2("""12:12:12.223546"N""")
            )
          )
        )
    )

    assert(
      fromCsv[Bar]("a,b,c") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a Coordinate a"),
            List(ParseFailure("Not a Coordinate1 b"), ParseFailure("Not a Coordinate2 c"))
          )
        )
      )
    )
  }

  test("Zip code") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: USZipCode, b: ItalianZipCode)

    assert(
      fromCsv[Bar]("43802,23887") ==
        List(Right(Bar(USZipCode("43802"), ItalianZipCode("23887"))))
    )

    assert(
      fromCsv[Bar]("a,b") == List(
        Left(NonEmptyList(ParseFailure("Not a USZipCode a"), List(ParseFailure("Not a ItalianZipCode b"))))
      )
    )
  }

  test("Numbers") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: Number1, b: Signed, c: Unsigned32, d: Percentage, e: Scientific)

    assert(
      fromCsv[Bar]("99.99,-10,4294967295,10%,-2.384E-03") ==
        List(
          Right(
            Bar(Number1("99.99"), Signed("-10"), Unsigned32("4294967295"), Percentage("10%"), Scientific("-2.384E-03"))
          )
        )
    )

    assert(
      fromCsv[Bar]("a,b,c,d,e") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a Number1 a"),
            List(
              ParseFailure("Not a Signed b"),
              ParseFailure("Not a Unsigned32 c"),
              ParseFailure("Not a Percentage d"),
              ParseFailure("Not a Scientific e")
            )
          )
        )
      )
    )
  }

  test("Codes") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(
      a: ItalianFiscalCode,
      b: ItalianVAT,
      c: ItalianIban,
      d: USstates,
      e: USstates1,
      f: USstreets,
      g: USstreetNumber
    )

    assert(
      fromCsv[Bar](
        """BDAPPP14A01A001R,13297040362,IT28 W800 0000 2921 0064 5211 151,CA,Florida,"123 Park Ave Apt 123 New York City, NY 10002",P.O. Box 432"""
      ) ==
        List(
          Right(
            Bar(
              ItalianFiscalCode("BDAPPP14A01A001R"),
              ItalianVAT("13297040362"),
              ItalianIban("IT28 W800 0000 2921 0064 5211 151"),
              USstates("CA"),
              USstates1("Florida"),
              USstreets("123 Park Ave Apt 123 New York City, NY 10002"),
              USstreetNumber("P.O. Box 432")
            )
          )
        )
    )

    assert(
      fromCsv[Bar]("a,b,c,d,e,f,g") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a ItalianFiscalCode a"),
            List(
              ParseFailure("Not a ItalianVAT b"),
              ParseFailure("Not a ItalianIban c"),
              ParseFailure("Not a USstates d"),
              ParseFailure("Not a USstates1 e"),
              ParseFailure("Not a USstreets f"),
              ParseFailure("Not a USstreetNumber g")
            )
          )
        )
      )
    )
  }

  test("BitcoinAdd") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: BitcoinAdd)

    assert(
      fromCsv[Bar]("3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v") == List(
        Right(Bar(BitcoinAdd("3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v")))
      )
    )

    assert(fromCsv[Bar]("aa") == List(Left(NonEmptyList(ParseFailure("Not a BitcoinAdd aa"), Nil))))
  }

  test("Celsius") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: Celsius)

    assert(fromCsv[Bar]("+2 °C") == List(Right(Bar(Celsius("+2 °C")))))

    assert(fromCsv[Bar]("aa") == List(Left(NonEmptyList(ParseFailure("Not a Celsius aa"), Nil))))
  }

  test("Fahrenheit") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: Fahrenheit)

    assert(fromCsv[Bar]("+2 °F") == List(Right(Bar(Fahrenheit("+2 °F")))))

    assert(fromCsv[Bar]("aa") == List(Left(NonEmptyList(ParseFailure("Not a Fahrenheit aa"), Nil))))
  }

  test("ApacheError") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: ApacheError)

    assert(
      fromCsv[Bar]("[Fri Dec 16 02:25:55 2005] [error] [client 1.2.3.4] Client sent malformed Host header") == List(
        Right(Bar(ApacheError("[Fri Dec 16 02:25:55 2005] [error] [client 1.2.3.4] Client sent malformed Host header")))
      )
    )

    assert(fromCsv[Bar]("aa") == List(Left(NonEmptyList(ParseFailure("Not a ApacheError aa"), Nil))))
  }

  test("Concurrency") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: UsdCurrency, b: EurCurrency, c: YenCurrency)

    assert(
      fromCsv[Bar]("$1.00,\"133,89 EUR\",¥1.00") == List(
        Right(Bar(UsdCurrency("$1.00"), EurCurrency("133,89 EUR"), YenCurrency("¥1.00")))
      )
    )

    assert(
      fromCsv[Bar]("a,b,c") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a UsdCurrency a"),
            List(ParseFailure("Not a EurCurrency b"), ParseFailure("Not a YenCurrency c"))
          )
        )
      )
    )
  }

  test("NotASCII") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: NotASCII)

    assert(fromCsv[Bar]("テスト。") == List(Right(Bar(NotASCII("テスト。")))))

    assert(fromCsv[Bar]("aa") == List(Left(NonEmptyList(ParseFailure("Not a NotASCII aa"), Nil))))
  }

  test("Crontab") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: Cron)

    assert(fromCsv[Bar]("5 4 * * *") == List(Right(Bar(Cron("5 4 * * *")))))

    assert(fromCsv[Bar]("aa") == List(Left(NonEmptyList(ParseFailure("Not a Cron aa"), Nil))))
  }

  test("from csv social") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: Youtube, c: Facebook, d: Twitter)

    assert(
      fromCsv[Bar](
        "https://www.youtube.com/watch?v=9bZkp7q19f0,https://www.facebook.com/pages/,https://twitter.com/rtpharry/"
      ) == List(
        Right(
          Bar(
            Youtube("https://www.youtube.com/watch?v=9bZkp7q19f0"),
            Facebook("https://www.facebook.com/pages/"),
            Twitter("https://twitter.com/rtpharry/")
          )
        )
      )
    )

    assert(
      fromCsv[Bar]("aa,bb,cc") == List(
        Left(
          NonEmptyList(
            ParseFailure("Not a Youtube aa"),
            List(ParseFailure("Not a Facebook bb"), ParseFailure("Not a Twitter cc"))
          )
        )
      )
    )
  }

  test("decode custom type") {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import scala.util.Try
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    final case class MyType(a: Int)
    final case class Foo(a: MyType, b: Int)

    import com.github.gekomad.ittocsv.core.FromCsv._

    implicit def _l(implicit csvFormat: IttoCSVFormat): String => Either[ParseFailure, MyType] = { str: String =>
      if (str.startsWith("[") && str.endsWith("]"))
        Try(str.substring(1, str.length - 1).toInt)
          .map(f => Right(MyType(f)))
          .getOrElse(Left(ParseFailure(s"Not a MyType $str")))
      else Left(ParseFailure(s"Wrong format $str"))

    }

    assert(fromCsv[Foo]("[42],99") == List(Right(Foo(MyType(42), 99))))
    assert(fromCsv[Foo]("[x],99") == List(Left(NonEmptyList(ParseFailure("Not a MyType [x]"), Nil))))
    assert(fromCsv[Foo]("42,99") == List(Left(NonEmptyList(ParseFailure("Wrong format 42"), Nil))))

  }

  test("from csv url with custom parser") {

    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits.URL

    final case class Bar(a: String, b: URL)

    assert(fromCsv[Bar]("abc,http://abc.def.com") == List(Right(Bar("abc", URL("http://abc.def.com")))))
    assert(fromCsv[Bar]("abc,https://abc.def.com") == List(Right(Bar("abc", URL("https://abc.def.com")))))
    assert(fromCsv[Bar]("abc,www.aaa.com") == List(Left(NonEmptyList(ParseFailure("Not a URL www.aaa.com"), Nil))))

    {
      import com.github.gekomad.ittocsv.core.Types.Validator
      implicit val _l: Validator[URL] =
        com.github.gekomad.ittocsv.core.Types.implicits.validatorURL
          .copy(regex = """[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?""")

      assert(fromCsv[Bar]("abc,www.aaa.com") == List(Right(Bar("abc", URL("www.aaa.com")))))
    }
  }

  test("from csv email") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits.Email

    final case class Bar(a: String, b: Email)

    assert(fromCsv[Bar]("abc,aaa@aai.sss") == List(Right(Bar("abc", Email("aaa@aai.sss")))))
    assert(fromCsv[Bar]("abc,$aaa@aai.sss") == List(Right(Bar("abc", Email("$aaa@aai.sss")))))
    assert(fromCsv[Bar]("abc,a@i.d") == List(Right(Bar("abc", Email("a@i.d")))))
    assert(fromCsv[Bar]("abc,a@%.d") == List(Left(NonEmptyList(ParseFailure("Not a Email a@%.d"), Nil))))
    assert(fromCsv[Bar]("abc,a @i.d") == List(Left(NonEmptyList(ParseFailure("Not a Email a @i.d"), Nil))))
    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("Not a Email hi"), Nil))))
    assert(fromCsv[Bar]("abc,hi@") == List(Left(NonEmptyList(ParseFailure("Not a Email hi@"), Nil))))
    assert(fromCsv[Bar]("abc,@") == List(Left(NonEmptyList(ParseFailure("Not a Email @"), Nil))))
    assert(fromCsv[Bar]("abc,@.com") == List(Left(NonEmptyList(ParseFailure("Not a Email @.com"), Nil))))
    assert(fromCsv[Bar]("abc,hi@g.") == List(Left(NonEmptyList(ParseFailure("Not a Email hi@g."), Nil))))
    assert(fromCsv[Bar]("abc,hi@.d") == List(Left(NonEmptyList(ParseFailure("Not a Email hi@.d"), Nil))))
    assert(fromCsv[Bar]("abc,") == List(Left(NonEmptyList(ParseFailure("Not a Email "), Nil))))
    assert(fromCsv[Bar]("abc, ") == List(Left(NonEmptyList(ParseFailure("Not a Email  "), Nil))))
  }

  test("from csv emailSimple") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits.EmailSimple

    final case class Bar(a: String, b: EmailSimple)

    assert(fromCsv[Bar]("abc,aaa@aai.sss") == List(Right(Bar("abc", EmailSimple("aaa@aai.sss")))))
    assert(fromCsv[Bar]("abc,a@i.d") == List(Right(Bar("abc", EmailSimple("a@i.d")))))
    assert(fromCsv[Bar]("abc,a@%.d") == List(Right(Bar("abc", EmailSimple("a@%.d")))))

  }

  test("from csv email1") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits.Email1

    final case class Bar(a: String, b: Email1)

    assert(fromCsv[Bar]("abc,aaa@ai.sss") == List(Right(Bar("abc", Email1("aaa@ai.sss")))))
    assert(fromCsv[Bar]("abc,a@i.da") == List(Right(Bar("abc", Email1("a@i.da")))))
    assert(fromCsv[Bar]("abc,a@%.da") == List(Left(NonEmptyList(ParseFailure("Not a Email1 a@%.da"), Nil))))

  }

  test("from csv email with custom parser") {

    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    import com.github.gekomad.ittocsv.core.Types.implicits.Email

    final case class Bar(a: String, b: Email)
    import com.github.gekomad.ittocsv.core.Types.Validator
    implicit val _l: Validator[Email] =
      com.github.gekomad.ittocsv.core.Types.implicits.validatorEmail.copy(regex = """.+@.+\..+""")

    assert(fromCsv[Bar]("abc,a@%.d") == List(Right(Bar("abc", Email("a@%.d")))))
  }

  test("from csv to type") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    final case class Bar(a: String, b: Int)
    assert(fromCsv[Bar]("abc,42") == List(Right(Bar("abc", 42))))
    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("hi is not Int"), Nil))))
  }

  test("from csv to List of type") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    final case class Bar(a: String, b: Int)
    assert(fromCsv[Bar]("abc,42\r\nfoo,24") == List(Right(Bar("abc", 42)), Right(Bar("foo", 24))))
  }

  test("tokenizeCsvLine to types complete") {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(a: Int, b: Double, c: Option[String], d: Boolean)

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    val o = fromCsv[Foo]("1,3.14,foo,true")

    assert(o == List(Right(Foo(1, 3.14, Some("foo"), true))))

  }

  test("list of csv string to list of type") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    final case class Foo(a: Int, b: Double, c: String, d: Boolean)
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default
    val o                                 = fromCsv[Foo](List("1,3.14,foo,true", "2,3.14,bar,false")) // List[Either[NonEmptyList[ParseFailure], Foo]]
    assert(o == List(Right(Foo(1, 3.14, "foo", true)), Right(Foo(2, 3.14, "bar", false))))
  }

  test("list of csv string to list of type with empty string and ignoreEmptyLines false") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    final case class Foo(a: Int)
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default
    val o                                 = fromCsv[Foo](List("1", "")) // List[Either[NonEmptyList[ParseFailure], Foo]]
    assert(o == List(Right(Foo(1)), Left(NonEmptyList(ParseFailure(" is not Int"), Nil))))
  }

  test("list of csv string to list of type with empty string and ignoreEmptyLines true") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    final case class Foo(a: Int)
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default.withIgnoreEmptyLines(true)
    val o                                 = fromCsv[Foo](List("1", "", "2")) // List[Either[NonEmptyList[ParseFailure], Foo]]
    assert(o == List(Right(Foo(1)), Right(Foo(2))))
  }

  test("decode Option[List[Int]]") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(v: String, a: Option[List[Int]])

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", Some(List(1, 2, 3))))))
    assert(fromCsv[Foo]("abc,\"1,xy,3\"") == List(Left(cats.data.NonEmptyList(ParseFailure("Bad type on xy"), Nil))))

  }

  test("decode List[Char]") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(v: String, a: List[Char])

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("abc,\"a,b,c\"") == List(Right(Foo("abc", List('a', 'b', 'c')))))
    assert(fromCsv[Foo]("abc,\"1,xy,3\"") == List(Left(cats.data.NonEmptyList(ParseFailure("Bad type on xy"), Nil))))

  }

  test("decode List[Boolean]") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(a: List[Boolean])

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("\"true,false\"") == List(Right(Foo(List(true, false)))))
    assert(fromCsv[Foo]("\"abc,false\"") == List(Left(cats.data.NonEmptyList(ParseFailure("Bad type on abc"), Nil))))

  }

  test("decode List[Int]") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(v: String, a: List[Int])

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", List(1, 2, 3)))))
    assert(fromCsv[Foo]("abc,\"1,xy,3\"") == List(Left(cats.data.NonEmptyList(ParseFailure("Bad type on xy"), Nil))))

  }

  test("decode List[Double]") {

    import com.github.gekomad.ittocsv.core.FromCsv._

    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    assert(fromCsvL[Double]("1.1,2.1,3.1") == List(Right(1.1), Right(2.1), Right(3.1)))
    assert(fromCsvL[Double]("1.1,abc,3.1") == List(Right(1.1), Left(ParseFailure("abc is not Double")), Right(3.1)))
    assert(fromCsvL[Double]("") == List(Left(ParseFailure(" is not Double"))))

  }

  test("decode LocalDateTime") {

    import java.time.LocalDateTime

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Conversions._

    final case class Foo(a: Int, b: LocalDateTime)

    implicit val csvFormat = IttoCSVFormat.default

    {
      val o = fromCsv[Foo]("1,2000-12-31T11:21:19")
      assert(
        o == List(
          Right(
            Foo(1, LocalDateTime.parse("2000-12-31T11:21:19", java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
          )
        )
      )
    }

  }

  test("decode Option[LocalDate]") {
    import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
    import java.time.LocalDate

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Conversions._

    final case class Foo(a: Int, b: Option[LocalDate])

    implicit val csvFormat = IttoCSVFormat.default

    {
      val o = fromCsv[Foo]("1,2000-12-31")
      assert(o == List(Right(Foo(1, Some(LocalDate.parse("2000-12-31", ISO_LOCAL_DATE))))))
    }

  }

  test("decode Option[LocalDateTime]") {
    import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
    import java.time.LocalDateTime

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Conversions._

    final case class Foo(a: Int, b: Option[LocalDateTime])

    implicit val csvFormat = IttoCSVFormat.default

    {
      val o = fromCsv[Foo]("1,2000-12-31T11:21:19")
      assert(o == List(Right(Foo(1, Some(LocalDateTime.parse("2000-12-31T11:21:19", ISO_LOCAL_DATE_TIME))))))
    }

  }

  test("decode date and time") {
    import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
    import java.time.LocalDateTime
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Conversions._
    import java.time.{LocalDate, LocalTime, OffsetDateTime}
    import java.time.format.DateTimeFormatter
    import java.time.ZonedDateTime
    import java.time.Instant

    final case class Foo(
      i: Instant,
      t: LocalTime,
      d: LocalDate,
      dt: OffsetDateTime,
      z: ZonedDateTime,
      ldt: LocalDateTime
    )

    implicit val csvFormat = IttoCSVFormat.default

    {
      val o = fromCsv[Foo](
        "2019-11-30T18:35:24.00Z,11:15:30,2019-12-27,2012-12-03T10:15:30+01:00,2019-04-01T17:24:11.252+05:30[Asia/Calcutta],2000-12-31T11:21:19"
      )
      assert(
        o == List(
          Right(
            Foo(
              Instant.parse("2019-11-30T18:35:24.00Z"),
              LocalTime.parse("11:15:30", DateTimeFormatter.ISO_LOCAL_TIME),
              LocalDate.parse("2019-12-27"),
              OffsetDateTime.parse("2012-12-03T10:15:30+01:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
              ZonedDateTime.parse("2019-04-01T17:24:11.252+05:30[Asia/Calcutta]"),
              LocalDateTime.parse("2000-12-31T11:21:19", ISO_LOCAL_DATE_TIME)
            )
          )
        )
      )
    }

  }

  test("decode custom Option[LocalDateTime]") {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(a: Int, b: Option[java.time.LocalDateTime])

    import scala.util.Try
    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter

    implicit val csvFormat = IttoCSVFormat.default

    implicit def localDateTimeToCsv: String => Either[ParseFailure, Option[LocalDateTime]] = {
      case "" => Right(None)
      case s =>
        Try {
          val x = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))
          Right(Some(x))
        }.getOrElse(Left(ParseFailure(s"Not a LocalDataTime $s")))

    }

    {
      val l = LocalDateTime.parse("2000-11-11 11:11:11.0", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))
      val o = fromCsv[Foo]("1,2000-11-11 11:11:11.0")
      assert(o == List(Right(Foo(1, Some(l)))))
    }

    {
      val o = fromCsv[Foo]("1,daigoro-xx-11 11:11:11.0")
      assert(o == List(Left(cats.data.NonEmptyList(ParseFailure("Not a LocalDataTime daigoro-xx-11 11:11:11.0"), Nil))))
    }
  }

}

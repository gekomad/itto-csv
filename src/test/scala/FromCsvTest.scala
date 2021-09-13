import cats.effect.{ExitCode, IO}
import org.junit.{Assert, Test}
import com.github.gekomad.ittocsv.core.FromCsv.fromCsvL
import com.github.gekomad.ittocsv.core.FromCsv.Decoder
import com.github.gekomad.ittocsv.core.FromCsv.list2Product

import com.github.gekomad.ittocsv.parser.IttoCSVFormat

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.Try

class FromCsvTest {

  @Test def csv_string_to_type_1(): Unit = {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    given IttoCSVFormat = IttoCSVFormat.default

    {
      final case class Foo(a: Int, b: Double, c: String, d: Option[Boolean])
      val csv: List[String] = List("1", "3.14", "foo", "true")
      val a = list2Product[Foo](csv)
      assert(a == Right(Foo(1, 3.14, "foo", Some(true))))
    }

    {
      final case class Foo(a: Int, b: Double, c: String, d: Boolean)
      val csv: List[String] = List("1", "3.14", "foo", "False")
      val a = list2Product[Foo](csv)
      assert(a == Right(Foo(1, 3.14, "foo", false)))
    }

  }

  @Test def csv_string_to_type_2(): Unit = {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    given IttoCSVFormat = IttoCSVFormat.default

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

    val csv: List[String] = List("1", "3.14", "foo", "", "", "hi", "", "3.3", "", "100")
    val a = list2Product[Foo](csv)
    assert(a == Right(Foo(1, 3.14, "foo", None, None, Some("hi"), None, Some(3.3), None, Some(100))))
  }

  @Test def csv_string_to_type_3(): Unit = {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    given IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(a: Int, b: Char, c: String, d: Option[Boolean])

    {
      val csv: List[String] = List("1", "λ", "foo", "true")
      assert(list2Product[Foo](csv) == Right(Foo(1, 'λ', "foo", Some(true))))
    }

    {
      val csv: List[String] = List("1", "λ", "foo", "baz")
      assert(list2Product[Foo](csv) == Left(List("baz value is not valid Boolean")))
    }

  }

  @Test def tokenizeCsvLine_to_types_ok(): Unit = {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.util.StringUtils._

    given IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(a: Int, b: Double, c: String, d: Boolean)

    val csv: Option[List[String]] = tokenizeCsvLine("1,3.14,foo,true")
    csv match {
      case None => assert(false)
      case Some(g) =>
        assert(g == List("1", "3.14", "foo", "true"))
        val a = list2Product[Foo](g)
        assert(a == Right(Foo(1, 3.14, "foo", true)))
    }
  }

  @Test def tokenizeCsvLine_to_types_boolean_ko(): Unit = {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    import cats.data.Validated.Invalid
    import com.github.gekomad.ittocsv.util.StringUtils._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(a: Int, b: Double, c: String, d: Boolean)

    val csv: Option[List[String]] = tokenizeCsvLine("1,3.14,foo,bar")
    csv match {
      case None => assert(false)
      case Some(g) =>
        assert(g == List("1", "3.14", "foo", "bar"))
        val a = list2Product[Foo](g)
        assert(a == Left(List("bar value is not valid Boolean")))
    }
  }

  @Test def tokenizeCsvLine_to_types_Option_Double__ko(): Unit = {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    import com.github.gekomad.ittocsv.util.StringUtils._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Foo(a: Int, b: Double, c: String, d: Option[Double])

    val csv: Option[List[String]] = tokenizeCsvLine("1,3.14,foo,bar")
    csv match {
      case None => assert(false)
      case Some(g) =>
        assert(g == List("1", "3.14", "foo", "bar"))
        val a = list2Product[Foo](g)
        assert(a == Left(List("bar value is not valid Double")))
    }
  }

  @Test def fromCsvSha1(): Unit = {

    import com.github.gekomad.ittocsv.core.Types.implicits.SHA1
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(a: String, b: SHA1)
    assert(
      fromCsv[Bar]("abc,1c18da5dbf74e3fc1820469cf1f54355b7eec92d") == List(
        Right(Bar("abc", SHA1("1c18da5dbf74e3fc1820469cf1f54355b7eec92d")))
      )
    )

    assert(fromCsv[Bar]("abc,hi") == List(Left(List("hi value is not valid SHA1"))))
  }

  @Test def fromCsvShaKO(): Unit = {

    import com.github.gekomad.ittocsv.core.Types.implicits.SHA1
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(a: SHA1, b: SHA1)
    val x = fromCsv[Bar]("abc,hi")
    assert(x == List(Left(List("abc value is not valid SHA1", "hi value is not valid SHA1"))))

  }

  @Test def List_Int_ok(): Unit = {
    given IttoCSVFormat = IttoCSVFormat.default
    import com.github.gekomad.ittocsv.core.FromCsv.fromCsvL
    val a = fromCsvL[Int]("1,2,3")
    assert(a == List(Right(1), Right(2), Right(3)))
  }

  @Test def List_Int_ko(): Unit = {
    given IttoCSVFormat = IttoCSVFormat.default
    import com.github.gekomad.ittocsv.core.FromCsv.fromCsvL
    val a = fromCsvL[Int]("foo,bar,3")
    assert(a == List(Left("foo value is not valid Int"), Left("bar value is not valid Int"), Right(3)))
  }

  @Test def from_csv_SHA256(): Unit = {
    import com.github.gekomad.ittocsv.core.Types.implicits.SHA256
    import com.github.gekomad.ittocsv.core.FromCsv._

    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(a: String, b: SHA256)
    assert(
      fromCsv[Bar]("abc,000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1") == List(
        Right(Bar("abc", SHA256("000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1")))
      )
    )

    assert(fromCsv[Bar]("abc,hi") == List(Left(List("hi value is not valid SHA256"))))

  }

  @Test def from_csv_IP(): Unit = {
    import com.github.gekomad.ittocsv.core.Types.implicits.IP
    import com.github.gekomad.ittocsv.core.FromCsv._

    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(a: String, b: IP)
    assert(fromCsv[Bar]("abc,10.192.168.1") == List(Right(Bar("abc", IP("10.192.168.1")))))

    assert(fromCsv[Bar]("abc,hi") == List(Left(List("hi value is not valid IP"))))
  }

  @Test def from_csv_IP6() = {
    import com.github.gekomad.ittocsv.core.Types.implicits.IP6
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(a: String, b: IP6)
    assert(fromCsv[Bar]("abc,2001:db8:a0b:12f0::1") == List(Right(Bar("abc", IP6("2001:db8:a0b:12f0::1")))))

    assert(fromCsv[Bar]("abc,hi") == List(Left(List("hi value is not valid IP6"))))
  }

  @Test def from_csv_MD5(): Unit = {
    import com.github.gekomad.ittocsv.core.Types.implicits.MD5
    import com.github.gekomad.ittocsv.core.FromCsv._

    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(a: String, b: MD5)
    assert(
      fromCsv[Bar]("abc,23f8e84c1f4e7c8814634267bd456194") == List(
        Right(Bar("abc", MD5("23f8e84c1f4e7c8814634267bd456194")))
      )
    )
    assert(fromCsv[Bar]("abc,hi") == List(Left(List("hi value is not valid MD5"))))
  }

  @Test def from_csv_UUID(): Unit = {
    import java.util.UUID
    import com.github.gekomad.ittocsv.core.FromCsv._

    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(a: String, b: UUID)

    assert(
      fromCsv[Bar]("abc,487d414d-67a6-4c2f-b95b-d811561ccd75") == List(
        Right(Bar("abc", UUID.fromString("487d414d-67a6-4c2f-b95b-d811561ccd75")))
      )
    )

    assert(
      fromCsv[Bar]("abc,xxc586e2-7cc3-4d39-a449-") == List(
        Left(List("xxc586e2-7cc3-4d39-a449- value is not valid UUID"))
      )
    )

  }

  @Test def from_csv_URL(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: String, b: URL, c: URL1, d: URL2, e: URL3)

    assert(
      fromCsv[Bar](
        "abc,http://abc.def.com,http://www.aaa.com,http://www.aaa.com,https://www.google.com:8080/url?"
      ) == List(
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
          List(
            "www.aaa.com value is not valid URL",
            "abc.def.com value is not valid URL1",
            "abc.def.com value is not valid URL2",
            "abc.def.com value is not valid URL3"
          )
        )
      )
    )
  }

  @Test def from_csv_domain(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

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
          List(
            "www.aaa.com value is not valid FTP",
            "abc.def.com value is not valid FTP1",
            "abc.def.com value is not valid FTP2",
            "abc value is not valid Domain"
          )
        )
      )
    )
  }

  @Test def hex(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: HEX, b: HEX1, c: HEX2, d: HEX3)

    assert(
      fromCsv[Bar]("F0F0F0,#F0F0F0,0xF0F0F0,0xF0F0F0") ==
        List(Right(Bar(HEX("F0F0F0"), HEX1("#F0F0F0"), HEX2("0xF0F0F0"), HEX3("0xF0F0F0"))))
    )

    assert(
      fromCsv[Bar]("aa,bb,cc,dd") == List(
        Left(
          List("aa value is not valid HEX",
               "bb value is not valid HEX1",
               "cc value is not valid HEX2",
               "dd value is not valid HEX3"
          )
        )
      )
    )
  }

  @Test def germanStreet(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: GermanStreet)

    assert(fromCsv[Bar]("Mühlenstr. 33") == List(Right(Bar(GermanStreet("Mühlenstr. 33")))))

    assert(fromCsv[Bar]("aa") == List(Left(List("aa value is not valid GermanStreet"))))
  }

  @Test def singleChar(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(b: SingleChar)
    assert(fromCsv[Bar]("a") == List(Right(Bar(SingleChar("a")))))
    assert(fromCsv[Bar]("aa") == List(Left(List("aa value is not valid SingleChar"))))
  }

  @Test def azString(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(b: AZString)
    assert(fromCsv[Bar]("aA") == List(Right(Bar(AZString("aA")))))
    assert(fromCsv[Bar]("1") == List(Left(List("1 value is not valid AZString"))))
  }

  @Test def stringAndNumber(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(b: StringAndNumber)
    assert(fromCsv[Bar]("aA1") == List(Right(Bar(StringAndNumber("aA1")))))
    assert(fromCsv[Bar]("$") == List(Left(List("$ value is not valid StringAndNumber"))))
  }

  @Test def asciiString(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(b: AsciiString)
    assert(fromCsv[Bar]("aA1%") == List(Right(Bar(AsciiString("aA1%")))))
    assert(fromCsv[Bar]("テ") == List(Left(List("テ value is not valid AsciiString"))))
  }

  @Test def singleNumber(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Types.implicits._
    given IttoCSVFormat = IttoCSVFormat.default

    final case class Bar(b: SingleNumber)
    assert(fromCsv[Bar]("1") == List(Right(Bar(SingleNumber("1")))))
    assert(fromCsv[Bar]("11") == List(Left(List("11 value is not valid SingleNumber"))))
  }

  @Test def macAddress(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: MACAddress)

    assert(fromCsv[Bar]("fE:dC:bA:98:76:54") == List(Right(Bar(MACAddress("fE:dC:bA:98:76:54")))))

    assert(fromCsv[Bar]("aa") == List(Left(List("aa value is not valid MACAddress"))))
  }

  @Test def phines(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

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
          List(
            "aa value is not valid USphoneNumber",
            "bb value is not valid ItalianMobilePhone",
            "cc value is not valid ItalianPhone"
          )
        )
      )
    )

  }

  @Test def time(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

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
      fromCsv[Bar](
        "1/12/1902,1-12-1902,01/01/1900,01-12-1902,1/12/1902,1-12-1902,01/12/1902,01-12-1902,8am,23:50:00"
      ) == List(
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
          List(
            "1 value is not valid MDY",
            "2 value is not valid MDY2",
            "3 value is not valid MDY3",
            "4 value is not valid MDY4",
            "5 value is not valid DMY",
            "6 value is not valid DMY2",
            "7 value is not valid DMY3",
            "8 value is not valid DMY4",
            "9 value is not valid Time",
            "10 value is not valid Time24"
          )
        )
      )
    )

  }

  @Test def coordinates(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default.withQuote('|')

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
          List("a value is not valid Coordinate",
               "b value is not valid Coordinate1",
               "c value is not valid Coordinate2"
          )
        )
      )
    )
  }

  @Test def zipCode(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(a: USZipCode, b: ItalianZipCode)

    assert(
      fromCsv[Bar]("43802,23887") ==
        List(Right(Bar(USZipCode("43802"), ItalianZipCode("23887"))))
    )

    assert(
      fromCsv[Bar]("a,b") == List(Left(List("a value is not valid USZipCode", "b value is not valid ItalianZipCode")))
    )
  }

  @Test def numbers(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

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
          List(
            "a value is not valid Number1",
            "b value is not valid Signed",
            "c value is not valid Unsigned32",
            "d value is not valid Percentage",
            "e value is not valid Scientific"
          )
        )
      )
    )
  }

  @Test def codes(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

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
          List(
            "a value is not valid ItalianFiscalCode",
            "b value is not valid ItalianVAT",
            "c value is not valid ItalianIban",
            "d value is not valid USstates",
            "e value is not valid USstates1",
            "f value is not valid USstreets",
            "g value is not valid USstreetNumber"
          )
        )
      )
    )
  }

  @Test def bitcoinAdd(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: BitcoinAdd)

    assert(
      fromCsv[Bar]("3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v") == List(
        Right(Bar(BitcoinAdd("3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v")))
      )
    )

    assert(fromCsv[Bar]("aa") == List(Left(List("aa value is not valid BitcoinAdd"))))
  }

  @Test def celsius(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: Celsius)

    assert(fromCsv[Bar]("+2 °C") == List(Right(Bar(Celsius("+2 °C")))))

    assert(fromCsv[Bar]("aa") == List(Left(List("aa value is not valid Celsius"))))
  }

  @Test def fahrenheit(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: Fahrenheit)

    assert(fromCsv[Bar]("+2 °F") == List(Right(Bar(Fahrenheit("+2 °F")))))

    assert(fromCsv[Bar]("aa") == List(Left(List("aa value is not valid Fahrenheit"))))
  }

  @Test def apacheError(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: ApacheError)

    assert(
      fromCsv[Bar]("[Fri Dec 16 02:25:55 2005] [error] [client 1.2.3.4] Client sent malformed Host header") == List(
        Right(Bar(ApacheError("[Fri Dec 16 02:25:55 2005] [error] [client 1.2.3.4] Client sent malformed Host header")))
      )
    )

    assert(fromCsv[Bar]("aa") == List(Left(List("aa value is not valid ApacheError"))))
  }

  @Test def concurrency(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

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
          List(
            "a value is not valid UsdCurrency",
            "b value is not valid EurCurrency",
            "c value is not valid YenCurrency"
          )
        )
      )
    )
  }

  @Test def notAscii(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: NotASCII)

    assert(fromCsv[Bar]("テスト。") == List(Right(Bar(NotASCII("テスト。")))))

    assert(fromCsv[Bar]("aa") == List(Left(List("aa value is not valid NotASCII"))))
  }

  @Test def crontab(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits._

    final case class Bar(b: Cron)

    assert(fromCsv[Bar]("5 4 * * *") == List(Right(Bar(Cron("5 4 * * *")))))

    assert(fromCsv[Bar]("aa") == List(Left(List("aa value is not valid Cron"))))
  }

  @Test def fromCsvSocial(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

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
          List(
            "aa value is not valid Youtube",
            "bb value is not valid Facebook",
            "cc value is not valid Twitter"
          )
        )
      )
    )
  }

  @Test def decodeCustomType(): Unit = { //TODO add in microsite

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import scala.util.Try
    given IttoCSVFormat = IttoCSVFormat.default

    final case class MyType(a: Int)
    final case class Foo(a: MyType, b: Int)

    import com.github.gekomad.ittocsv.core.FromCsv._

    given Decoder[String, MyType] = a =>
      (if (a.startsWith("[") && a.endsWith("]"))
         Try(a.substring(1, a.length - 1).toInt)
           .map(f => Some(MyType(f)))
           .getOrElse(None)
       else None).toRight(List(s"$a value is not valid MyType"))

    assert(fromCsv[Foo]("[42],99") == List(Right(Foo(MyType(42), 99))))
    assert(fromCsv[Foo]("[x],99") == List(Left(List("[x] value is not valid MyType"))))
    assert(fromCsv[Foo]("42,99") == List(Left(List("42 value is not valid MyType"))))

  }

  @Test def from_csv_email_with_custom_parser(): Unit = {

    import com.github.gekomad.ittocsv.core.Types.implicits.Email
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default
    import com.github.gekomad.regexcollection.Collection.Validator
    import com.github.gekomad.ittocsv.core.Types.RegexValidator

    final case class Bar(a: String, b: Email)

    assert(fromCsv[Bar]("abc,a@%.d") == List(Left(List("a@%.d value is not valid Email"))))

    {
      given Decoder[String, Email] = (a: String) => RegexValidator[Email](""".+@.+\..+""").validate(a)
      assert(fromCsv[Bar]("abc,a@%.d") == List(Right(Bar("abc", Email("a@%.d")))))
    }
  }

  @Test def from_csv_url_with_custom_parser(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits.URL

    final case class Bar(a: String, b: URL)
    assert(fromCsv[Bar]("abc,http://abc.def.com") == List(Right(Bar("abc", URL("http://abc.def.com")))))
    assert(fromCsv[Bar]("abc,https://abc.def.com") == List(Right(Bar("abc", URL("https://abc.def.com")))))
    assert(fromCsv[Bar]("abc,www.aaa.com") == List(Left(List("www.aaa.com value is not valid URL"))))

    {
      import com.github.gekomad.regexcollection.Collection.Validator
      import com.github.gekomad.ittocsv.core.Types.RegexValidator

      given Decoder[String, URL] = (a: String) =>
        RegexValidator[URL]("""[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?""")
          .validate(a)

      assert(fromCsv[Bar]("abc,www.aaa.com") == List(Right(Bar("abc", URL("www.aaa.com")))))
    }
  }

  @Test def from_csv_email(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits.Email

    final case class Bar(a: String, b: Email)

    assert(fromCsv[Bar]("abc,aaa@aai.sss") == List(Right(Bar("abc", Email("aaa@aai.sss")))))
    assert(fromCsv[Bar]("abc,$aaa@aai.sss") == List(Right(Bar("abc", Email("$aaa@aai.sss")))))
    assert(fromCsv[Bar]("abc,a@i.d") == List(Right(Bar("abc", Email("a@i.d")))))
    assert(fromCsv[Bar]("abc,a@%.d") == List(Left(List("a@%.d value is not valid Email"))))
    assert(fromCsv[Bar]("abc,a @i.d") == List(Left(List("a @i.d value is not valid Email"))))
    assert(fromCsv[Bar]("abc,hi") == List(Left(List("hi value is not valid Email"))))
    assert(fromCsv[Bar]("abc,hi@") == List(Left(List("hi@ value is not valid Email"))))
    assert(fromCsv[Bar]("abc,@") == List(Left(List("@ value is not valid Email"))))
    assert(fromCsv[Bar]("abc,@.com") == List(Left(List("@.com value is not valid Email"))))
    assert(fromCsv[Bar]("abc,hi@g.") == List(Left(List("hi@g. value is not valid Email"))))
    assert(fromCsv[Bar]("abc,hi@.d") == List(Left(List("hi@.d value is not valid Email"))))
    assert(fromCsv[Bar]("abc,") == List(Left(List(" value is not valid Email"))))
    assert(fromCsv[Bar]("abc, ") == List(Left(List("  value is not valid Email"))))
  }

  @Test def from_csv_email_simple(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits.EmailSimple

    final case class Bar(a: String, b: EmailSimple)

    assert(fromCsv[Bar]("abc,aaa@aai.sss") == List(Right(Bar("abc", EmailSimple("aaa@aai.sss")))))
    assert(fromCsv[Bar]("abc,a@i.d") == List(Right(Bar("abc", EmailSimple("a@i.d")))))
    assert(fromCsv[Bar]("abc,a@%.d") == List(Right(Bar("abc", EmailSimple("a@%.d")))))

  }

  @Test def from_csv_email1(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.implicits.Email1

    final case class Bar(a: String, b: Email1)

    assert(fromCsv[Bar]("abc,aaa@ai.sss") == List(Right(Bar("abc", Email1("aaa@ai.sss")))))
    assert(fromCsv[Bar]("abc,a@i.da") == List(Right(Bar("abc", Email1("a@i.da")))))
    assert(fromCsv[Bar]("abc,a@%.da") == List(Left(List("a@%.da value is not valid Email1"))))

  }

  @Test def from_csv_to_type(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default
    final case class Bar(a: String, b: Int)
    assert(fromCsv[Bar]("abc,42") == List(Right(Bar("abc", 42))))
    assert(fromCsv[Bar]("abc,hi") == List(Left(List("hi value is not valid Int"))))
  }

  @Test def from_csv_to_list_of_type(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._
    given IttoCSVFormat = IttoCSVFormat.default
    final case class Bar(a: String, b: Int)
    assert(fromCsv[Bar]("abc,42\r\nfoo,24") == List(Right(Bar("abc", 42)), Right(Bar("foo", 24))))
  }

  @Test def tokenizeCsvLine_to_types_complete: Unit = {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(a: Int, b: Double, c: Option[String], d: Boolean)

    given IttoCSVFormat = IttoCSVFormat.default

    val o = fromCsv[Foo]("1,3.14,foo,true")

    assert(o == List(Right(Foo(1, 3.14, Some("foo"), true))))

  }

  @Test def list_of_csv_string_to_list_of_type(): Unit = {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    final case class Foo(a: Int, b: Double, c: String, d: Boolean)
    given IttoCSVFormat = IttoCSVFormat.default
    val o = fromCsv[Foo](List("1,3.14,foo,true", "2,3.14,bar,false")) // List[Either[List[ParseFailure], Foo]]
    assert(o == List(Right(Foo(1, 3.14, "foo", true)), Right(Foo(2, 3.14, "bar", false))))
  }

  @Test def list_of_csv_string_to_list_of_type_with_empty_string_and_ignoreEmptyLines_false(): Unit = {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    final case class Foo(a: Int)
    given IttoCSVFormat = IttoCSVFormat.default
    val o = fromCsv[Foo](List("1", "")) // List[Either[List[ParseFailure], Foo]]
    assert(o == List(Right(Foo(1)), Left(List(" value is not valid Int"))))
  }

  @Test def list_of_csv_string_to_list_of_type_with_empty_string_and_ignoreEmptyLines_true(): Unit = {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    final case class Foo(a: Int)
    given IttoCSVFormat = IttoCSVFormat.default.withIgnoreEmptyLines(true)
    val o = fromCsv[Foo](List("1", "", "2"))
    assert(o == List(Right(Foo(1)), Right(Foo(2))))
  }

  @Test def decode_List_char(): Unit = {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(v: String, a: List[Char])

    given IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("abc,\"a,b,c\"") == List(Right(Foo("abc", List('a', 'b', 'c')))))
    assert(fromCsv[Foo]("abc,\"a,λ,c\"") == List(Right(Foo("abc", List('a', 'λ', 'c')))))
    assert(fromCsv[Foo]("abc,\"1,xy,3\"") == List(Left(List("xy value is not valid Char"))))
  }

  @Test def decodeOption_List_int(): Unit = {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(v: String, a: Option[List[Int]])

    given IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", Some(List(1, 2, 3))))))
    assert(fromCsv[Foo]("abc,\"1,xy,3\"") == List(Left(List("xy value is not valid Int"))))
  }

  @Test def decode_List_bool(): Unit = {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(a: List[Boolean])

    given IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("\"true,false\"") == List(Right(Foo(List(true, false)))))
    assert(fromCsv[Foo]("\"abc,false\"") == List(Left(List("abc value is not valid Boolean"))))

  }

  @Test def decode_List_int(): Unit = {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(v: String, a: List[Int])

    given IttoCSVFormat = IttoCSVFormat.default
    val a = fromCsv[Foo]("abc,\"1,2,3\"")
    assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", List(1, 2, 3)))))
    assert(fromCsv[Foo]("abc,\"1,xy,y\"") == List(Left(List("xy value is not valid Int", "y value is not valid Int"))))

  }

  @Test def decode_List_double(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv.fromCsvL

    given IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsvL[Double]("1.1,2.1,3.1") == List(Right(1.1), Right(2.1), Right(3.1)))
    assert(fromCsvL[Double]("1.1,abc,3.1") == List(Right(1.1), Left("abc value is not valid Double"), Right(3.1)))
    assert(
      fromCsvL[Double]("1.1,abc,foo") == List(
        Right(1.1),
        Left("abc value is not valid Double"),
        Left("foo value is not valid Double")
      )
    )
    assert(fromCsvL[Double]("") == List(Left(" value is not valid Double")))

  }

  @Test def decode_LocalDateTime(): Unit = {
    import java.time.LocalDateTime

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(a: Int, b: LocalDateTime)

    given IttoCSVFormat = IttoCSVFormat.default

    val o = fromCsv[Foo]("1,2000-12-31T11:21:19")
    assert(
      o == List(
        Right(
          Foo(1, LocalDateTime.parse("2000-12-31T11:21:19", java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        )
      )
    )

  }

  @Test def decode_Option_LocalDate(): Unit = {
    import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
    import java.time.LocalDate

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(a: Int, b: Option[LocalDate])

    given IttoCSVFormat = IttoCSVFormat.default

    val o = fromCsv[Foo]("1,2000-12-31")
    assert(o == List(Right(Foo(1, Some(LocalDate.parse("2000-12-31", ISO_LOCAL_DATE))))))

  }

  @Test def decode_LocalDateTime2(): Unit = {
    import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
    import java.time.LocalDateTime

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(a: Int, b: Option[LocalDateTime])

    given IttoCSVFormat = IttoCSVFormat.default

    {
      val o = fromCsv[Foo]("1,2000-12-31T11:21:19")
      assert(o == List(Right(Foo(1, Some(LocalDateTime.parse("2000-12-31T11:21:19", ISO_LOCAL_DATE_TIME))))))
    }

  }
  @Test def decode_date_and_time(): Unit = {

    import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
    import java.time.LocalDateTime
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
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

    given IttoCSVFormat = IttoCSVFormat.default

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

  @Test def CSVtoListOftTypeWithCustomLocalDateTime(): Unit = {
    import com.github.gekomad.ittocsv.core.FromCsv._

    given IttoCSVFormat = IttoCSVFormat.default

    case class Foo(a: Int, b: java.time.LocalDateTime)

    given Decoder[String, LocalDateTime] = { case s =>
      scala.util
        .Try {
          Right(LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0")))
        }
        .getOrElse(Left(List(s"Not a LocalDataTime $s")))
    }

    val l = fromCsv[Foo]("1,2000-12-31 11:21:19.0")
    assert(
      l == List(
        Right(
          Foo(1, LocalDateTime.parse("2000-12-31 11:21:19.0", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0")))
        )
      )
    )
  }

  @Test def decode_custom_option_LocalDateTime(): Unit = {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    import com.github.gekomad.ittocsv.core.FromCsv._

    final case class Foo(a: Int, b: Option[java.time.LocalDateTime])

    import scala.util.Try
    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter

    given IttoCSVFormat = IttoCSVFormat.default

    val pattern = "yyyy-MM-dd HH:mm:ss.0"
    given Decoder[String, Option[LocalDateTime]] = {
      case "" => Right(None)
      case s =>
        Try {
          val x = LocalDateTime.parse(s, DateTimeFormatter.ofPattern(pattern))
          Right(Some(x))
        }.getOrElse(Left(List(s"Not a LocalDataTime $s")))
    }

    given (String => Either[String, Option[LocalDateTime]]) = {
      case "" => Right(None)
      case s =>
        Try {
          val x = LocalDateTime.parse(s, DateTimeFormatter.ofPattern(pattern))
          Right(Some(x))
        }.getOrElse(Left(s"Not a LocalDataTime $s"))
    }

    {
      val l = LocalDateTime.parse("2000-11-11 11:11:11.0", DateTimeFormatter.ofPattern(pattern))
      val o = fromCsv[Foo]("1,2000-11-11 11:11:11.0")
      assert(o == List(Right(Foo(1, Some(l)))))
    }

    {
      val o = fromCsv[Foo]("1,daigoro-xx-11 11:11:11.0")
      assert(o == List(Left(List("Not a LocalDataTime daigoro-xx-11 11:11:11.0"))))
    }
  }

}

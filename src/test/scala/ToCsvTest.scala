import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
import org.junit.{Assert, Test}

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, OffsetDateTime, ZonedDateTime}
import java.time.format.DateTimeFormatter
import com.github.gekomad.ittocsv.core.Types.implicits.*
import com.github.gekomad.ittocsv.core.ToCsv.*
import com.github.gekomad.ittocsv.core.ToCsv
import com.github.gekomad.ittocsv.core.FromCsv.Decoder
import com.github.gekomad.ittocsv.core.ToCsv.given
import java.util.UUID
import scala.deriving.Mirror

class ToCsvTest {
  given IttoCSVFormat = IttoCSVFormat.default
  @Test def toCsvSHA1(): Unit = {

    final case class Bar(i: Int, a: SHA1)
    val x = Bar(1, SHA1("1c18da5dbf74e3fc1820469cf1f54355b7eec92d"))

    assert(toCsv(x) == "1,1c18da5dbf74e3fc1820469cf1f54355b7eec92d")
  }

  @Test def email(): Unit = {

    final case class Bar(i: Int, email: Email)

    assert(toCsv(Bar(1, Email("daigoro@itto.com"))) == "1,daigoro@itto.com")
  }

  @Test def email1(): Unit = {

    final case class Bar(i: Int, email: Email1)

    assert(toCsv(Bar(1, Email1("daigoro@itto.com"))) == "1,daigoro@itto.com")
  }

  @Test def emailSimple(): Unit = {

    final case class Bar(i: Int, email: EmailSimple)

    assert(toCsv(Bar(1, EmailSimple("daigoro@itto.com"))) == "1,daigoro@itto.com")
  }

  @Test def url(): Unit = {

    final case class Bar(i: Int, url: URL, url1: URL1, url2: URL2, url3: URL3)

    assert(
      toCsv(
        Bar(
          1,
          URL("http://aaa.ccc.com"),
          URL1("http://www.aaa.com"),
          URL2("http://www.aaa.com"),
          URL3("https://www.google.com:8080/url?")
        )
      ) ==
        "1,http://aaa.ccc.com,http://www.aaa.com,http://www.aaa.com,https://www.google.com:8080/url?"
    )
  }

  @Test def ftpDomain(): Unit = {

    final case class Bar(i: Int, b: FTP, c: FTP1, d: FTP2, e: Domain)

    assert(
      toCsv(Bar(1, FTP("ftp://aaa.com"), FTP1("ftp://aaa.com"), FTP2("ftps://aaa.com"), Domain("plus.google.com"))) ==
        "1,ftp://aaa.com,ftp://aaa.com,ftps://aaa.com,plus.google.com"
    )
  }

  @Test def social1(): Unit = {

    final case class Bar(b: Youtube, c: Facebook, d: Twitter)

    assert(
      toCsv(
        Bar(
          Youtube("https://www.youtube.com/watch?v=9bZkp7q19f0"),
          Facebook("http://www.facebook.com/thesimpsons"),
          Twitter("http://twitter.com/rtpharry/")
        )
      ) ==
        "https://www.youtube.com/watch?v=9bZkp7q19f0,http://www.facebook.com/thesimpsons,http://twitter.com/rtpharry/"
    )
  }

  @Test def macAddress(): Unit = {

    final case class Bar(i: Int, a: MACAddress)

    assert(toCsv(Bar(1, MACAddress("fE:dC:bA:98:76:54"))) == "1,fE:dC:bA:98:76:54")
  }

  @Test def phones(): Unit = {

    final case class Bar(a: USphoneNumber, b: ItalianMobilePhone, c: ItalianPhone)

    assert(
      toCsv(
        Bar(USphoneNumber("555-555-5555"), ItalianMobilePhone("+393471234561"), ItalianPhone("02 645566"))
      ) == "555-555-5555,+393471234561,02 645566"
    )
  }

  @Test def bitcoinAdd(): Unit = {

    final case class Bar(i: Int, a: BitcoinAdd)

    assert(toCsv(Bar(1, BitcoinAdd("3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v"))) == "1,3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v")
  }

  @Test def codes(): Unit = {

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
      toCsv(
        Bar(
          ItalianFiscalCode("BDAPPP14A01A001R"),
          ItalianVAT("13297040362"),
          ItalianIban("IT28 W800 0000 2921 0064 5211 151"),
          USstates("CA"),
          USstates1("Florida"),
          USstreets("123 Park Ave Apt 123 New York City, NY 10002"),
          USstreetNumber("P.O. Box 432")
        )
      ) == """BDAPPP14A01A001R,13297040362,IT28 W800 0000 2921 0064 5211 151,CA,Florida,"123 Park Ave Apt 123 New York City, NY 10002",P.O. Box 432"""
    )
  }

  @Test def coordinates(): Unit = {
    final case class Bar(a: Coordinate, b: Coordinate1, c: Coordinate2)
    given IttoCSVFormat = IttoCSVFormat.default.withQuote('|')

    assert(
      toCsv(
        Bar(
          Coordinate("N90.00.00 E180.00.00"),
          Coordinate1("""45°23'36.0" N 10°33'48.0" E"""),
          Coordinate2("""12:12:12.223546"N""")
        )
      ) == """N90.00.00 E180.00.00,45°23'36.0" N 10°33'48.0" E,12:12:12.223546"N"""
    )
  }

  @Test def numbers(): Unit = {

    final case class Bar(a: Number1, b: Signed, c: Unsigned32, d: Percentage, e: Scientific)

    assert(
      toCsv(
        Bar(Number1("99.99"), Signed("-10"), Unsigned32("4294967295"), Percentage("10%"), Scientific("-2.384E-03"))
      ) == "99.99,-10,4294967295,10%,-2.384E-03"
    )
  }

  @Test def zipCode(): Unit = {

    final case class Bar(a: USZipCode, b: ItalianZipCode)

    assert(toCsv(Bar(USZipCode("43802"), ItalianZipCode("23887"))) == "43802,23887")
  }

  @Test def germanStreet(): Unit = {

    final case class Bar(a: GermanStreet)

    assert(toCsv(Bar(GermanStreet("Mühlenstr. 33"))) == "Mühlenstr. 33")
  }

  @Test def singleChar(): Unit = {

    final case class Bar(a: SingleChar)
    assert(toCsv(Bar(SingleChar("a"))) == "a")
  }

  @Test def azString(): Unit = {

    final case class Bar(a: AZString)
    assert(toCsv(Bar(AZString("aA"))) == "aA")
  }

  @Test def celsius1(): Unit = {

    final case class Bar(a: Celsius)
    assert(toCsv(Bar(Celsius("+2 °C"))) == "+2 °C")
  }

  @Test def fahrenheit(): Unit = {

    final case class Bar(a: Fahrenheit)
    assert(toCsv(Bar(Fahrenheit("+2 °F"))) == "+2 °F")
  }

  @Test def stringAndNumber(): Unit = {

    final case class Bar(a: StringAndNumber)
    assert(toCsv(Bar(StringAndNumber("a1"))) == "a1")
  }

  @Test def asciiString(): Unit = {

    final case class Bar(a: AsciiString)
    assert(toCsv(Bar(AsciiString("a$"))) == "a$")
  }

  @Test def singleNumber(): Unit = {

    final case class Bar(a: SingleNumber)
    assert(toCsv(Bar(SingleNumber("3"))) == "3")
  }

  @Test def concurrency(): Unit = {

    final case class Bar(a: UsdCurrency, b: EurCurrency, c: YenCurrency)

    assert(
      toCsv(Bar(UsdCurrency("$1.00"), EurCurrency("133,89 EUR"), YenCurrency("¥1.00"))) == "$1.00,\"133,89 EUR\",¥1.00"
    )
  }

  @Test def crontab(): Unit = {

    final case class Bar(i: Int, a: Cron)

    assert(toCsv(Bar(1, Cron("5 4 * * *"))) == "1,5 4 * * *")
  }

  @Test def apacheError(): Unit = {

    final case class Bar(b: ApacheError)

    assert(
      toCsv(
        Bar(ApacheError("[Fri Dec 16 02:25:55 2005] [error] [client 1.2.3.4] Client sent malformed Host header"))
      ) == "[Fri Dec 16 02:25:55 2005] [error] [client 1.2.3.4] Client sent malformed Host header"
    )
  }

  @Test def noAscii(): Unit = {

    final case class Bar(b: NotASCII)

    assert(toCsv(Bar(NotASCII("テスト。"))) == "テスト。")
  }

  @Test def time(): Unit = {

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
      toCsv(
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
      ) ==
        "1/12/1902,1-12-1902,01/01/1900,01-12-1902,1/12/1902,1-12-1902,01/12/1902,01-12-1902,8am,23:50:00"
    )
  }

  @Test def hex(): Unit = {

    final case class Bar(a: HEX, b: HEX1, c: HEX2, d: HEX3)

    assert(
      toCsv(
        Bar(HEX("F0F0F0"), HEX1("#F0F0F0"), HEX2("0xF0F0F0"), HEX3("0xF0F0F0"))
      ) == "F0F0F0,#F0F0F0,0xF0F0F0,0xF0F0F0"
    )
  }

  @Test def ip(): Unit = {

    final case class Bar(i: Int, a: IP)

    assert(toCsv(Bar(1, IP("10.168.1.108"))) == "1,10.168.1.108")
  }

  @Test def ip6(): Unit = {

    final case class Bar(i: Int, a: IP6)

    assert(toCsv(Bar(1, IP6("2001:db8:a0b:12f0::1"))) == "1,2001:db8:a0b:12f0::1")
  }

  @Test def sha256(): Unit = {

    final case class Bar(i: Int, a: SHA256)

    assert(
      toCsv(
        Bar(1, SHA256("000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1"))
      ) == "1,000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1"
    )

  }

  @Test def uuid(): Unit = {
    final case class Bar(i: Int, a: UUID)

    assert(
      toCsv(Bar(1, UUID.fromString("1CC3CCBB-C749-3078-E050-1AACBE064651"))) == "1,1cc3ccbb-c749-3078-e050-1aacbe064651"
    )

  }

  @Test def type_to_csv_string(): Unit = {
    {
      given IttoCSVFormat = IttoCSVFormat.default.withDelimiter('.')
      final case class Bar(i: Int, salary: Double)
      val a = toCsv(Bar(1, 33003.3))
      assert(toCsv(Bar(1, 33003.3)) == "1.\"33003.3\"")

    }

    { // use default formatter

      given IttoCSVFormat = IttoCSVFormat.default.withPrintHeader(false)

      final case class Bar(name: String, date: java.util.Date, salary: Double)
      given FieldEncoder[java.util.Date] = customFieldEncoder[java.util.Date](_.toString)

      val d = new java.util.Date(0).toString

      assert(toCsv(Bar("Bo,b", new java.util.Date(0), 33003.3)) == s""""Bo,b",$d,33003.3""")

      assert(
        toCsv(List(Bar("Bob", new java.util.Date(0), 1111.3), Bar("Jim", new java.util.Date(0), 2222.2))) ==
          s"Bob,$d,1111.3,Jim,$d,2222.2"
      )
    }

    { // use tab formatter

      given IttoCSVFormat = IttoCSVFormat.tab.withRecordSeparator("\n")
      given FieldEncoder[java.util.Date] = customFieldEncoder[java.util.Date](_.toString)

      final case class Bar(name: String, date: java.util.Date, salary: Double)
      val d = new java.util.Date(0).toString
      assert(
        toCsv(Bar("Bo,b", new java.util.Date(0), 33003.3)) ==
          s"Bo,b\t$d\t33003.3"
      )

      assert(
        toCsv(List(Bar("Bob", new java.util.Date(0), 1111.3), Bar("Jim", new java.util.Date(0), 2222.2))) ==
          s"Bob\t$d\t1111.3\tJim\t$d\t2222.2"
      )
    }

    {

      final case class Baz(x: String)
      final case class Foo(a: Int, c: Baz)
      final case class Bar(a: String, b: Int, c: Foo)
      val a = toCsvFlat(Bar("hello", 3, Foo(1, Baz("hi, dude"))))

      assert(a == "hello,3,1,\"hi, dude\"")
    }
  }

  @Test def encode_custom_type(): Unit = {
    final case class MyType(a: Int)
    final case class Foo(a: MyType, b: Int)

    //encode
    import com.github.gekomad.ittocsv.core.ToCsv._
    given FieldEncoder[MyType] = customFieldEncoder[MyType](x => s"[${x.a}]")

    assert(toCsv(Foo(MyType(42), 99)) == "[42],99")

  }

  @Test def toCsvTest(): Unit = {

    object ToCsvT {

      import com.github.gekomad.ittocsv.core.Header._

      inline def toCsvT[A <: Product](
        csvT: (A, Long)
      )(using m: Mirror.ProductOf[A], e: RowEncoder[m.MirroredElemTypes], csvFormat: IttoCSVFormat): String =
        (if (csvT._2 == 0) csvHeader[A] else "") + toCsv(csvT._1, true)
    }

    given IttoCSVFormat = IttoCSVFormat.default.withDelimiter(';').withRecordSeparator("\n")
    import ToCsvT._

    final case class Foo(name: String)

    val l = for {
      c <- 0 to 5
    } yield toCsvT((Foo("id" + c), c))

    assert(l.mkString == "name\nid0\nid1\nid2\nid3\nid4\nid5")

  }

  @Test def type_to_csv_with_Instant(): Unit = {

    given IttoCSVFormat = IttoCSVFormat.default.withPrintHeader(false)

    val instant1 = Instant.parse("2010-11-30T18:35:24.00Z")
    val instant2 = Instant.parse("2011-11-30T18:35:24.00Z")
    val instant3 = Instant.parse("2012-11-30T18:35:24.00Z")
    val instant4 = Instant.parse("2013-11-30T18:35:24.00Z")

    final case class Bar(i: Option[Instant], e: Instant)
    val l: List[Bar] = List(Bar(Some(instant1), instant2), Bar(Some(instant3), instant4))
    assert(toCsv(l) == "2010-11-30T18:35:24Z,2011-11-30T18:35:24Z,2012-11-30T18:35:24Z,2013-11-30T18:35:24Z")
  }

  @Test def type_to_csv_with_date_and_time(): Unit = {

    import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
    import java.time.LocalDateTime

    given IttoCSVFormat = IttoCSVFormat.default.withPrintHeader(false)

    val localDateTime = LocalDateTime.parse("2000-12-31T12:13:14", ISO_LOCAL_DATE_TIME)
    val localTime = LocalTime.parse("11:15:30", DateTimeFormatter.ISO_LOCAL_TIME)
    val localDate = LocalDate.parse("2019-12-27")
    val offsetDateTime = OffsetDateTime.parse("2012-12-03T10:15:30+01:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val zonedDateTime = ZonedDateTime.parse("2019-04-01T17:24:11.252+05:30[Asia/Calcutta]")

    final case class Bar(
      a: LocalDateTime,
      b: LocalTime,
      c: Option[LocalDate],
      e: Option[OffsetDateTime],
      f: ZonedDateTime
    )
    val l: List[Bar] = List(Bar(localDateTime, localTime, Some(localDate), Some(offsetDateTime), zonedDateTime))
    assert(
      toCsv(
        l
      ) == "2000-12-31T12:13:14,11:15:30,2019-12-27,2012-12-03T10:15:30+01:00,2019-04-01T17:24:11.252+05:30[Asia/Calcutta]"
    )
  }

  @Test def type_to_csv_with_custom_localDateTime(): Unit = {

    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter
    given IttoCSVFormat = IttoCSVFormat.default.withPrintHeader(false)
    given FieldEncoder[LocalDateTime] =
      customFieldEncoder[LocalDateTime](_.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0")))

    val localDateTime =
      LocalDateTime.parse("2000-11-11 11:11:11.0", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))

    final case class Bar(a: String, b: Long, c: LocalDateTime, e: Option[Int])
    val l: List[Bar] = List(Bar("Yel,low", 3L, localDateTime, Some(1)), Bar("eee", 7L, localDateTime, None))
    val x = toCsv(l)
    assert(x == "\"Yel,low\",3,2000-11-11 11:11:11.0,1,eee,7,2000-11-11 11:11:11.0,")
  }

  @Test def from_type_to_csv(): Unit = {
    final case class Bar(a: String, b: Int)
    assert(toCsv(Bar("Bar", 42)) == "Bar,42")
  }

  @Test def from_list_of_type_to_csv(): Unit = {
    final case class Bar(a: String, b: Int)
    assert(toCsv(List(Bar("abc", 42), Bar("def", 24))) == "abc,42,def,24")
  }

  @Test def from_list_of_type_to_List_of_csv(): Unit = {
    final case class Bar(a: String, b: Int)
    assert(toCsvL(List(Bar("abc", 42), Bar("def", 24))) == "a,b\r\nabc,42\r\ndef,24")
  }

  @Test def serialize_List_Type(): Unit = {

    final case class Bar(c: String, a: Int)

    val x = List(Bar("abc", 1), Bar("def", 2))

    assert(toCsv(x) == "abc,1,def,2")
    assert(x.map(toCsv(_)) == List("abc,1", "def,2"))
  }

  @Test def serialize_List_Double(): Unit = {

    assert(toCsv(List(1.1, 2.1, 3.1)) == "1.1,2.1,3.1")

  }

  @Test def serialize_with_record_separator(): Unit = {
    final case class Foo(a: String, b: String)

    assert(toCsv(Foo("aaa", "bbb"), true) == "\r\naaa,bbb")

  }

  @Test def write_Csv_with_header(): Unit = {

    import com.github.gekomad.ittocsv.core.Header._
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    final case class Bar(name: String, date: java.util.Date, salary: Double)

    import com.github.gekomad.ittocsv.core.ToCsv._
    given IttoCSVFormat = IttoCSVFormat.tab
    given FieldEncoder[java.util.Date] = customFieldEncoder[java.util.Date](_.toString)

    def g[A <: Product](a: A)(using m: scala.deriving.Mirror.ProductOf[A], e: RowEncoder[m.MirroredElemTypes]): String =
      toCsv(a)

    val d = new java.util.Date(0).toString
    assert(g(Bar("Bo,b", new java.util.Date(0), 33003.3)) == s"Bo,b\t$d\t33003.3")

  }

  @Test def get_header1(): Unit = {
    import com.github.gekomad.ittocsv.core.Header._
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    final case class Record(i: Int, d: Double, s: String, b: Boolean)

    given IttoCSVFormat = IttoCSVFormat.default

    inline def g[A](using mirror: Mirror.Of[A]): String = csvHeader[A]

    val header = g[Record]
    assert(header == "i,d,s,b")
  }

  @Test def get_header2(): Unit = {
    import com.github.gekomad.ittocsv.core.Header._
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    final case class Record(X: Int, d: Double, s: String, b: Boolean)
    given IttoCSVFormat = IttoCSVFormat.default.withDelimiter('X')

    val header = csvHeader[Record]

    assert(header == "\"X\"XdXsXb")
  }
}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.github.gekomad.ittocsv.core.CsvStringEncoder
import org.scalatest.FunSuite

class ToCsvTest extends FunSuite {

  test("email") {
    import com.github.gekomad.ittocsv.core.Types.EmailOps._
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Employee(i: Int, email: Email)

    assert(toCsv(Employee(1, Email("daigoro@itto.com"))) == "1,daigoro@itto.com")

  }

  test("url") {
    import com.github.gekomad.ittocsv.core.Types.UrlOps._
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Employee(i: Int, email: URL)

    assert(toCsv(Employee(1, URL("http://aaa.ccc.com"))) == "1,http://aaa.ccc.com")

  }

  test("IP") {
    import com.github.gekomad.ittocsv.core.Types.IPOps.IP
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Employee(i: Int, a: IP)

    assert(toCsv(Employee(1, IP("10.168.1.108"))) == "1,10.168.1.108")
  }

  test("IP6") {
    import com.github.gekomad.ittocsv.core.Types.IPOps.IP6
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Employee(i: Int, a: IP6)

    assert(toCsv(Employee(1, IP6("2001:db8:a0b:12f0::1"))) == "1,2001:db8:a0b:12f0::1")
  }

  test("SHA1") {
    import com.github.gekomad.ittocsv.core.Types.SHAOps.SHA1
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Employee(i: Int, a: SHA1)

    assert(toCsv(Employee(1, SHA1("1c18da5dbf74e3fc1820469cf1f54355b7eec92d"))) == "1,1c18da5dbf74e3fc1820469cf1f54355b7eec92d")

  }

  test("SHA256") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.ToCsv._
    import com.github.gekomad.ittocsv.core.Types.SHAOps.SHA256
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Employee(i: Int, a: SHA256)

    assert(toCsv(Employee(1, SHA256("000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1"))) == "1,000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1")

  }

  test("UUID") {
    import java.util.UUID
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Employee(i: Int, a: UUID)

    assert(toCsv(Employee(1, UUID.fromString("1CC3CCBB-C749-3078-E050-1AACBE064651"))) == "1,1cc3ccbb-c749-3078-e050-1aacbe064651")

  }

  test("type to csv string") {

    {
      import com.github.gekomad.ittocsv.core.ToCsv._
      import com.github.gekomad.ittocsv.parser.IttoCSVFormat

      implicit val csvFormat: IttoCSVFormat =
        IttoCSVFormat.default.withDelimiter('.')

      case class Employee(i: Int, salary: Double)

      assert(toCsv(Employee(1, 33003.3)) == "1.\"33003.3\"")

    }

    { // use default formatter
      import com.github.gekomad.ittocsv.core.CsvStringEncoder
      import com.github.gekomad.ittocsv.core.ToCsv._
      import com.github.gekomad.ittocsv.parser.IttoCSVFormat
      implicit val csvFormat: IttoCSVFormat =
        IttoCSVFormat.default.withPrintHeader(false)

      case class Employee(name: String, date: java.util.Date, salary: Double)

      implicit val dateEncoder: CsvStringEncoder[java.util.Date] =
        new CsvStringEncoder[java.util.Date] {
          override def encode(value: java.util.Date): String = value.toString
        }

      val d = new java.util.Date(0).toString

      assert(toCsv(Employee("Bo,b", new java.util.Date(0), 33003.3)) == s""""Bo,b",$d,33003.3""")

      assert(
        toCsv(List(Employee("Bob", new java.util.Date(0), 1111.3), Employee("Jim", new java.util.Date(0), 2222.2))) ==
          s"Bob,$d,1111.3,Jim,$d,2222.2"
      )
    }

    { // use tab formatter
      import com.github.gekomad.ittocsv.core.CsvStringEncoder
      import com.github.gekomad.ittocsv.core.ToCsv._
      import com.github.gekomad.ittocsv.parser.IttoCSVFormat
      implicit val csvFormat: IttoCSVFormat =
        IttoCSVFormat.tab.withRecordSeparator("\n")
      implicit val dateEncoder: CsvStringEncoder[java.util.Date] =
        new CsvStringEncoder[java.util.Date] {
          override def encode(value: java.util.Date): String = value.toString
        }

      case class Employee(name: String, date: java.util.Date, salary: Double)
      val d = new java.util.Date(0).toString
      assert(
        toCsv(Employee("Bo,b", new java.util.Date(0), 33003.3)) ==
          s"Bo,b\t$d\t33003.3"
      )

      assert(
        toCsv(List(Employee("Bob", new java.util.Date(0), 1111.3), Employee("Jim", new java.util.Date(0), 2222.2))) ==
          s"Bob\t$d\t1111.3\tJim\t$d\t2222.2"
      )
    }

    {
      import com.github.gekomad.ittocsv.core.ToCsv._
      implicit val csvFormat =
        com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

      case class Baz(x: String)
      case class Foo(a: Int, c: Baz)
      case class Bar(a: String, b: Int, c: Foo)

      assert(toCsv(Bar("Bar", 3, Foo(1, Baz("hi, dude")))) == "Bar,3,1,\"hi, dude\"")
    }
  }

  test("ToCsvT") {

    object ToCsvT {

      import com.github.gekomad.ittocsv.core.Header._
      import com.github.gekomad.ittocsv.parser.IttoCSVFormat
      import com.github.gekomad.ittocsv.core.CsvStringEncoder
      import com.github.gekomad.ittocsv.core.ToCsv.toCsv

      def toCsvT[A: FieldNames](csvT: (A, Long))(implicit enc: CsvStringEncoder[A], csvFormat: IttoCSVFormat): String =
        (if (csvT._2 == 0) csvHeader[A] else "") + toCsv(csvT._1, true)

    }

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    implicit val csvFormat: IttoCSVFormat =
      IttoCSVFormat.default.withDelimiter(';').withRecordSeparator("\n")

    import com.github.gekomad.ittocsv.core.ToCsv._
    import ToCsvT._

    case class Foo(name: String)

    val l = for {
      c <- 0 to 5
    } yield toCsvT((Foo("id" + c), c))

    assert(l.mkString == "name\nid0\nid1\nid2\nid3\nid4\nid5")

  }

  test("type to csv with localDateTime") {
    import com.github.gekomad.ittocsv.core.ToCsv._
    import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
        .withPrintHeader(false)

    val localDateTime =
      LocalDateTime.parse("2000-12-31T12:13:14", ISO_LOCAL_DATE_TIME)

    case class Bar(a: String, b: Long, c: Option[LocalDateTime], e: Option[Int])
    val l: List[Bar] = List(Bar("Yel,low", 3L, Some(localDateTime), Some(1)), Bar("eee", 7L, Some(localDateTime), None))
    assert(toCsv(l) == "\"Yel,low\",3,2000-12-31T12:13:14,1,eee,7,2000-12-31T12:13:14,")
  }

  test("type to csv with custom localDateTime") {
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
        .withPrintHeader(false)

    implicit def localDateTimeEncoder(implicit csvFormat: com.github.gekomad.ittocsv.parser.IttoCSVFormat): CsvStringEncoder[LocalDateTime] = new CsvStringEncoder[LocalDateTime] {
      override def encode(value: LocalDateTime): String =
        value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))
    }

    val localDateTime =
      LocalDateTime.parse("2000-11-11 11:11:11.0", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))

    case class Bar(a: String, b: Long, c: LocalDateTime, e: Option[Int])
    val l: List[Bar] = List(Bar("Yel,low", 3L, localDateTime, Some(1)), Bar("eee", 7L, localDateTime, None))
    assert(toCsv(l) == "\"Yel,low\",3,2000-11-11 11:11:11.0,1,eee,7,2000-11-11 11:11:11.0,")
  }

  test("from type to csv") {
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    case class Bar(a: String, b: Int)
    assert(toCsv(Bar("Bar", 42)) == "Bar,42")
  }

  test("from list of type to csv") {
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    case class Bar(a: String, b: Int)
    assert(toCsv(List(Bar("abc", 42), Bar("def", 24))) == "abc,42,def,24")
  }

  test("from list of type to List of csv") {
    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    case class Bar(a: String, b: Int)
    assert(toCsvL(List(Bar("abc", 42), Bar("def", 24))) == "a,b\r\nabc,42\r\ndef,24")
  }

  test("serialize List[Type]") {

    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    case class Bar(c: String, a: Int)
    import com.github.gekomad.ittocsv.core.ToCsv._

    val x = List(Bar("abc", 1), Bar("def", 2))
    assert(toCsv(x) == "abc,1,def,2")
    assert(x.map(toCsv(_)) == List("abc,1", "def,2"))
  }

  test("serialize List[Double]") {

    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.ToCsv._

    assert(toCsv(List(1.1, 2.1, 3.1)) == "1.1,2.1,3.1")

  }

  test("serialize with record separator") {

    case class Foo(a: String, b: String)
    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    import com.github.gekomad.ittocsv.core.ToCsv._

    assert(toCsv(Foo("aaa", "bbb"), true) == "\r\naaa,bbb")

  }

  ignore("encode List[Short]") { //TODO togliere da master

    case class Foo(v: String, a: List[Short])

    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.ToCsv._

    assert(toCsv(Foo("abc", List(1, 2, 3))) == "abc,\"1,2,3\"")
    assert(toCsv(Foo("abc", List())) == "abc")

  }

  test("write Csv with header") {
    import com.github.gekomad.ittocsv.core.CsvStringEncoder
    import com.github.gekomad.ittocsv.core.Header._
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    case class Employee(name: String, date: java.util.Date, salary: Double)

    import com.github.gekomad.ittocsv.core.ToCsv._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.tab

    implicit val dateEncoder: CsvStringEncoder[java.util.Date] =
      new CsvStringEncoder[java.util.Date] {
        override def encode(value: java.util.Date): String = value.toString
      }

    def g[A: FieldNames](a: A)(implicit enc: CsvStringEncoder[A]): String =
      toCsv(a)

    val d = new java.util.Date(0).toString
    assert(g(Employee("Bo,b", new java.util.Date(0), 33003.3)) == s"Bo,b\t$d\t33003.3")

  }

  test("get header 1") {
    import com.github.gekomad.ittocsv.core.Header._
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    case class Record(i: Int, d: Double, s: String, b: Boolean)

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    def g[A: FieldNames]: String = csvHeader[A]

    val header = g[Record]
    assert(header == "i,d,s,b")
  }

  test("get header 2") {
    import com.github.gekomad.ittocsv.core.Header._
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    case class Record(X: Int, d: Double, s: String, b: Boolean)
    implicit val csvFormat: IttoCSVFormat =
      IttoCSVFormat.default.withDelimiter('X')

    val header = csvHeader[Record]

    assert(header == "\"X\"XdXsXb")
  }
}

import cats.data.NonEmptyList
import org.scalatest.FunSuite

class FromCsvTest extends FunSuite {

  test("csv string to type - 1") {

    import cats.data.Validated.{Invalid, Valid}
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Header._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Foo(a: Int, b: Double, c: String, d: Option[Boolean])

    val schema = Schema.of[Foo]
    val fields: List[String] = fieldNames[Foo]
    val csv: List[String] = List("1", "3.14", "foo", "true")
    val p: Map[String, String] = fields.zip(csv).toMap
    assert(schema.readFrom(p) == Valid(Foo(1, 3.14, "foo", Some(true))))

    def e: Map[String, String] = Map("c" -> "true", "b" -> "xx", "d" -> "true")

    assert(schema.readFrom(e) == Invalid(NonEmptyList(ParseFailure("a is missing"), List(ParseFailure("xx is not Double")))))

  }

  test("csv string to type - 2") {

    import cats.data.Validated.{Invalid, Valid}
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Header._
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Foo(a: Int, b: Double, c: String, d: Option[Boolean], e: Option[String], f: Option[String], e1: Option[Double], f1: Option[Double], e2: Option[Int], f2: Option[Int])

    val schema = Schema.of[Foo]
    val fields: List[String] = fieldNames[Foo]
    val csv: List[String] = List("1", "3.14", "foo", "", "", "hi", "", "3.3", "", "100")
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

    case class Foo(a: Int, b: Char, c: String, d: Option[Boolean])

    val schema = Schema.of[Foo]
    val fields: List[String] = fieldNames[Foo]
    val csv: List[String] = List("1", "λ", "foo", "baz")
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

    case class Foo(a: Int, b: Double, c: String, d: Boolean)

    val fields: List[String] = fieldNames[Foo]
    val csv: Option[List[String]] = tokenizeCsvLine("1,3.14,foo,true")
    csv match {
      case None => assert(false)
      case Some(g) =>
        assert(g == List("1", "3.14", "foo", "true"))
        val schema = Schema.of[Foo]
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

    case class Foo(a: Int, b: Double, c: String, d: Boolean)

    val fields: List[String] = fieldNames[Foo]
    val csv: Option[List[String]] = tokenizeCsvLine("1,3.14,foo,bar")
    csv match {
      case None => assert(false)
      case Some(g) =>
        assert(g == List("1", "3.14", "foo", "bar"))
        val schema = Schema.of[Foo]
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

    case class Foo(a: Int, b: Double, c: String, d: Option[Double])

    val fields: List[String] = fieldNames[Foo]
    val csv: Option[List[String]] = tokenizeCsvLine("1,3.14,foo,bar")
    csv match {
      case None => assert(false)
      case Some(g) =>
        assert(g == List("1", "3.14", "foo", "bar"))
        val schema = Schema.of[Foo]
        val p: Map[String, String] = fields.zip(g).toMap
        assert(schema.readFrom(p) == Invalid(NonEmptyList(ParseFailure("""Not a Double for input string: bar"""), Nil)))
    }
  }

  test("from csv SHA1") {
    import cats.data.NonEmptyList
    import com.github.gekomad.ittocsv.core.Types.SHAOps.SHA1
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Conversions.toSHA1s
    import com.github.gekomad.ittocsv.core.FromCsv.ParseFailure
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    case class Bar(a: String, b: SHA1)
    assert(fromCsv[Bar]("abc,1c18da5dbf74e3fc1820469cf1f54355b7eec92d") == List(Right(Bar("abc", SHA1("1c18da5dbf74e3fc1820469cf1f54355b7eec92d")))))

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("hi is not SHA1"), Nil))))
  }

  test("from csv SHA256") {
    import com.github.gekomad.ittocsv.core.Types.SHAOps.SHA256
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Conversions.toSHA256s
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    case class Bar(a: String, b: SHA256)
    assert(fromCsv[Bar]("abc,000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1") == List(Right(Bar("abc", SHA256("000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1")))))

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("hi is not SHA256"), Nil))))

  }

  test("from csv IP") {
    import com.github.gekomad.ittocsv.core.Types.IPOps.IP
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Conversions.toIPs
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    case class Bar(a: String, b: IP)
    assert(fromCsv[Bar]("abc,10.192.168.1") == List(Right(Bar("abc", IP("10.192.168.1")))))

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("hi is not IP"), Nil))))
  }

  test("from csv IP6") {
    import com.github.gekomad.ittocsv.core.Types.IPOps.IP6
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Conversions.toIP6s
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    case class Bar(a: String, b: IP6)
    assert(fromCsv[Bar]("abc,2001:db8:a0b:12f0::1") == List(Right(Bar("abc", IP6("2001:db8:a0b:12f0::1")))))

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("hi is not IP6"), Nil))))
  }

  test("from csv MD5") {
    import com.github.gekomad.ittocsv.core.Types.MD5Ops.MD5
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.Conversions.toMD5s
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    case class Bar(a: String, b: MD5)
    assert(fromCsv[Bar]("abc,23f8e84c1f4e7c8814634267bd456194") == List(Right(Bar("abc", MD5("23f8e84c1f4e7c8814634267bd456194")))))

    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("hi is not MD5"), Nil))))
  }

  test("from csv uuid") {
    import java.util.UUID
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.Conversions.toUUIDS
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    case class Bar(a: String, b: UUID)

    assert(fromCsv[Bar]("abc,1CC3CCBB-C749-3078-E050-1AACBE064651") == List(Right(Bar("abc", UUID.fromString("1CC3CCBB-C749-3078-E050-1AACBE064651")))))

    assert(fromCsv[Bar]("abc,a8c586e2-7cc3-4d39-a449-") == List(Left(NonEmptyList(ParseFailure("a8c586e2-7cc3-4d39-a449- is not UUID"), Nil))))

  }

  test("from csv url") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.UrlOps.URL

    case class Bar(a: String, b: URL)

    assert(fromCsv[Bar]("abc,http://abc.def.com") == List(Right(Bar("abc", URL("http://abc.def.com")))))
    assert(fromCsv[Bar]("abc,https://abc.def.com") == List(Right(Bar("abc", URL("https://abc.def.com")))))
    assert(fromCsv[Bar]("abc,www.aaa.com") == List(Left(NonEmptyList(ParseFailure("Not a URL www.aaa.com"), Nil))))
  }

  test("from csv url with custom parser") {

    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    import com.github.gekomad.ittocsv.core.Types.UrlOps.{URL, UrlValidator}

    case class Bar(a: String, b: URL)

    implicit val urlValidator = com.github.gekomad.ittocsv.core.Types.UrlOps.validator.copy(urlRegex = """^[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?$""")

    assert(fromCsv[Bar]("abc,http://abc.def.com") == List(Right(Bar("abc", URL("http://abc.def.com")))))
    assert(fromCsv[Bar]("abc,https://abc.def.com") == List(Right(Bar("abc", URL("https://abc.def.com")))))
    assert(fromCsv[Bar]("abc,www.aaa.com") == List(Right(Bar("abc", URL("www.aaa.com")))))
  }

  test("from csv email") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default

    import com.github.gekomad.ittocsv.core.Types.EmailOps.Email

    case class Bar(a: String, b: Email)

    assert(fromCsv[Bar]("abc,aaa@aai.sss") == List(Right(Bar("abc", Email("aaa@aai.sss")))))
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

  test("from csv email with custom parser") {

    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    import com.github.gekomad.ittocsv.core.Types.EmailOps.{Email, EmailValidator}

    case class Bar(a: String, b: Email)

    implicit val emailValidator = com.github.gekomad.ittocsv.core.Types.EmailOps.validator.copy(emailRegex = """.+@.+\..+""")

    assert(fromCsv[Bar]("abc,a@%.d") == List(Right(Bar("abc", Email("a@%.d")))))
  }

  test("from csv to type") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    case class Bar(a: String, b: Int)
    assert(fromCsv[Bar]("abc,42") == List(Right(Bar("abc", 42))))
    assert(fromCsv[Bar]("abc,hi") == List(Left(NonEmptyList(ParseFailure("hi is not Int"), Nil))))
  }

  test("from csv to List of type") {
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    case class Bar(a: String, b: Int)
    assert(fromCsv[Bar]("abc,42\r\nfoo,24") == List(Right(Bar("abc", 42)), Right(Bar("foo", 24))))
  }

  test("tokenizeCsvLine to types complete") {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    case class Foo(a: Int, b: Double, c: Option[String], d: Boolean)

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    val o = fromCsv[Foo]("1,3.14,foo,true")

    assert(o == List(Right(Foo(1, 3.14, Some("foo"), true))))

  }

  test("list of csv string to list of type") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    case class Foo(a: Int, b: Double, c: String, d: Boolean)
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default
    val o = fromCsv[Foo](List("1,3.14,foo,true", "2,3.14,bar,false")) // List[Either[NonEmptyList[ParseFailure], Foo]]
    assert(o == List(Right(Foo(1, 3.14, "foo", true)), Right(Foo(2, 3.14, "bar", false))))
  }

  test("list of csv string to list of type with empty string and ignoreEmptyLines false") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    case class Foo(a: Int)
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default
    val o = fromCsv[Foo](List("1", "")) // List[Either[NonEmptyList[ParseFailure], Foo]]
    assert(o == List(Right(Foo(1)), Left(NonEmptyList(ParseFailure(" is not Int"), Nil))))
  }

  test("list of csv string to list of type with empty string and ignoreEmptyLines true") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    case class Foo(a: Int)
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default.withIgnoreEmptyLines(true)
    val o = fromCsv[Foo](List("1", "", "2")) // List[Either[NonEmptyList[ParseFailure], Foo]]
    assert(o == List(Right(Foo(1)), Right(Foo(2))))
  }


  test("decode Option[List[Int]]") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.FromCsv._

    case class Foo(v: String, a: Option[List[Int]])

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", Some(List(1, 2, 3))))))
    assert(fromCsv[Foo]("abc,\"1,xy,3\"") == List(Left(cats.data.NonEmptyList(ParseFailure("Not a List[type] 1,xy,3"), Nil))))

  }

  test("decode List[Char]") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.FromCsv._

    case class Foo(v: String, a: List[Char])

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("abc,\"a,b,c\"") == List(Right(Foo("abc", List('a', 'b', 'c')))))
    assert(fromCsv[Foo]("abc,\"1,xy,3\"") == List(Left(cats.data.NonEmptyList(ParseFailure("Not a List[type] 1,xy,3"), Nil))))

  }


  test("decode List[Boolean]") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.FromCsv._

    case class Foo(a: List[Boolean])

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("\"true,false\"") == List(Right(Foo(List(true, false)))))
    assert(fromCsv[Foo]("\"abc,false\"") == List(Left(cats.data.NonEmptyList(ParseFailure("Not a List[type] abc,false"), Nil))))

  }

  test("decode List[Int]") {
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    import com.github.gekomad.ittocsv.core.FromCsv._

    case class Foo(v: String, a: List[Int])

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", List(1, 2, 3)))))
    assert(fromCsv[Foo]("abc,\"1,xy,3\"") == List(Left(cats.data.NonEmptyList(ParseFailure("Not a List[type] 1,xy,3"), Nil))))

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
    import com.github.gekomad.ittocsv.core.FromCsv.Convert.fromStringToLocalDateTime

    case class Foo(a: Int, b: LocalDateTime)

    implicit val csvFormat = IttoCSVFormat.default

    {
      val o = fromCsv[Foo]("1,2000-12-31T11:21:19")
      assert(o == List(Right(Foo(1, LocalDateTime.parse("2000-12-31T11:21:19", java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)))))
    }

  }

  test("decode Option[LocalDateTime]") {
    import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
    import java.time.LocalDateTime

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.FromCsv.Convert._

    case class Foo(a: Int, b: Option[LocalDateTime])


    implicit val csvFormat = IttoCSVFormat.default

    {
      val o = fromCsv[Foo]("1,2000-12-31T11:21:19")
      assert(o == List(Right(Foo(1, Some(LocalDateTime.parse("2000-12-31T11:21:19", ISO_LOCAL_DATE_TIME))))))
    }

  }

  test("decode custom Option[LocalDateTime]") {

    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    import com.github.gekomad.ittocsv.core.FromCsv._

    case class Foo(a: Int, b: Option[java.time.LocalDateTime])

    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter

    implicit val csvFormat = IttoCSVFormat.default

    //generic
    implicit def localDateTimeToCsv: String => Either[ParseFailure, Option[LocalDateTime]] = {
      case "" => Right(None)
      case s => try {
        val x = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0"))
        Right(Some(x))
      }
      catch {
        case _: Throwable => Left(ParseFailure(s"Not a LocalDataTime $s"))
      }
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


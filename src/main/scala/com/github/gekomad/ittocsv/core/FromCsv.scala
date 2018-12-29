package com.github.gekomad.ittocsv.core

import com.github.gekomad.ittocsv.core.Types.IPOps.{IP, IP6}
import com.github.gekomad.ittocsv.core.Types.UrlOps.{URL, UrlValidator}
import com.github.gekomad.ittocsv.util.TryCatch.tryCatch

/**
  * Converts a CSV to type
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information
  */
object FromCsv {

  import cats.data.NonEmptyList
  import com.github.gekomad.ittocsv.parser.IttoCSVFormat
  import com.github.gekomad.ittocsv.core.Header.{FieldNames, fieldNames}
  import com.github.gekomad.ittocsv.util.StringUtils.tokenizeCsvLine

  case class ParseFailure(error: String)

  /**
    * @param csv       is the string to parse. It might contain record separator
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return `Seq[Either[NonEmptyList[ParseFailure], A]]` based on the parsing of `csv`, any errors are reported
    *         {{{
    * import com.github.gekomad.ittocsv.core.FromCsv._
    * implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    *
    * case class Bar(a: String, b: Int)
    *
    * assert(fromCsv[Bar]("abc,42\r\nfoo,24") == List(Right(Bar("abc", 42)), Right(Bar("foo", 24))))
    * assert(fromCsv[Bar]("abc,hi") == List(Left(cats.data.NonEmptyList(com.github.gekomad.ittocsv.core.FromCsv.ParseFailure("hi is not Int"), Nil))))
    *
    * case class Foo(v: String, a: List[Int])
    * assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", List(1, 2, 3)))))
    *         }}}
    */
  def fromCsv[A: FieldNames : Schema](csv: String)(implicit csvFormat: IttoCSVFormat): Seq[Either[NonEmptyList[ParseFailure], A]] =
    fromCsv(csv.split(csvFormat.recordSeparator, -1).toList)

  import com.github.gekomad.ittocsv.core.Conversions._

  /**
    *
    * @param csv       is the string to parse. It might contain record separator
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return `Seq[Either[ParseFailure, A]]` based on the parsing of `csv`, any errors are reported
    *         {{{
    * import com.github.gekomad.ittocsv.core.FromCsv._
    *
    * implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    *
    * assert(fromCsvL[Double]("1.1,2.1,3.1") == List(Right(1.1), Right(2.1), Right(3.1)))
    * assert(fromCsvL[Double]("1.1,abc,3.1") == List(Right(1.1), Left(com.github.gekomad.ittocsv.core.FromCsv.ParseFailure("abc is not Double")), Right(3.1)))
    *         }}}
    */
  def fromCsvL[A: ConvertTo](csv: String)(implicit csvFormat: IttoCSVFormat): Seq[Either[ParseFailure, A]] = {
    val x = csv.split(csvFormat.delimeter.toString, -1).toList
    x.map(a => convert[A](a))
  }

  /**
    * @param csvList   is the List[String] to parse
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return `Seq[Either[NonEmptyList[ParseFailure], A]]` based on the parsing of `csvList` any errors are reported
    *         {{{
    * import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    * import com.github.gekomad.ittocsv.core.FromCsv._
    * case class Foo(a: Int, b: Double, c: String, d: Boolean)
    * implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default
    * val p1 = fromCsv[Foo](List("1,3.14,foo,true", "2,3.14,bar,false"))
    * assert(p1 == List(Right(Foo(1, 3.14, "foo", true)), Right(Foo(2, 3.14, "bar", false))))
    *         }}}
    */
  def fromCsv[A: FieldNames : Schema](csvList: List[String])(implicit csvFormat: IttoCSVFormat): Seq[Either[NonEmptyList[ParseFailure], A]] = {
    if (csvList.isEmpty) Nil else
      csvList collect { case row if !row.isEmpty || !csvFormat.ignoreEmptyLines =>
        tokenizeCsvLine(row) match {
          case None => Left(NonEmptyList(ParseFailure(s"$csvList is not a valid csv string"), Nil))
          case Some(t) =>
            val schema = Schema.of[A]
            val p: Map[String, String] = fieldNames[A].zip(t).toMap
            schema.readFrom(p).toEither
        }
      }
  }

  import java.util.UUID
  import com.github.gekomad.ittocsv.core.Conversions.ConvertTo
  import com.github.gekomad.ittocsv.core.Types.EmailOps._
  import com.github.gekomad.ittocsv.core.Types.MD5Ops.MD5
  import com.github.gekomad.ittocsv.core.Types.SHAOps.{SHA1, SHA256}
  import com.github.gekomad.ittocsv.parser.IttoCSVFormat
  import cats._, implicits._, data.ValidatedNel
  import shapeless._, labelled._

  private type Result[A] = ValidatedNel[ParseFailure, A]


  trait Convert[V] {
    def parse(input: String): Result[V]
  }

  object Convert {

    def to[V](input: String)(implicit C: Convert[V]): Result[V] =
      C.parse(input)

    def instance[V](body: String => Result[V]): Convert[V] = new Convert[V]{
      def parse(input: String): Result[V]= body(input)
    }

    implicit def optionLists[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[Option[List[A]]] =
      Convert.instance {
        s =>
          try {
            val x: List[A] = s.split(csvFormat.delimeter.toString, -1).toList.map { a =>
              com.github.gekomad.ittocsv.core.Conversions.convert[A](a).getOrElse(throw new Exception)
            }
            (Right(Some(x)): Either[ParseFailure, Option[List[A]]]).toValidatedNel
          }
          catch {
            case _: Throwable =>
              (Left(ParseFailure(s"Not a List[type] $s")): Either[ParseFailure, Option[List[A]]]).toValidatedNel
          }
      }

    implicit def urls(implicit csvFormat: IttoCSVFormat, urlValidator: UrlValidator): Convert[URL] =
      Convert.instance(urlValidator.validate(_).toValidatedNel)

    implicit def emails(implicit csvFormat: IttoCSVFormat, emailValidator: EmailValidator): Convert[Email] =
      Convert.instance(emailValidator.validate(_).toValidatedNel)

    implicit def lists[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[List[A]] =
      Convert.instance {
        s =>
          try {
            val x: List[A] = s.split(csvFormat.delimeter.toString, -1).toList.map(com.github.gekomad.ittocsv.core.Conversions.convert[A](_).getOrElse(throw new Exception))
            (Right(x): Either[ParseFailure, List[A]]).toValidatedNel
          }
          catch {
            case _: Throwable =>
              (Left(ParseFailure(s"Not a List[type] $s")): Either[ParseFailure, List[A]]).toValidatedNel
          }
      }

    implicit def md5s[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[MD5] =
      Convert.instance(Conversions.toMD5s.to(_).toValidatedNel: ValidatedNel[ParseFailure, MD5])

    implicit def ips[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[IP] =
      Convert.instance(Conversions.toIPs.to(_).toValidatedNel: ValidatedNel[ParseFailure, IP])

    implicit def ip6s[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[IP6] =
      Convert.instance(Conversions.toIP6s.to(_).toValidatedNel: ValidatedNel[ParseFailure, IP6])

    implicit def sha1s[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[SHA1] =
      Convert.instance(s => Conversions.toSHA1s.to(s).toValidatedNel: ValidatedNel[ParseFailure, SHA1])

    implicit def sha256s[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[SHA256] = {
      Convert.instance(s => Conversions.toSHA256s.to(s).toValidatedNel: ValidatedNel[ParseFailure, SHA256])
    }

    implicit def uuids[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[UUID] =
      Convert.instance(Conversions.toUUIDS.to(_).toValidatedNel: ValidatedNel[ParseFailure, UUID])

    implicit def generic[A](implicit f: String => Either[ParseFailure, A]): Convert[A] = Convert.instance(f(_).toValidatedNel)

    import java.time.LocalDateTime
    import java.time.LocalDate
    import java.time.LocalTime
    import java.time.OffsetDateTime
    import java.time.OffsetTime
    import java.time.ZonedDateTime

    import java.time.format.DateTimeFormatter.{
      ISO_LOCAL_DATE,
      ISO_LOCAL_DATE_TIME,
      ISO_LOCAL_TIME,
      ISO_OFFSET_DATE_TIME,
      ISO_OFFSET_TIME,
      ISO_ZONED_DATE_TIME
    }

    implicit val fromStringToLocalDateTime: String => Either[ParseFailure, LocalDateTime] = { s =>
      tryCatch(LocalDateTime.parse(s, ISO_LOCAL_DATE_TIME))(s"Not a LocalDataTime $s")
    }

    implicit def fromGeneralOption[A](implicit f: String => Either[ParseFailure, A]): String => Either[ParseFailure, Option[A]] = { s =>
      if (s == "") Right(None) else
        f(s).map(Some(_))
    }

    implicit val fromStringToLocalDate: String => Either[ParseFailure, LocalDate] = { s =>
      tryCatch(LocalDate.parse(s, ISO_LOCAL_DATE))(s"Not a LocalDate $s")
    }

    implicit val fromStringToLocalTime: String => Either[ParseFailure, LocalTime] = { s =>
      tryCatch(LocalTime.parse(s, ISO_LOCAL_TIME))(s"Not a LocalTime $s")
    }

    implicit val fromStringToOffsetDateTime: String => Either[ParseFailure, OffsetDateTime] = { s =>
      tryCatch(OffsetDateTime.parse(s, ISO_OFFSET_DATE_TIME))(s"Not a OffsetDateTime $s")
    }

    implicit val fromStringToOffsetTime: String => Either[ParseFailure, OffsetTime] = { s =>
      tryCatch(OffsetTime.parse(s, ISO_OFFSET_TIME))(s"Not a OffsetTime $s")
    }

    implicit val fromStringToZonedDateTime: String => Either[ParseFailure, ZonedDateTime] = { s =>
      tryCatch(ZonedDateTime.parse(s, ISO_ZONED_DATE_TIME))(s"Not a ZonedDateTime $s")
    }

    //////////////////////
    implicit val optionBoolean: Convert[Option[Boolean]] =
      Convert.instance {
        case "" => (Right(None): Either[ParseFailure, Option[Boolean]]).toValidatedNel
        case s => tryCatch(Some(s.toBoolean))(s"Not a Boolean for input string: $s").toValidatedNel
      }

    implicit val optionShort: Convert[Option[Short]] =
      Convert.instance {
        case "" => (Right(None): Either[ParseFailure, Option[Short]]).toValidatedNel
        case s => tryCatch(Some(s.toShort))(s"Not a Short for input string: $s").toValidatedNel
      }

    implicit val optionByte: Convert[Option[Byte]] =
      Convert.instance {
        case "" => (Right(None): Either[ParseFailure, Option[Byte]]).toValidatedNel
        case s => tryCatch(Some(s.toByte))(s"Not a Byte for input string: $s").toValidatedNel
      }

    implicit val optionChar: Convert[Option[Char]] =
      Convert.instance {
        case "" => (Right(None): Either[ParseFailure, Option[Char]]).toValidatedNel
        case s => tryCatch(if (s.length == 1) Some(s(0)) else throw new Exception)(s"Not a Char for input string: $s").toValidatedNel
      }

    implicit val optionString: Convert[Option[String]] = Convert.instance {
      case "" => (Right(None): Either[ParseFailure, Option[String]]).toValidatedNel
      case s => (Right(Some(s)): Either[ParseFailure, Option[String]]).toValidatedNel
    }

    implicit val optionDouble: Convert[Option[Double]] = Convert.instance {
      case "" => (Right(None): Either[ParseFailure, Option[Double]]).toValidatedNel
      case s => tryCatch(Some(s.toDouble))(s"Not a Double for input string: $s").toValidatedNel
    }

//    implicit def optionGeneral[A](implicit f : String => Convert[A]):Convert[Option[A]] = { s =>
//      s match {TODO
//        case "" => (Right(None): Either[ParseFailure, Option[A]]).toValidatedNel
//        case s => val p: Convert[A] =f(s)
//          p.map(r => r)
//          ???
//      }
//    }

    implicit val optionInt: Convert[Option[Int]] = Convert.instance {
      case "" => (Right(None): Either[ParseFailure, Option[Int]]).toValidatedNel
      case s => tryCatch(Some(s.toInt))(s"Not a Int for input string: $s").toValidatedNel
    }

    implicit val booleans: Convert[Boolean] = Convert.instance(Conversions.toBooleans.to(_).toValidatedNel)

    implicit val shorts: Convert[Short] = Convert.instance(Conversions.toShorts.to(_).toValidatedNel)

    implicit val bytes: Convert[Byte] = Convert.instance(Conversions.toBytes.to(_).toValidatedNel)

    implicit val double: Convert[Double] = Convert.instance(Conversions.toDoubles.to(_).toValidatedNel)

    implicit val chars: Convert[Char] = Convert.instance(Conversions.toChars.to(_).toValidatedNel)

    implicit val ints: Convert[Int] = Convert.instance(Conversions.toInts.to(_).toValidatedNel)

    implicit val strings: Convert[String] = Convert.instance(_.validNel)
  }

  sealed trait Schema[A] {
    def readFrom(input: Map[String, String]): ValidatedNel[ParseFailure, A]
  }

  object Schema {
    def of[A](implicit s: Schema[A]): Schema[A] = s

    private def instance[A](body: Map[String, String] => Result[A]): Schema[A] = new Schema[A] {
      def readFrom(input: Map[String, String]): Result[A] = body(input)
    }

    implicit val noOp: Schema[HNil] = new Schema[HNil] {
      def readFrom(input: Map[String, String]): ValidatedNel[Nothing, HNil.type] = HNil.validNel
    }

    implicit def parsing[K <: Symbol, V: Convert, T <: HList](
                                                               implicit key: Witness.Aux[K],
                                                               next: Schema[T]): Schema[FieldType[K, V] :: T] = Schema.instance { input =>
      (input.get(key.value.name).fold(ParseFailure(s"${key.value.name} is missing").invalidNel: Result[V])(entry => Convert.to[V](entry))
        .map(field[K](_)), next.readFrom(input)).mapN(_ :: _)
    }

    implicit def classes[A, R <: HList](
                                         implicit repr: LabelledGeneric.Aux[A, R],
                                         schema: Schema[R]): Schema[A] =
      Schema.instance { input =>
        schema.readFrom(input).map(x => repr.from(x))
      }
  }

}

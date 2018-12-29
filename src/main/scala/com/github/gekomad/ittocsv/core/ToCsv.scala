package com.github.gekomad.ittocsv.core

import java.util.UUID

import com.github.gekomad.ittocsv.core.Header._
import com.github.gekomad.ittocsv.core.Types.EmailOps._
import com.github.gekomad.ittocsv.core.Types.IPOps.{IP, IP6}
import com.github.gekomad.ittocsv.core.Types.MD5Ops.MD5
import com.github.gekomad.ittocsv.core.Types.SHAOps.{SHA1, SHA256}
import com.github.gekomad.ittocsv.core.Types.UrlOps.URL
import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
import shapeless.{::, Generic, HList, HNil, Lazy}

trait CsvStringEncoder[A] {
  def encode(value: A): String
}

/**
  * Converts the type A to CSV
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csb/blob/master/README.md]] for more information.
  */
object ToCsv {

  def createEncoder[A](func: A => String): CsvStringEncoder[A] = new CsvStringEncoder[A] {
    override def encode(value: A): String = func(value)
  }

  val csvConverter = StringToCsvField

  implicit def stringEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[String] = createEncoder(t => csvConverter.stringToCsvField(t))

  implicit def intEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Int] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def longEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Long] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def doubleEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Double] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def booleanEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Boolean] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def byteEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Byte] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def uuidEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[UUID] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def md5Encoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[MD5] = createEncoder(t => csvConverter.stringToCsvField(t.code))

  implicit def sha1Encoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[SHA1] = createEncoder(t => csvConverter.stringToCsvField(t.code))

  implicit def sha256Encoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[SHA256] = createEncoder(t => csvConverter.stringToCsvField(t.code))

  implicit def ipEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[IP] = createEncoder(t => csvConverter.stringToCsvField(t.code))

  implicit def ip6Encoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[IP6] = createEncoder(t => csvConverter.stringToCsvField(t.code))

  implicit def shortEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Short] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def floatEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Float] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def charEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Char] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def emailEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Email] = createEncoder(t => csvConverter.stringToCsvField(t.email))

  implicit def urlEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[URL] = createEncoder(t => csvConverter.stringToCsvField(t.url))

  /////////////////
  import java.time.LocalDateTime
  import java.time.LocalDate
  import java.time.LocalTime
  import java.time.OffsetDateTime
  import java.time.OffsetTime
  import java.time.ZonedDateTime

  implicit def localDateEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[LocalDate] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def localDateTimeEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[LocalDateTime] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def localTimeEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[LocalTime] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def offsetDateTimeEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[OffsetDateTime] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def offsetTimeEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[OffsetTime] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  implicit def zonedDateTimeEncoder(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[ZonedDateTime] = createEncoder(t => csvConverter.stringToCsvField(t.toString))

  ///////////////////////////

  implicit val hnilEncoder: CsvStringEncoder[HNil] = new CsvStringEncoder[HNil] {
    override def encode(value: HNil): String = ""
  }

  implicit def genericEncoder[A, R](implicit gen: Generic.Aux[A, R], rEncoder: Lazy[CsvStringEncoder[R]]): CsvStringEncoder[A] = createEncoder { value => rEncoder.value.encode(gen.to(value)) }

  private def header[A: FieldNames](implicit enc: CsvStringEncoder[A], csvFormat: IttoCSVFormat): String = if (csvFormat.printHeader) csvHeader[A] + csvFormat.recordSeparator else ""

  /**
    * @param a                    is the element to convert
    * @param printRecordSeparator if true, appends the record separator to end of string
    * @param enc                  the [[com.github.gekomad.ittocsv.core.CsvStringEncoder]] encoder
    * @param csvFormat            the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return the CSV string encoded
    * {{{
    * import com.github.gekomad.ittocsv.core.ToCsv._
    * implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    *
    * case class Bar(a: String, b: Int)
    * assert(toCsv(Bar("侍", 42)) == "侍,42")
    * case class Baz(x: String)
    * case class Foo(a: Int, c: Baz)
    * case class Xyz(a: String, b: Int, c: Foo)
    *
    * assert(toCsv(Xyz("hello", 3, Foo(1, Baz("hi, dude")))) == "hello,3,1,\"hi, dude\"")
    * }}}
    *
    */
  def toCsv[A](a: A, printRecordSeparator: Boolean = false)(implicit enc: CsvStringEncoder[A], csvFormat: IttoCSVFormat): String =
    (if (printRecordSeparator) csvFormat.recordSeparator else "") + enc.encode(a)

  /**
    * @param a         is the List of elements to convert
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return the CSV string encoded
    * {{{
    * import com.github.gekomad.ittocsv.core.ToCsv._
    * implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    * case class Bar(a: String, b: Int)
    * assert(toCsv(List(Bar("abc", 42), Bar("def", 24))) == "abc,42,def,24")
    * }}}
    *
    */
  def toCsv[A](a: Seq[A])(implicit enc: CsvStringEncoder[A], csvFormat: IttoCSVFormat): String =
    a.map(value => toCsv(value)).mkString(csvFormat.delimeter.toString)

  /**
    * @param a         is the List of elements to convert
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return the multiline CSV string encoded
    * {{{
    * import com.github.gekomad.ittocsv.core.ToCsv._
    * implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    * case class Bar(a: String, b: Int)
    * assert(toCsvL(List(Bar("abc", 42), Bar("def", 24))) == "a,b\r\nabc,42\r\ndef,24")
    * }}}
    *
    */
  def toCsvL[A: FieldNames](a: Seq[A])(implicit enc: CsvStringEncoder[A], csvFormat: IttoCSVFormat): String =
    header + a.map(value => toCsv(value)).mkString(csvFormat.recordSeparator)

  implicit def hlistEncoder[H, T <: HList](implicit hEncoder: CsvStringEncoder[H], tEncoder: CsvStringEncoder[T], csvFormat: IttoCSVFormat): CsvStringEncoder[H :: T] = createEncoder {
    case h :: HNil => hEncoder.encode(h)
    case h :: Nil :: HNil => hEncoder.encode(h)
    case h :: t =>
      hEncoder.encode(h) ++ csvFormat.delimeter.toString + tEncoder.encode(t)
  }

  import shapeless.{:+:, CNil, Coproduct, Inl, Inr}

  implicit val cnilEncoder: CsvStringEncoder[CNil] = createEncoder(_ => throw new Exception("Inconceivable!"))

  implicit def coproductEncoder[H, T <: Coproduct](
                                                    implicit
                                                    hEncoder: Lazy[CsvStringEncoder[H]],
                                                    tEncoder: CsvStringEncoder[T]
                                                  ): CsvStringEncoder[H :+: T] = createEncoder {
    case Inl(h) => hEncoder.value.encode(h)
    case Inr(t) => tEncoder.encode(t)
  }
}

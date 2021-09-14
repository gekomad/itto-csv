package com.github.gekomad.ittocsv.core

import com.github.gekomad.ittocsv.core.Header._
import com.github.gekomad.ittocsv.core.Types.implicits._
import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZonedDateTime
import java.util.UUID
import scala.deriving.Mirror

object ToCsv:
  trait FieldEncoder[A]:
    def encodeField(a: A)(using csvFormat: IttoCSVFormat): String

  trait RowEncoder[A]:
    def encodeRow(a: A)(using csvFormat: IttoCSVFormat): List[String]

  def customFieldEncoder[A](f: A => String) =
    new FieldEncoder[A]:
      def encodeField(x: A)(using csvFormat: IttoCSVFormat) =
        StringToCsvField.stringToCsvField(f(x))

  given FieldEncoder[Int] = customFieldEncoder[Int](_.toString)
  given FieldEncoder[Boolean] = customFieldEncoder[Boolean](x => if x then "true" else "false")
  given FieldEncoder[UUID] = customFieldEncoder[UUID](_.toString)
  given FieldEncoder[String] = customFieldEncoder[String](identity)
  given FieldEncoder[Long] = customFieldEncoder[Long](_.toString)
  given FieldEncoder[Double] = customFieldEncoder[Double](_.toString)
  given FieldEncoder[Byte] = customFieldEncoder[Byte](_.toString)
  given FieldEncoder[Short] = customFieldEncoder[Short](_.toString)
  given FieldEncoder[Float] = customFieldEncoder[Float](_.toString)
  given FieldEncoder[Char] = customFieldEncoder[Char](_.toString)
  given FieldEncoder[LocalDate] = customFieldEncoder[LocalDate](_.toString)
  given FieldEncoder[LocalDateTime] = customFieldEncoder[LocalDateTime](_.toString)
  given FieldEncoder[LocalTime] = customFieldEncoder[LocalTime](_.toString)
  given FieldEncoder[OffsetDateTime] = customFieldEncoder[OffsetDateTime](_.toString)
  given FieldEncoder[OffsetTime] = customFieldEncoder[OffsetTime](_.toString)
  given FieldEncoder[ZonedDateTime] = customFieldEncoder[ZonedDateTime](_.toString)
  given FieldEncoder[Instant] = customFieldEncoder[Instant](_.toString)
  given FieldEncoder[SHA1] = customFieldEncoder[SHA1](_.value.toString)
  given FieldEncoder[SHA256] = customFieldEncoder[SHA256](_.value.toString)
  given FieldEncoder[IP] = customFieldEncoder[IP](_.value.toString)
  given FieldEncoder[IP6] = customFieldEncoder[IP6](_.value.toString)
  given FieldEncoder[BitcoinAdd] = customFieldEncoder[BitcoinAdd](_.value.toString)
  given FieldEncoder[USphoneNumber] = customFieldEncoder[USphoneNumber](_.value.toString)
  given FieldEncoder[ItalianMobilePhone] = customFieldEncoder[ItalianMobilePhone](_.value.toString)
  given FieldEncoder[ItalianPhone] = customFieldEncoder[ItalianPhone](_.value.toString)
  given FieldEncoder[Time24] = customFieldEncoder[Time24](_.value.toString)
  given FieldEncoder[MDY] = customFieldEncoder[MDY](_.value.toString)
  given FieldEncoder[MDY2] = customFieldEncoder[MDY2](_.value.toString)
  given FieldEncoder[MDY3] = customFieldEncoder[MDY3](_.value.toString)
  given FieldEncoder[MDY4] = customFieldEncoder[MDY4](_.value.toString)
  given FieldEncoder[DMY] = customFieldEncoder[DMY](_.value.toString)
  given FieldEncoder[DMY2] = customFieldEncoder[DMY2](_.value.toString)
  given FieldEncoder[DMY3] = customFieldEncoder[DMY3](_.value.toString)
  given FieldEncoder[DMY4] = customFieldEncoder[DMY4](_.value.toString)
  given FieldEncoder[Time] = customFieldEncoder[Time](_.value.toString)
  given FieldEncoder[Cron] = customFieldEncoder[Cron](_.value.toString)
  given FieldEncoder[ItalianFiscalCode] = customFieldEncoder[ItalianFiscalCode](_.value.toString)
  given FieldEncoder[ItalianVAT] = customFieldEncoder[ItalianVAT](_.value.toString)
  given FieldEncoder[ItalianIban] = customFieldEncoder[ItalianIban](_.value.toString)
  given FieldEncoder[USstates] = customFieldEncoder[USstates](_.value.toString)
  given FieldEncoder[USstates1] = customFieldEncoder[USstates1](_.value.toString)
  given FieldEncoder[USZipCode] = customFieldEncoder[USZipCode](_.value.toString)
  given FieldEncoder[ItalianZipCode] = customFieldEncoder[ItalianZipCode](_.value.toString)
  given FieldEncoder[USstreets] = customFieldEncoder[USstreets](_.value.toString)
  given FieldEncoder[USstreetNumber] = customFieldEncoder[USstreetNumber](_.value.toString)
  given FieldEncoder[GermanStreet] = customFieldEncoder[GermanStreet](_.value.toString)
  given FieldEncoder[UsdCurrency] = customFieldEncoder[UsdCurrency](_.value.toString)
  given FieldEncoder[EurCurrency] = customFieldEncoder[EurCurrency](_.value.toString)
  given FieldEncoder[YenCurrency] = customFieldEncoder[YenCurrency](_.value.toString)
  given FieldEncoder[NotASCII] = customFieldEncoder[NotASCII](_.value.toString)
  given FieldEncoder[SingleChar] = customFieldEncoder[SingleChar](_.value.toString)
  given FieldEncoder[AZString] = customFieldEncoder[AZString](_.value.toString)
  given FieldEncoder[AsciiString] = customFieldEncoder[AsciiString](_.value.toString)
  given FieldEncoder[StringAndNumber] = customFieldEncoder[StringAndNumber](_.value.toString)
  given FieldEncoder[ApacheError] = customFieldEncoder[ApacheError](_.value.toString)
  given FieldEncoder[Number1] = customFieldEncoder[Number1](_.value.toString)
  given FieldEncoder[Unsigned32] = customFieldEncoder[Unsigned32](_.value.toString)
  given FieldEncoder[Signed] = customFieldEncoder[Signed](_.value.toString)
  given FieldEncoder[Percentage] = customFieldEncoder[Percentage](_.value.toString)
  given FieldEncoder[Scientific] = customFieldEncoder[Scientific](_.value.toString)
  given FieldEncoder[SingleNumber] = customFieldEncoder[SingleNumber](_.value.toString)
  given FieldEncoder[Celsius] = customFieldEncoder[Celsius](_.value.toString)
  given FieldEncoder[Fahrenheit] = customFieldEncoder[Fahrenheit](_.value.toString)
  given FieldEncoder[Coordinate] = customFieldEncoder[Coordinate](_.value.toString)
  given FieldEncoder[Coordinate1] = customFieldEncoder[Coordinate1](_.value.toString)
  given FieldEncoder[Coordinate2] = customFieldEncoder[Coordinate2](_.value.toString)
  given FieldEncoder[Youtube] = customFieldEncoder[Youtube](_.value.toString)
  given FieldEncoder[Facebook] = customFieldEncoder[Facebook](_.value.toString)
  given FieldEncoder[Twitter] = customFieldEncoder[Twitter](_.value.toString)
  given FieldEncoder[MACAddress] = customFieldEncoder[MACAddress](_.value.toString)
  given FieldEncoder[Email1] = customFieldEncoder[Email1](_.value.toString)
  given FieldEncoder[Email] = customFieldEncoder[Email](_.value.toString)
  given FieldEncoder[EmailSimple] = customFieldEncoder[EmailSimple](_.value.toString)
  given FieldEncoder[HEX] = customFieldEncoder[HEX](_.value.toString)
  given FieldEncoder[HEX1] = customFieldEncoder[HEX1](_.value.toString)
  given FieldEncoder[HEX2] = customFieldEncoder[HEX2](_.value.toString)
  given FieldEncoder[HEX3] = customFieldEncoder[HEX3](_.value.toString)
  given FieldEncoder[URL] = customFieldEncoder[URL](_.value.toString)
  given FieldEncoder[URL1] = customFieldEncoder[URL1](_.value.toString)
  given FieldEncoder[URL2] = customFieldEncoder[URL2](_.value.toString)
  given FieldEncoder[URL3] = customFieldEncoder[URL3](_.value.toString)
  given FieldEncoder[FTP] = customFieldEncoder[FTP](_.value.toString)
  given FieldEncoder[FTP1] = customFieldEncoder[FTP1](_.value.toString)
  given FieldEncoder[FTP2] = customFieldEncoder[FTP2](_.value.toString)
  given FieldEncoder[Domain] = customFieldEncoder[Domain](_.value.toString)
  given FieldEncoder[MD5] = customFieldEncoder[MD5](_.value.toString)

  given [A](using enc: FieldEncoder[A]): FieldEncoder[Option[A]] =
    new FieldEncoder[Option[A]]:
      def encodeField(x: Option[A])(using csvFormat: IttoCSVFormat) =
        x match
          case Some(xx) => enc.encodeField(xx)
          case _        => StringToCsvField.stringToCsvField("")

  given [A](using enc: FieldEncoder[A]): FieldEncoder[List[A]] =
    new FieldEncoder[List[A]]:
      def encodeField(x: List[A])(using csvFormat: IttoCSVFormat) =
        x match
          case x :: Nil => enc.encodeField(x)
          case x :: xs  => enc.encodeField(x) + csvFormat.recordSeparator + encodeField(xs)
          case _        => StringToCsvField.stringToCsvField("")

  given RowEncoder[EmptyTuple] with
    def encodeRow(empty: EmptyTuple)(using csvFormat: IttoCSVFormat) = List.empty

  given [H: FieldEncoder, T <: Tuple: RowEncoder]: RowEncoder[H *: T] with
    def encodeRow(tuple: H *: T)(using csvFormat: IttoCSVFormat) =
      summon[FieldEncoder[H]].encodeField(tuple.head) :: summon[RowEncoder[T]].encodeRow(tuple.tail)

  private def tupleToCsv[A <: Tuple: RowEncoder](tuple: A)(using csvFormat: IttoCSVFormat): List[String] =
    summon[RowEncoder[A]].encodeRow(tuple)

  inline def header[A](using mirror: Mirror.Of[A], csvFormat: IttoCSVFormat): String =
    if (csvFormat.printHeader) csvHeader[A] + csvFormat.recordSeparator else ""

  /**
   * @param a
   *   is the element to convert
   * @param printRecordSeparator
   *   if true, appends the record separator to end of string
   * @param csvFormat
   *   the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
   * @return
   *   the CSV string encoded
   */
  def toCsv[A <: Product](
    a: A,
    printRecordSeparator: Boolean = false
  )(using m: scala.deriving.Mirror.ProductOf[A], e: RowEncoder[m.MirroredElemTypes], csvFormat: IttoCSVFormat): String =
    (if (printRecordSeparator) csvFormat.recordSeparator else "") + toCsv(a)

  inline def toCsvL[A <: Product](a: Seq[A])(using
    m: scala.deriving.Mirror.ProductOf[A],
    e: RowEncoder[m.MirroredElemTypes],
    csvFormat: IttoCSVFormat
  ): String = header + a.map(toCsv(_)).mkString(csvFormat.recordSeparator)

  def toCsv[A <: Product](a: Seq[A])(using
    m: scala.deriving.Mirror.ProductOf[A],
    e: RowEncoder[m.MirroredElemTypes],
    csvFormat: IttoCSVFormat
  ): String = a.map(toCsv(_)).mkString(csvFormat.delimeter.toString)

  def toCsv[A](a: Seq[A])(using
    enc: FieldEncoder[A],
    csvFormat: IttoCSVFormat
  ): String = a.map(toCsv(_)).mkString(csvFormat.delimeter.toString)

  def toCsv[A <: Product](t: A)(using
    m: scala.deriving.Mirror.ProductOf[A],
    e: RowEncoder[m.MirroredElemTypes],
    csvFormat: IttoCSVFormat
  ): String = e.encodeRow(Tuple.fromProductTyped(t)).mkString(csvFormat.delimeter.toString)

  def toCsv[A](t: A)(using
    enc: FieldEncoder[A],
    csvFormat: IttoCSVFormat
  ): String = enc.encodeField(t)

  def toCsvFlat[A <: Product](a: A)(using m: scala.deriving.Mirror.ProductOf[A], csvFormat: IttoCSVFormat) = {

    def flatTuple(any: Any): Tuple = any match
      case p: Product => p.productIterator.map(flatTuple).foldLeft(EmptyTuple: Tuple)(_ ++ _)
      case a          => Tuple1(a)

    val tuple = flatTuple(Tuple.fromProductTyped(a)).toList
    tuple.map(a => StringToCsvField.stringToCsvField(a.toString)).mkString(csvFormat.delimeter.toString)
  }
  
end ToCsv

package com.github.gekomad.ittocsv.core

import java.util.UUID
import com.github.gekomad.ittocsv.util.TryCatch.tryCatch

/**
  * Converts a string to type
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information.
  */
object Conversions {

  trait ConvertTo[A] {
    def to(a: String): Either[ParseFailure, A]
  }

  implicit val toInts: ConvertTo[Int] = new ConvertTo[Int] {
    def to(a: String): Either[ParseFailure, Int] =
      tryCatch(a.toInt)(s"$a is not Int")
  }

  implicit val toDoubles: ConvertTo[Double] = new ConvertTo[Double] {
    def to(a: String): Either[ParseFailure, Double] =
      tryCatch(a.toDouble)(s"$a is not Double")
  }

  implicit val toBytes: ConvertTo[Byte] = new ConvertTo[Byte] {
    def to(a: String): Either[ParseFailure, Byte] =
      tryCatch(a.toByte)(s"$a is not Byte")
  }

  implicit val toShorts: ConvertTo[Short] = new ConvertTo[Short] {
    def to(a: String): Either[ParseFailure, Short] =
      tryCatch(a.toShort)(s"$a is not Short")
  }

  implicit val toFloats: ConvertTo[Float] = new ConvertTo[Float] {
    def to(a: String): Either[ParseFailure, Float] =
      tryCatch(a.toFloat)(s"$a is not Float")
  }

  implicit val toLongs: ConvertTo[Long] = new ConvertTo[Long] {
    def to(a: String): Either[ParseFailure, Long] =
      tryCatch(a.toLong)(s"$a is not Long")
  }

  implicit val toChars: ConvertTo[Char] = new ConvertTo[Char] {
    def to(a: String): Either[ParseFailure, Char] =
      tryCatch(if (a.length == 1) a(0) else throw new Exception)(s"$a is not Char")
  }

  implicit val toBooleans: ConvertTo[Boolean] = new ConvertTo[Boolean] {
    def to(a: String): Either[ParseFailure, Boolean] =
      tryCatch(a.toBoolean)(s"$a is not Boolean")
  }

  implicit val toUUIDS: ConvertTo[UUID] = new ConvertTo[UUID] {
    def to(a: String): Either[ParseFailure, UUID] =
      tryCatch(UUID.fromString(a))(s"$a is not UUID")
  }

  import java.time._
  import java.time.format.DateTimeFormatter._

  implicit val fromStringToLocalDateTime: String => Either[ParseFailure, LocalDateTime] = { s =>
    tryCatch(LocalDateTime.parse(s, ISO_LOCAL_DATE_TIME))(s"Not a LocalDataTime $s")
  }

  implicit def fromGenericOption[A](implicit f: String => Either[ParseFailure, A]): String => Either[ParseFailure, Option[A]] = {
    import cats.implicits._
    s =>
      if (s == "") Right(None)
      else {
        val c: Either[ParseFailure, A] = f(s)
        c.map(r => Some(r))
      }
  }

  implicit val fromStringToLocalDate: ConvertTo[LocalDate] =
    new ConvertTo[LocalDate] {
      def to(a: String): Either[ParseFailure, LocalDate] =
        tryCatch(LocalDate.parse(a, ISO_LOCAL_DATE))(s"Not a LocalDate $a")
    }

  implicit val fromStringToLocalTime: ConvertTo[LocalTime] =
    new ConvertTo[LocalTime] {
      def to(a: String): Either[ParseFailure, LocalTime] =
        tryCatch(LocalTime.parse(a, ISO_LOCAL_TIME))(s"Not a LocalTime $a")
    }

  implicit val fromStringToOffsetDateTime: ConvertTo[OffsetDateTime] =
    new ConvertTo[OffsetDateTime] {
      def to(s: String): Either[ParseFailure, OffsetDateTime] =
        tryCatch(OffsetDateTime.parse(s, ISO_OFFSET_DATE_TIME))(s"Not a OffsetDateTime $s")
    }

  implicit val fromStringToOffsetTime: ConvertTo[OffsetTime] =
    new ConvertTo[OffsetTime] {
      def to(s: String): Either[ParseFailure, OffsetTime] =
        tryCatch(OffsetTime.parse(s, ISO_OFFSET_TIME))(s"Not a OffsetTime $s")
    }

  implicit val fromStringToZonedDateTime: ConvertTo[ZonedDateTime] =
    new ConvertTo[ZonedDateTime] {
      def to(s: String): Either[ParseFailure, ZonedDateTime] =
        tryCatch(ZonedDateTime.parse(s, ISO_ZONED_DATE_TIME))(s"Not a ZonedDateTime $s")
    }

  def convert[A](s: String)(implicit f: ConvertTo[A]): Either[ParseFailure, A] =
    f.to(s)
}

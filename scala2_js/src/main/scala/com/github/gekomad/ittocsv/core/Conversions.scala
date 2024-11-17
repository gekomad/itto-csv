package com.github.gekomad.ittocsv.core

import java.util.UUID
import com.github.gekomad.ittocsv.util.TryTo.tryToEither

/** Converts a string to type
  *
  * @author
  *   Giuseppe Cannella
  * @since 0.0.1
  * @see
  *   See test code for more information
  * @see
  *   See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information.
  */
object Conversions {

  trait ConvertTo[A] {
    def to(a: String): Either[ParseFailure, A]
  }

  implicit val toInts: ConvertTo[Int] = (a: String) => tryToEither(a.toInt)(ParseFailure(s"$a is not Int"))

  implicit val toDoubles: ConvertTo[Double] = (a: String) => tryToEither(a.toDouble)(ParseFailure(s"$a is not Double"))

  implicit val toBytes: ConvertTo[Byte] = (a: String) => tryToEither(a.toByte)(ParseFailure(s"$a is not Byte"))

  implicit val toShorts: ConvertTo[Short] = (a: String) => tryToEither(a.toShort)(ParseFailure(s"$a is not Short"))

  implicit val toFloats: ConvertTo[Float] = (a: String) => tryToEither(a.toFloat)(ParseFailure(s"$a is not Float"))

  implicit val toLongs: ConvertTo[Long] = (a: String) => tryToEither(a.toLong)(ParseFailure(s"$a is not Long"))

  implicit val toChars: ConvertTo[Char] = (a: String) =>
    tryToEither(if (a.length == 1) a(0) else throw new Exception)(ParseFailure(s"$a is not Char"))

  implicit val toBooleans: ConvertTo[Boolean] = (a: String) =>
    tryToEither(a.toBoolean)(ParseFailure(s"$a is not Boolean"))

  implicit val toUUIDS: ConvertTo[UUID] = (a: String) =>
    tryToEither(UUID.fromString(a))(ParseFailure(s"$a is not UUID"))

  import java.time._
  import java.time.format.DateTimeFormatter._

  implicit def fromGenericOption[A](implicit
    f: String => Either[ParseFailure, A]
  ): String => Either[ParseFailure, Option[A]] =
    s => if (s == "") Right(None) else f(s).map(Some(_))

  implicit val fromStringToLocalDateTime: String => Either[ParseFailure, LocalDateTime] = { s =>
    tryToEither(LocalDateTime.parse(s, ISO_LOCAL_DATE_TIME))(ParseFailure(s"Not a LocalDataTime $s"))
  }

  implicit val fromStringToLocalDate: String => Either[ParseFailure, LocalDate] = { s =>
    tryToEither(LocalDate.parse(s, ISO_LOCAL_DATE))(ParseFailure(s"Not a LocalDate $s"))
  }

  implicit val fromStringToLocalTime: String => Either[ParseFailure, LocalTime] =
    (s: String) => tryToEither(LocalTime.parse(s, ISO_LOCAL_TIME))(ParseFailure(s"Not a LocalTime $s"))

  implicit val fromStringToOffsetDateTime: String => Either[ParseFailure, OffsetDateTime] =
    (s: String) => tryToEither(OffsetDateTime.parse(s, ISO_OFFSET_DATE_TIME))(ParseFailure(s"Not a OffsetDateTime $s"))

  implicit val fromStringToOffsetTime: String => Either[ParseFailure, OffsetTime] =
    (s: String) => tryToEither(OffsetTime.parse(s, ISO_OFFSET_TIME))(ParseFailure(s"Not a OffsetTime $s"))

  implicit val fromStringToZonedDateTime: String => Either[ParseFailure, ZonedDateTime] =
    (s: String) => tryToEither(ZonedDateTime.parse(s, ISO_ZONED_DATE_TIME))(ParseFailure(s"Not a ZonedDateTime $s"))

  implicit val fromStringInstant: String => Either[ParseFailure, Instant] =
    (s: String) => tryToEither(Instant.parse(s))(ParseFailure(s"Not a Instant $s"))

  def convert[A](s: String)(implicit f: ConvertTo[A]): Either[ParseFailure, A] = f.to(s)
}

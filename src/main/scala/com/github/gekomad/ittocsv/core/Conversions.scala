package com.github.gekomad.ittocsv.core

import java.util.UUID
import com.github.gekomad.ittocsv.core.Types.EmailOps._
import com.github.gekomad.ittocsv.core.Types.IPOps.{IP, IP6}
import com.github.gekomad.ittocsv.core.Types.MD5Ops.MD5
import com.github.gekomad.ittocsv.core.Types.SHAOps.{SHA1, SHA256}
import com.github.gekomad.ittocsv.core.Types.UrlOps.{URL, UrlValidator}
import com.github.gekomad.ittocsv.util.TryCatch.tryCatch
import com.github.gekomad.regexcollection.Validate._

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

  implicit def toEmails(implicit emailValidator: EmailValidator): ConvertTo[Email] =
    new ConvertTo[Email] {
      def to(a: String): Either[ParseFailure, Email] =
        emailValidator.validate(a)
    }

  implicit def toUrls(implicit urlValidator: UrlValidator): ConvertTo[URL] =
    new ConvertTo[URL] {
      def to(a: String): Either[ParseFailure, URL] = urlValidator.validate(a)
    }

  implicit val toMD5s: ConvertTo[MD5] = new ConvertTo[MD5] {
    def to(a: String): Either[ParseFailure, MD5] =
      tryCatch {
        validate[com.github.gekomad.regexcollection.MD5](a).map(_ => MD5(a)) getOrElse (throw new Exception)
      }(s"$a is not MD5")
  }

  implicit val toIPs: ConvertTo[IP] = new ConvertTo[IP] {
    def to(a: String): Either[ParseFailure, IP] =
      tryCatch {
        validate[com.github.gekomad.regexcollection.IP](a).map(_ => IP(a)) getOrElse (throw new Exception)
      }(s"$a is not IP")
  }

  implicit val toIP6s: ConvertTo[IP6] = new ConvertTo[IP6] {
    def to(a: String): Either[ParseFailure, IP6] =
      tryCatch {
        validate[com.github.gekomad.regexcollection.IP_6](a).map(_ => IP6(a)) getOrElse (throw new Exception)
      }(s"$a is not IP6")
  }

  implicit val toSHA1s: ConvertTo[SHA1] = new ConvertTo[SHA1] {
    def to(a: String): Either[ParseFailure, SHA1] =
      tryCatch {
        validate[com.github.gekomad.regexcollection.SHA1](a).map(_ => SHA1(a)) getOrElse (throw new Exception)
      }(s"$a is not SHA1")
  }

  implicit val toSHA256s: ConvertTo[SHA256] = new ConvertTo[SHA256] {
    def to(a: String): Either[ParseFailure, SHA256] =
      tryCatch {
        validate[com.github.gekomad.regexcollection.SHA256](a).map(_ => SHA256(a)) getOrElse (throw new Exception)
      }(s"$a is not SHA256")
  }

  implicit val toUUIDS: ConvertTo[UUID] = new ConvertTo[UUID] {
    def to(a: String): Either[ParseFailure, UUID] =
      tryCatch(UUID.fromString(a))(s"$a is not UUID")
  }

  import java.time.LocalDateTime
  import java.time.LocalDate
  import java.time.LocalTime
  import java.time.OffsetDateTime
  import java.time.OffsetTime
  import java.time.ZonedDateTime

  import java.time.format.DateTimeFormatter.{ISO_LOCAL_DATE, ISO_LOCAL_DATE_TIME, ISO_LOCAL_TIME, ISO_OFFSET_DATE_TIME, ISO_OFFSET_TIME, ISO_ZONED_DATE_TIME}

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

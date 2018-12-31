package com.github.gekomad.ittocsv.core

import java.util.UUID

import com.github.gekomad.ittocsv.core.Conversions.ConvertTo
import com.github.gekomad.ittocsv.core.Types.EmailOps._
import com.github.gekomad.ittocsv.core.Types.MD5Ops.MD5
import com.github.gekomad.ittocsv.core.Types.SHAOps.{SHA1, SHA256}
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import cats._
import com.github.gekomad.ittocsv.core.Types.IPOps.{IP, IP6}
import com.github.gekomad.ittocsv.core.Types.UrlOps.{URL, UrlValidator}
import com.github.gekomad.ittocsv.util.TryCatch.tryCatch
import implicits._
import data.ValidatedNel

import scala.util.Try

trait Convert[V] {
  def parse(input: String): ValidatedNel[ParseFailure, V]
}

object Convert {

  def to[V](input: String)(implicit C: Convert[V]): ValidatedNel[ParseFailure, V] =
    C.parse(input)

  def instance[V](body: String => ValidatedNel[ParseFailure, V]): Convert[V] = new Convert[V] {
    def parse(input: String): ValidatedNel[ParseFailure, V] = body(input)
  }

  implicit def optionLists[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[Option[List[A]]] =
    Convert.instance { s =>
      Try {
        val x: List[A] = s.split(csvFormat.delimeter.toString, -1).toList.map {
          com.github.gekomad.ittocsv.core.Conversions.convert[A](_).getOrElse(throw new Exception)
        }
        Right(Some(x))
      }.getOrElse {
        Left(ParseFailure(s"Not a List[type] $s"))
      }.toValidatedNel
    }

  implicit def urls(implicit csvFormat: IttoCSVFormat, urlValidator: UrlValidator): Convert[URL] =
    Convert.instance(urlValidator.validate(_).toValidatedNel)

  implicit def emails(implicit csvFormat: IttoCSVFormat, emailValidator: EmailValidator): Convert[Email] =
    Convert.instance(emailValidator.validate(_).toValidatedNel)

  implicit def lists[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[List[A]] =
    Convert.instance { s =>
      Try {
        val x: List[A] = s
          .split(csvFormat.delimeter.toString, -1)
          .toList
          .map(com.github.gekomad.ittocsv.core.Conversions.convert[A](_).getOrElse(throw new Exception))
        Right(x): Either[ParseFailure, List[A]]
      }.getOrElse {
        Left(ParseFailure(s"Not a List[type] $s")): Either[ParseFailure, List[A]]
      }.toValidatedNel

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

  implicit def generic[A](implicit f: String => Either[ParseFailure, A]): Convert[A] =
    Convert.instance(f(_).toValidatedNel)

  implicit val optionBoolean: Convert[Option[Boolean]] =
    Convert.instance {
      case "" => (Right(None): Either[ParseFailure, Option[Boolean]]).toValidatedNel
      case s  => tryCatch(Some(s.toBoolean))(s"Not a Boolean for input string: $s").toValidatedNel
    }

  implicit val optionShort: Convert[Option[Short]] =
    Convert.instance {
      case "" => (Right(None): Either[ParseFailure, Option[Short]]).toValidatedNel
      case s  => tryCatch(Some(s.toShort))(s"Not a Short for input string: $s").toValidatedNel
    }

  implicit val optionByte: Convert[Option[Byte]] =
    Convert.instance {
      case "" => (Right(None): Either[ParseFailure, Option[Byte]]).toValidatedNel
      case s  => tryCatch(Some(s.toByte))(s"Not a Byte for input string: $s").toValidatedNel
    }

  implicit val optionChar: Convert[Option[Char]] =
    Convert.instance {
      case "" => (Right(None): Either[ParseFailure, Option[Char]]).toValidatedNel
      case s =>
        tryCatch(if (s.length == 1) Some(s(0)) else throw new Exception)(s"Not a Char for input string: $s").toValidatedNel
    }

  implicit val optionString: Convert[Option[String]] = Convert.instance {
    case "" => (Right(None): Either[ParseFailure, Option[String]]).toValidatedNel
    case s  => (Right(Some(s)): Either[ParseFailure, Option[String]]).toValidatedNel
  }

  implicit val optionDouble: Convert[Option[Double]] = Convert.instance {
    case "" => (Right(None): Either[ParseFailure, Option[Double]]).toValidatedNel
    case s  => tryCatch(Some(s.toDouble))(s"Not a Double for input string: $s").toValidatedNel
  }

  implicit val optionInt: Convert[Option[Int]] = Convert.instance {
    case "" => (Right(None): Either[ParseFailure, Option[Int]]).toValidatedNel
    case s  => tryCatch(Some(s.toInt))(s"Not a Int for input string: $s").toValidatedNel
  }

  implicit val booleans: Convert[Boolean] = Convert.instance(Conversions.toBooleans.to(_).toValidatedNel)

  implicit val shorts: Convert[Short] = Convert.instance(Conversions.toShorts.to(_).toValidatedNel)

  implicit val bytes: Convert[Byte] = Convert.instance(Conversions.toBytes.to(_).toValidatedNel)

  implicit val double: Convert[Double] = Convert.instance(Conversions.toDoubles.to(_).toValidatedNel)

  implicit val chars: Convert[Char] = Convert.instance(Conversions.toChars.to(_).toValidatedNel)

  implicit val ints: Convert[Int] = Convert.instance(Conversions.toInts.to(_).toValidatedNel)

  implicit val strings: Convert[String] = Convert.instance(_.validNel)
}

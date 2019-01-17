package com.github.gekomad.ittocsv.core

import cats.data.ValidatedNel
import cats.implicits._
import com.github.gekomad.ittocsv.core.Conversions.ConvertTo
import com.github.gekomad.ittocsv.core.Types.Validate
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import com.github.gekomad.ittocsv.util.TryCatch.tryCatch
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

  implicit def genericValidator[A](implicit csvFormat: IttoCSVFormat, validator: Validate[A]): Convert[A] =
    Convert.instance(validator.validate(_).toValidatedNel)

  implicit def generic[A](implicit f: String => Either[ParseFailure, A]): Convert[A] =
    Convert.instance(f(_).toValidatedNel)

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

  implicit def gen[A](implicit conv: ConvertTo[A]): Convert[A] = Convert.instance(conv.to(_).toValidatedNel)

  implicit val strings: Convert[String] = Convert.instance(_.validNel)
}

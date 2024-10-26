package com.github.gekomad.ittocsv.core

import cats.{Applicative, Id}
import cats.data.{Validated, ValidatedNel}
import com.github.gekomad.ittocsv.core.Conversions.ConvertTo
import com.github.gekomad.ittocsv.core.Types.Validate
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import com.github.gekomad.ittocsv.util.TryTo._
import com.github.gekomad.ittocsv.core.Conversions.convert
trait Convert[V] {
  def parse(input: String): ValidatedNel[ParseFailure, V]
}
import scala.language.higherKinds
object Convert {
  import cats.implicits._

  def to[V](input: String)(implicit C: Convert[V]): ValidatedNel[ParseFailure, V] = C.parse(input)

  def instance[V](body: String => ValidatedNel[ParseFailure, V]): Convert[V] = (input: String) => body(input)

  def f[A: ConvertTo, F[_]: Applicative](implicit csvFormat: IttoCSVFormat): Convert[F[List[A]]] =
    Convert.instance(
      _.split(csvFormat.delimeter.toString, -1).toList
        .map(s => tryToValidate(convert[A](s).getOrElse(throw new Exception))(ParseFailure(s"Bad type on $s")))
        .sequence
        .map(Applicative[F].pure(_))
    )

  implicit def optionLists[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[Option[List[A]]] = f[A, Option]

  implicit def lists[A: ConvertTo](implicit csvFormat: IttoCSVFormat): Convert[List[A]] = f[A, Id]

  implicit def genericValidator[A](implicit csvFormat: IttoCSVFormat, validator: Validate[A]): Convert[A] =
    Convert.instance(validator.validate(_).toValidatedNel)

  implicit def generic[A](implicit f: String => Either[ParseFailure, A]): Convert[A] =
    Convert.instance(f(_).toValidatedNel)

  implicit val optionBoolean: Convert[Option[Boolean]] = Convert.instance {
    case "" => Validated.valid(None)
    case s  => tryToValidate(Some(s.toBoolean))(ParseFailure(s"Not a Boolean for input string: $s"))
  }

  implicit val optionShort: Convert[Option[Short]] = Convert.instance {
    case "" => Validated.valid(None)
    case s  => tryToValidate(Some(s.toShort))(ParseFailure(s"Not a Short for input string: $s"))
  }

  implicit val optionByte: Convert[Option[Byte]] = Convert.instance {
    case "" => Validated.valid(None)
    case s  => tryToValidate(Some(s.toByte))(ParseFailure(s"Not a Byte for input string: $s"))
  }

  implicit val optionChar: Convert[Option[Char]] = Convert.instance {
    case "" => Validated.valid(None)
    case s =>
      tryToValidate(if (s.length == 1) Some(s(0)) else throw new Exception)(
        ParseFailure(s"Not a Char for input string: $s")
      )
  }

  implicit val optionString: Convert[Option[String]] = Convert.instance {
    case "" => Validated.valid(None)
    case s  => Validated.valid(Some(s))
  }

  implicit val optionDouble: Convert[Option[Double]] = Convert.instance {
    case "" => Validated.valid(None)
    case s  => tryToValidate(Some(s.toDouble))(ParseFailure(s"Not a Double for input string: $s"))
  }

  implicit val optionInt: Convert[Option[Int]] = Convert.instance {
    case "" => Validated.valid(None)
    case s  => tryToValidate(Some(s.toInt))(ParseFailure(s"Not a Int for input string: $s"))
  }

  implicit def gen[A](implicit conv: ConvertTo[A]): Convert[A] = Convert.instance(conv.to(_).toValidatedNel)

  implicit val strings: Convert[String] = Convert.instance(_.validNel)
}

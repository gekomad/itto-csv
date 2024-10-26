package com.github.gekomad.ittocsv.util
import cats.data.{NonEmptyList, Validated}
import scala.util.{Success, Try}

object TryTo {
  def tryToEither[A, B](a: => A)(b: B): Either[B, A] = Try(a) match {
    case Success(value) => Right(value)
    case _              => Left(b)
  }

  def tryToValidate[A, B](a: => A)(b: B): Validated[NonEmptyList[B], A] = Try(a) match {
    case Success(value) => Validated.valid(value)
    case _              => Validated.invalidNel(b)
  }

}

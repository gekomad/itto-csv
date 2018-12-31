package com.github.gekomad.ittocsv.util

import com.github.gekomad.ittocsv.core.ParseFailure

import scala.util.Try

object TryCatch {
  def tryCatch[A](a: => A)(b: String): Either[ParseFailure, A] = Try(a).map(Right(_)).getOrElse(Left(ParseFailure(b)))

}

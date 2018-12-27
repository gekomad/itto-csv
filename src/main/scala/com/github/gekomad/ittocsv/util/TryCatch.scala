package com.github.gekomad.ittocsv.util

import com.github.gekomad.ittocsv.core.FromCsv.ParseFailure

object TryCatch {
  def tryCatch[A](a: => A)(b: String): Either[ParseFailure, A] = try {
    Right(a)
  } catch {
    case _: Throwable => Left(ParseFailure(b))
  }

}

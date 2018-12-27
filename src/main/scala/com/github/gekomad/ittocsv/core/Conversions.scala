package com.github.gekomad.ittocsv.core

import java.util.UUID

import com.github.gekomad.ittocsv.core.FromCsv.ParseFailure
import com.github.gekomad.ittocsv.core.Types.EmailOps._
import com.github.gekomad.ittocsv.core.Types.IPOps.{IP, IP6}
import com.github.gekomad.ittocsv.core.Types.MD5Ops.MD5
import com.github.gekomad.ittocsv.core.Types.SHAOps.{SHA1, SHA256}
import com.github.gekomad.ittocsv.core.Types.UrlOps.{URL, UrlValidator}
import com.github.gekomad.ittocsv.util.TryCatch.tryCatch

/**
  * Converts a string to type
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csb/blob/master/README.md]] for more information.
  */

object Conversions {

  trait ConvertTo[A] {
    def to(a: String): Either[ParseFailure, A]
  }

  implicit val toInts: ConvertTo[Int] = (a: String) => tryCatch(a.toInt)(s"$a is not Int")

  implicit val toDoubles: ConvertTo[Double] = (a: String) => tryCatch(a.toDouble)(s"$a is not Double")

  implicit val toBytes: ConvertTo[Byte] = (a: String) => tryCatch(a.toByte)(s"$a is not Byte")

  implicit val toShorts: ConvertTo[Short] = (a: String) => tryCatch(a.toShort)(s"$a is not Short")

  implicit val toFloats: ConvertTo[Float] = (a: String) => tryCatch(a.toFloat)(s"$a is not Float")

  implicit val toLongs: ConvertTo[Long] = (a: String) => tryCatch(a.toLong)(s"$a is not Long")

  implicit val toChars: ConvertTo[Char] = (a: String) => tryCatch(if (a.length == 1) a(0) else throw new Exception)(s"$a is not Char")

  implicit val toBooleans: ConvertTo[Boolean] = (a: String) => tryCatch(a.toBoolean)(s"$a is not Boolean")

  implicit def toEmails(implicit emailValidator: EmailValidator): ConvertTo[Email] = (a: String) => emailValidator.validate(a)

  implicit def toUrls(implicit urlValidator: UrlValidator): ConvertTo[URL] = (a: String) => urlValidator.validate(a)

  implicit val toMD5s: ConvertTo[MD5] = (a: String) => tryCatch {
    val reg = "^[a-fA-F0-9]{32}$".r
    if (reg.findFirstMatchIn(a).isDefined) MD5(a) else throw new Exception
  }(s"$a is not MD5")

  implicit val toIPs: ConvertTo[IP] = (a: String) => tryCatch {
    val reg = "^(?:(?:2(?:[0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\\.){3}(?:(?:2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9]))$".r
    if (reg.findFirstMatchIn(a).isDefined) IP(a) else throw new Exception
  }(s"$a is not IP")

  implicit val toIP6s: ConvertTo[IP6] = (a: String) => tryCatch {
    val reg = "^(([a-fA-F0-9]{1,4}|):){1,7}([a-fA-F0-9]{1,4}|:)$".r
    if (reg.findFirstMatchIn(a).isDefined) IP6(a) else throw new Exception
  }(s"$a is not IP6")

  implicit val toSHA1s: ConvertTo[SHA1] = (a: String) => tryCatch {
    val reg = "^[a-fA-F0-9]{40}$".r
    if (reg.findFirstMatchIn(a).isDefined) SHA1(a) else throw new Exception
  }(s"$a is not SHA1")

  implicit val toSHA256s: ConvertTo[SHA256] = (a: String) => tryCatch {
    val reg = "^[A-Fa-f0-9]{64}$".r
    if (reg.findFirstMatchIn(a).isDefined) SHA256(a) else throw new Exception
  }(s"$a is not SHA256")

  implicit val toUUIDS: ConvertTo[UUID] = (a: String) => tryCatch(UUID.fromString(a))(s"$a is not UUID")

  def convert[A](s: String)(implicit f: ConvertTo[A]): Either[ParseFailure, A] = f.to(s)
}
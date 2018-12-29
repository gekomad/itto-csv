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

  implicit val toInts: ConvertTo[Int] = new ConvertTo[Int] {
    def to(a: String): Either[ParseFailure, Int]= tryCatch(a.toInt)(s"$a is not Int")
  }

  implicit val toDoubles: ConvertTo[Double] = new ConvertTo[Double] {
    def to(a: String): Either[ParseFailure, Double]= tryCatch(a.toDouble)(s"$a is not Double")
  }

  implicit val toBytes: ConvertTo[Byte] =  new ConvertTo[Byte] {
    def to(a: String): Either[ParseFailure, Byte]= tryCatch(a.toByte)(s"$a is not Byte")
  }

  implicit val toShorts: ConvertTo[Short] =new ConvertTo[Short] {
    def to(a: String): Either[ParseFailure, Short]= tryCatch(a.toShort)(s"$a is not Short")
  }

  implicit val toFloats: ConvertTo[Float] =new ConvertTo[Float] {
    def to(a: String): Either[ParseFailure, Float]= tryCatch(a.toFloat)(s"$a is not Float")
  }

  implicit val toLongs: ConvertTo[Long] =new ConvertTo[Long] {
    def to(a: String): Either[ParseFailure, Long]= tryCatch(a.toLong)(s"$a is not Long")
  }

  implicit val toChars: ConvertTo[Char] = new ConvertTo[Char] {
    def to(a: String): Either[ParseFailure, Char]= tryCatch(if (a.length == 1) a(0) else throw new Exception)(s"$a is not Char")
  }

  implicit val toBooleans: ConvertTo[Boolean] = new ConvertTo[Boolean] {
    def to(a: String): Either[ParseFailure, Boolean]= tryCatch(a.toBoolean)(s"$a is not Boolean")
  }

  implicit def toEmails(implicit emailValidator: EmailValidator): ConvertTo[Email] = new ConvertTo[Email] {
    def to(a: String): Either[ParseFailure, Email]= emailValidator.validate(a)
  }

  implicit def toUrls(implicit urlValidator: UrlValidator): ConvertTo[URL] = new ConvertTo[URL] {
    def to(a: String): Either[ParseFailure, URL]= urlValidator.validate(a)
  }

  implicit val toMD5s: ConvertTo[MD5] = new ConvertTo[MD5] {
    def to(a: String): Either[ParseFailure, MD5]= tryCatch {
      val reg = "^[a-fA-F0-9]{32}$".r
      if (reg.findFirstMatchIn(a).isDefined) MD5(a) else throw new Exception
    }(s"$a is not MD5")
  }

  implicit val toIPs: ConvertTo[IP] =new ConvertTo[IP] {
    def to(a: String): Either[ParseFailure, IP]=tryCatch {
      val reg = "^(?:(?:2(?:[0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\\.){3}(?:(?:2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9]))$".r
      if (reg.findFirstMatchIn(a).isDefined) IP(a) else throw new Exception
    }(s"$a is not IP")
  }


  implicit val toIP6s: ConvertTo[IP6] = new ConvertTo[IP6] {
    def to(a: String): Either[ParseFailure, IP6]= tryCatch {
      val reg = "^(([a-fA-F0-9]{1,4}|):){1,7}([a-fA-F0-9]{1,4}|:)$".r
      if (reg.findFirstMatchIn(a).isDefined) IP6(a) else throw new Exception
    }(s"$a is not IP6")
  }



  implicit val toSHA1s: ConvertTo[SHA1] = new ConvertTo[SHA1] {
    def to(a: String): Either[ParseFailure, SHA1]=tryCatch {
      val reg = "^[a-fA-F0-9]{40}$".r
      if (reg.findFirstMatchIn(a).isDefined) SHA1(a) else throw new Exception
    }(s"$a is not SHA1")
  }


  implicit val toSHA256s: ConvertTo[SHA256] =new ConvertTo[SHA256] {
    def to(a: String): Either[ParseFailure, SHA256]=tryCatch {
      val reg = "^[A-Fa-f0-9]{64}$".r
      if (reg.findFirstMatchIn(a).isDefined) SHA256(a) else throw new Exception
    }(s"$a is not SHA256")
  }


  implicit val toUUIDS: ConvertTo[UUID] = new ConvertTo[UUID] {
    def to(a: String): Either[ParseFailure, UUID]=  tryCatch(UUID.fromString(a))(s"$a is not UUID")
  }


  def convert[A](s: String)(implicit f: ConvertTo[A]): Either[ParseFailure, A] = f.to(s)
}
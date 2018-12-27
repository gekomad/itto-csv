package com.github.gekomad.ittocsv.core

import com.github.gekomad.ittocsv.core.FromCsv.ParseFailure

import scala.util.matching.Regex

object Types {

  object UrlOps {

    case class URL(url: String)

    case class UrlValidator(urlRegex: String) {

      def validate(url: String): Either[ParseFailure, URL] =
        if (urlRegex.r.findFirstMatchIn(url).isDefined) Right(URL(url)) else Left(ParseFailure(s"Not a URL $url")): Either[ParseFailure, URL]
    }

    implicit val validator: UrlValidator = UrlValidator( """^((\w+:\/\/)[-a-zA-Z0-9:@;?&=\/%\+\.\*!'\(\),\$_\{\}\^~\[\]`#|]+)$""")

  }

  object EmailOps {

    case class Email(email: String)

    case class EmailValidator(emailRegex: String) {

      def validate(email: String): Either[ParseFailure, Email] =
        if (emailRegex.r.findFirstMatchIn(email).isDefined) Right(Email(email)) else Left(ParseFailure(s"Not a Email $email")): Either[ParseFailure, Email]
    }

    implicit val validator: EmailValidator = EmailValidator( """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""")

  }

  object MD5Ops {

    case class MD5(code: String)

  }

  object SHAOps {

    case class SHA1(code: String)
    case class SHA256(code: String)

  }


  object IPOps {

    case class IP(code: String)
    case class IP6(code: String)

  }



}



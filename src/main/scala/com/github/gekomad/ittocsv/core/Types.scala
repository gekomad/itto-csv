package com.github.gekomad.ittocsv.core
import com.github.gekomad.regexcollection.Collection.Validator

object Types {

  object UrlOps {

    case class URL(url: String)

    case class UrlValidator(urlRegex: String = com.github.gekomad.regexcollection.Collection.validatorURL.regexp) {
      implicit val validator: Validator[com.github.gekomad.regexcollection.URL] = Validator[com.github.gekomad.regexcollection.URL](urlRegex)
      def validate(url: String): Either[ParseFailure, URL] =
        com.github.gekomad.regexcollection.Validate
          .validate[com.github.gekomad.regexcollection.URL](url)
          .map(_ => Right(URL(url)))
          .getOrElse(Left(ParseFailure(s"Not a URL $url")): Either[ParseFailure, URL])

    }

    implicit val validator: UrlValidator = UrlValidator()

  }

  object EmailOps {

    case class Email(email: String)

    case class EmailValidator(emailRegex: String = com.github.gekomad.regexcollection.Collection.validatorEmail.regexp) {
      implicit val validator: Validator[com.github.gekomad.regexcollection.Email] = Validator[com.github.gekomad.regexcollection.Email](emailRegex)
      def validate(email: String): Either[ParseFailure, Email] = {
        com.github.gekomad.regexcollection.Validate
          .validate[com.github.gekomad.regexcollection.Email](email)
          .map(_ => Right(Email(email)))
          .getOrElse(Left(ParseFailure(s"Not a Email $email")): Either[ParseFailure, Email])
      }
    }

    implicit val validator: EmailValidator = EmailValidator()
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

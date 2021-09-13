package com.github.gekomad.ittocsv.core

import scala.deriving.Mirror
import com.github.gekomad.regexcollection.Collection.Validator

object Types {

  trait Validate[A] {
    def validate(value: String): Either[List[String], A]
  }

  type Cons[A] = String => A

  final case class RegexValidator[A](regex: String)(using apply: Cons[A], m: Mirror.Of[A]) extends Validate[A] {
    given Validator[A] = Validator[A](regex)

    def validate(value: String): Either[List[String], A] = {
      com.github.gekomad.regexcollection.Validate
        .validate[A](value)
        .map(_ => Right(apply(value)))
        .getOrElse(Left(List(s"$value value is not valid ${m.toString}")))
    }
  }

  object implicits {
    final case class Youtube(value: String)

    final case class Facebook(value: String)

    final case class ApacheError(value: String)

    final case class Twitter(value: String)

    final case class UsdCurrency(value: String)

    final case class EurCurrency(value: String)

    final case class YenCurrency(value: String)

    final case class NotASCII(value: String)

    final case class SingleChar(value: String)

    final case class AZString(value: String)

    final case class StringAndNumber(value: String)

    final case class AsciiString(value: String)

    final case class Number1(value: String)

    final case class Unsigned32(value: String)

    final case class Signed(value: String)

    final case class Percentage(value: String)

    final case class Scientific(value: String)

    final case class SingleNumber(value: String)

    final case class Celsius(value: String)

    final case class Fahrenheit(value: String)

    final case class Coordinate(value: String)

    final case class Coordinate1(value: String)

    final case class Coordinate2(value: String)

    final case class MACAddress(value: String)

    final case class Email(value: String)

    final case class Email1(value: String)

    final case class EmailSimple(value: String)

    final case class HEX(value: String)

    final case class HEX1(value: String)

    final case class HEX2(value: String)

    final case class HEX3(value: String)

    final case class URL(value: String)

    final case class URL1(value: String)

    final case class URL2(value: String)

    final case class URL3(value: String)

    final case class FTP(value: String)

    final case class FTP1(value: String)

    final case class FTP2(value: String)

    final case class Domain(value: String)

    final case class MD5(value: String)

    final case class SHA1(value: String)

    final case class SHA256(value: String)

    final case class IP(value: String)

    final case class BitcoinAdd(value: String)

    final case class IP6(value: String)

    final case class USphoneNumber(value: String)

    final case class ItalianMobilePhone(value: String)

    final case class ItalianPhone(value: String)

    final case class Time24(value: String)

    final case class MDY(value: String)

    final case class MDY2(value: String)

    final case class MDY3(value: String)

    final case class MDY4(value: String)

    final case class DMY(value: String)

    final case class DMY2(value: String)

    final case class DMY3(value: String)

    final case class DMY4(value: String)

    final case class Time(value: String)

    final case class ItalianFiscalCode(value: String)

    final case class ItalianVAT(value: String)

    final case class ItalianIban(value: String)

    final case class USstates(value: String)

    final case class USstates1(value: String)

    final case class USZipCode(value: String)

    final case class ItalianZipCode(value: String)

    final case class USstreets(value: String)

    final case class USstreetNumber(value: String)

    final case class GermanStreet(value: String)

    final case class Cron(value: String)

    given Cons[Youtube] = Youtube.apply

    given Cons[Facebook] = Facebook.apply

    given Cons[Twitter] = Twitter.apply

    given Cons[MACAddress] = MACAddress.apply

    given Cons[Email] = Email.apply

    given Cons[EmailSimple] = EmailSimple.apply

    given Cons[Email1] = Email1.apply

    given Cons[HEX] = HEX.apply

    given Cons[HEX1] = HEX1.apply

    given Cons[HEX2] = HEX2.apply

    given Cons[HEX3] = HEX3.apply

    given Cons[URL] = URL.apply

    given Cons[URL1] = URL1.apply

    given Cons[URL2] = URL2.apply

    given Cons[URL3] = URL3.apply

    given Cons[FTP] = FTP.apply

    given Cons[FTP1] = FTP1.apply

    given Cons[FTP2] = FTP2.apply

    given Cons[Domain] = Domain.apply

    given Cons[MD5] = MD5.apply

    given Cons[SHA1] = SHA1.apply

    given Cons[SHA256] = SHA256.apply

    given Cons[IP] = IP.apply

    given Cons[IP6] = IP6.apply

    given Cons[BitcoinAdd] = BitcoinAdd.apply

    given Cons[USphoneNumber] = USphoneNumber.apply

    given Cons[ItalianMobilePhone] = ItalianMobilePhone.apply

    given Cons[ItalianPhone] = ItalianPhone.apply

    given Cons[Time24] = Time24.apply

    given Cons[MDY] = MDY.apply

    given Cons[MDY2] = MDY2.apply

    given Cons[MDY3] = MDY3.apply

    given Cons[MDY4] = MDY4.apply

    given Cons[DMY] = DMY.apply

    given Cons[DMY2] = DMY2.apply

    given Cons[DMY3] = DMY3.apply

    given Cons[DMY4] = DMY4.apply

    given Cons[Time] = Time.apply

    given Cons[Cron] = Cron.apply

    given Cons[ItalianFiscalCode] = ItalianFiscalCode.apply

    given Cons[ItalianVAT] = ItalianVAT.apply

    given Cons[ItalianIban] = ItalianIban.apply

    given Cons[USstates] = USstates.apply

    given Cons[USstates1] = USstates1.apply

    given Cons[USZipCode] = USZipCode.apply

    given Cons[ItalianZipCode] = ItalianZipCode.apply

    given Cons[USstreets] = USstreets.apply

    given Cons[USstreetNumber] = USstreetNumber.apply

    given Cons[GermanStreet] = GermanStreet.apply

    given Cons[UsdCurrency] = UsdCurrency.apply

    given Cons[EurCurrency] = EurCurrency.apply

    given Cons[YenCurrency] = YenCurrency.apply

    given Cons[NotASCII] = NotASCII.apply

    given Cons[SingleChar] = SingleChar.apply

    given Cons[AZString] = AZString.apply

    given Cons[StringAndNumber] = StringAndNumber.apply

    given Cons[AsciiString] = AsciiString.apply

    given Cons[ApacheError] = ApacheError.apply

    given Cons[Number1] = Number1.apply

    given Cons[Unsigned32] = Unsigned32.apply

    given Cons[Signed] = Signed.apply

    given Cons[Percentage] = Percentage.apply

    given Cons[Scientific] = Scientific.apply

    given Cons[SingleNumber] = SingleNumber.apply

    given Cons[Celsius] = Celsius.apply

    given Cons[Fahrenheit] = Fahrenheit.apply

    given Cons[Coordinate] = Coordinate.apply

    given Cons[Coordinate1] = Coordinate1.apply

    given Cons[Coordinate2] = Coordinate2.apply
  }
}

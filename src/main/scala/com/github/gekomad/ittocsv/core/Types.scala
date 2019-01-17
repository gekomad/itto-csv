package com.github.gekomad.ittocsv.core

object Types {

  trait Validate[A] {
    def validate(value: String): Either[ParseFailure, A]
  }

  type Cons[A] = String => A

  case class Validator[A](regex: String, typeName: String)(implicit apply: Cons[A]) extends Validate[A] {
    implicit val validator: com.github.gekomad.regexcollection.Collection.Validator[A] = com.github.gekomad.regexcollection.Collection.Validator[A](regex)
    def validate(value: String): Either[ParseFailure, A] =
      com.github.gekomad.regexcollection.Validate
        .validate[A](value)
        .map(_ => Right(apply(value)))
        .getOrElse(Left(ParseFailure(s"Not a $typeName $value")): Either[ParseFailure, A])
  }

  object implicits {
    case class Youtube(value: String)
    case class Facebook(value: String)
    case class Twitter(value: String)

    implicit val apply0: Cons[Youtube]  = Youtube
    implicit val apply1: Cons[Facebook] = Facebook
    implicit val apply2: Cons[Twitter]  = Twitter

    implicit val validatorYoutube: Validator[Youtube]   = Validator[Youtube](com.github.gekomad.regexcollection.Collection.validatorYoutube.regexp, Youtube.toString)
    implicit val validatorFacebook: Validator[Facebook] = Validator[Facebook](com.github.gekomad.regexcollection.Collection.validatorFacebook.regexp, Facebook.toString)
    implicit val validatorTwitter: Validator[Twitter]   = Validator[Twitter](com.github.gekomad.regexcollection.Collection.validatorTwitter.regexp, Twitter.toString)

    // MACAddressOps
    case class MACAddress(value: String)
    implicit val apply3: Cons[MACAddress] = MACAddress

    implicit val validatorMACAddress: Validator[MACAddress] = Validator[MACAddress](com.github.gekomad.regexcollection.Collection.validatorMACAddress.regexp, MACAddress.toString)

    // EmailOps

    implicit val apply4: Cons[Email]       = Email
    implicit val apply5: Cons[EmailSimple] = EmailSimple
    implicit val applye1: Cons[Email1]     = Email1

    case class Email(email: String)
    case class Email1(email: String)
    case class EmailSimple(emailSimple: String)
    implicit val validatorEmail1: Validator[Email1]           = Validator[Email1](com.github.gekomad.regexcollection.Collection.validatorEmail1.regexp, Email1.toString)
    implicit val validatorEmail: Validator[Email]             = Validator[Email](com.github.gekomad.regexcollection.Collection.validatorEmail.regexp, Email.toString)
    implicit val validatorEmailSimple: Validator[EmailSimple] = Validator[EmailSimple](com.github.gekomad.regexcollection.Collection.validatorEmailSimple.regexp, EmailSimple.toString)

    // HexOps
    case class HEX(value: String)
    case class HEX1(value: String)
    case class HEX2(value: String)
    case class HEX3(value: String)

    implicit val apply6: Cons[HEX]  = HEX
    implicit val apply7: Cons[HEX1] = HEX1
    implicit val apply8: Cons[HEX2] = HEX2
    implicit val apply9: Cons[HEX3] = HEX3

    implicit val validatorHEX: Validator[HEX]   = Validator[HEX](com.github.gekomad.regexcollection.Collection.validatorHEX.regexp, HEX.toString)
    implicit val validatorHEX1: Validator[HEX1] = Validator[HEX1](com.github.gekomad.regexcollection.Collection.validatorHEX1.regexp, HEX1.toString)
    implicit val validatorHEX2: Validator[HEX2] = Validator[HEX2](com.github.gekomad.regexcollection.Collection.validatorHEX2.regexp, HEX2.toString)
    implicit val validatorHEX3: Validator[HEX3] = Validator[HEX3](com.github.gekomad.regexcollection.Collection.validatorHEX3.regexp, HEX3.toString)

    // UrlOps
    case class URL(value: String)
    case class URL1(value: String)
    case class URL2(value: String)
    case class URL3(value: String)
    case class FTP(value: String)
    case class FTP1(value: String)
    case class FTP2(value: String)
    case class Domain(value: String)

    implicit val apply10: Cons[URL]    = URL
    implicit val apply11: Cons[URL1]   = URL1
    implicit val apply12: Cons[URL2]   = URL2
    implicit val apply13: Cons[URL3]   = URL3
    implicit val apply14: Cons[FTP]    = FTP
    implicit val apply15: Cons[FTP1]   = FTP1
    implicit val apply16: Cons[FTP2]   = FTP2
    implicit val apply17: Cons[Domain] = Domain

    implicit val validatorURL: Validator[URL]       = Validator[URL](com.github.gekomad.regexcollection.Collection.validatorURL.regexp, URL.toString)
    implicit val validatorURL1: Validator[URL1]     = Validator[URL1](com.github.gekomad.regexcollection.Collection.validatorURL1.regexp, URL1.toString)
    implicit val validatorURL2: Validator[URL2]     = Validator[URL2](com.github.gekomad.regexcollection.Collection.validatorURL2.regexp, URL2.toString)
    implicit val validatorURL3: Validator[URL3]     = Validator[URL3](com.github.gekomad.regexcollection.Collection.validatorURL3.regexp, URL3.toString)
    implicit val validatorFTP: Validator[FTP]       = Validator[FTP](com.github.gekomad.regexcollection.Collection.validatorFTP.regexp, FTP.toString)
    implicit val validatorFTP1: Validator[FTP1]     = Validator[FTP1](com.github.gekomad.regexcollection.Collection.validatorFTP1.regexp, FTP1.toString)
    implicit val validatorFTP2: Validator[FTP2]     = Validator[FTP2](com.github.gekomad.regexcollection.Collection.validatorFTP2.regexp, FTP2.toString)
    implicit val validatorDomain: Validator[Domain] = Validator[Domain](com.github.gekomad.regexcollection.Collection.validatorDomain.regexp, Domain.toString)

    // MD5Ops
    implicit val apply18: Cons[MD5] = MD5
    case class MD5(url: String)
    implicit val validatorMD5: Validator[MD5] = Validator[MD5](com.github.gekomad.regexcollection.Collection.validatorMD5.regexp, MD5.toString)

    // SHAOps
    implicit val apply19: Cons[SHA1]   = SHA1
    implicit val apply20: Cons[SHA256] = SHA256

    case class SHA1(value: String)
    case class SHA256(value: String)

    implicit val validatorSHA1: Validator[SHA1]     = Validator[SHA1](com.github.gekomad.regexcollection.Collection.validatorSHA1.regexp, SHA1.toString)
    implicit val validatorSHA256: Validator[SHA256] = Validator[SHA256](com.github.gekomad.regexcollection.Collection.validatorSHA256.regexp, SHA256.toString)

    // IPOps
    implicit val apply21: Cons[IP]  = IP
    implicit val apply22: Cons[IP6] = IP6

    case class IP(value: String)
    case class IP6(value: String)

    implicit val validatorIP: Validator[IP]   = Validator[IP](com.github.gekomad.regexcollection.Collection.validatorIP.regexp, IP.toString)
    implicit val validatorIP6: Validator[IP6] = Validator[IP6](com.github.gekomad.regexcollection.Collection.validatorIP_6.regexp, IP6.toString)

    // BitcoinAddOps
    implicit def apply23: Cons[BitcoinAdd] = BitcoinAdd

    case class BitcoinAdd(value: String)

    implicit val validatorBitcoinAdd: Validator[BitcoinAdd] = Validator[BitcoinAdd](com.github.gekomad.regexcollection.Collection.validatorBitcoinAdd.regexp, BitcoinAdd.toString)

    // PhonesOps
    implicit val apply24: Cons[USphoneNumber]      = USphoneNumber
    implicit val apply25: Cons[ItalianMobilePhone] = ItalianMobilePhone
    implicit val apply26: Cons[ItalianPhone]       = ItalianPhone

    case class USphoneNumber(value: String)
    case class ItalianMobilePhone(value: String)
    case class ItalianPhone(value: String)

    implicit val validatorUSphoneNumber: Validator[USphoneNumber] = Validator[USphoneNumber](com.github.gekomad.regexcollection.Collection.validatorUSphoneNumber.regexp, USphoneNumber.toString)
    implicit val validatorItalianMobilePhone: Validator[ItalianMobilePhone] =
      Validator[ItalianMobilePhone](com.github.gekomad.regexcollection.Collection.validatorItalianMobilePhone.regexp, ItalianMobilePhone.toString)
    implicit val validatorItalianPhone: Validator[ItalianPhone] = Validator[ItalianPhone](com.github.gekomad.regexcollection.Collection.validatorItalianPhone.regexp, ItalianPhone.toString)

    // TimeOps
    implicit val apply27: Cons[Time24] = Time24
    implicit val apply28: Cons[MDY]    = MDY
    implicit val apply29: Cons[MDY2]   = MDY2
    implicit val apply30: Cons[MDY3]   = MDY3
    implicit val apply31: Cons[MDY4]   = MDY4
    implicit val apply32: Cons[DMY]    = DMY
    implicit val apply33: Cons[DMY2]   = DMY2
    implicit val apply34: Cons[DMY3]   = DMY3
    implicit val apply35: Cons[DMY4]   = DMY4
    implicit val apply36: Cons[Time]   = Time

    case class Time24(value: String)
    case class MDY(value: String)
    case class MDY2(value: String)
    case class MDY3(value: String)
    case class MDY4(value: String)
    case class DMY(value: String)
    case class DMY2(value: String)
    case class DMY3(value: String)
    case class DMY4(value: String)
    case class Time(value: String)

    implicit val validatorTime24: Validator[Time24] = Validator[Time24](com.github.gekomad.regexcollection.Collection.validatorTime24.regexp, Time24.toString)
    implicit val validatorMDY: Validator[MDY]       = Validator[MDY](com.github.gekomad.regexcollection.Collection.validatorMDY.regexp, MDY.toString)
    implicit val validatorMDY2: Validator[MDY2]     = Validator[MDY2](com.github.gekomad.regexcollection.Collection.validatorMDY2.regexp, MDY2.toString)
    implicit val validatorMDY3: Validator[MDY3]     = Validator[MDY3](com.github.gekomad.regexcollection.Collection.validatorMDY3.regexp, MDY3.toString)
    implicit val validatorMDY4: Validator[MDY4]     = Validator[MDY4](com.github.gekomad.regexcollection.Collection.validatorMDY4.regexp, MDY4.toString)
    implicit val validatorDMY: Validator[DMY]       = Validator[DMY](com.github.gekomad.regexcollection.Collection.validatorDMY.regexp, DMY.toString)
    implicit val validatorDMY2: Validator[DMY2]     = Validator[DMY2](com.github.gekomad.regexcollection.Collection.validatorDMY2.regexp, DMY2.toString)
    implicit val validatorDMY3: Validator[DMY3]     = Validator[DMY3](com.github.gekomad.regexcollection.Collection.validatorDMY3.regexp, DMY3.toString)
    implicit val validatorDMY4: Validator[DMY4]     = Validator[DMY4](com.github.gekomad.regexcollection.Collection.validatorDMY4.regexp, DMY4.toString)
    implicit val validatorTime: Validator[Time]     = Validator[Time](com.github.gekomad.regexcollection.Collection.validatorTime.regexp, Time.toString)

    // CrontabOps
    case class Cron(value: String)
    implicit val apply37: Cons[Cron]            = Cron
    implicit val validatorCron: Validator[Cron] = Validator[Cron](com.github.gekomad.regexcollection.Collection.validatorCron.regexp, Cron.toString)

    // CodesOps

    case class ItalianFiscalCode(value: String)
    case class ItalianVAT(value: String)
    case class ItalianIban(value: String)
    case class USstates(value: String)
    case class USstates1(value: String)
    case class USZipCode(value: String)
    case class ItalianZipCode(value: String)
    case class USstreets(value: String)
    case class USstreetNumber(value: String)
    case class GermanStreet(value: String)

    implicit val apply38: Cons[ItalianFiscalCode] = ItalianFiscalCode
    implicit val apply39: Cons[ItalianVAT]        = ItalianVAT
    implicit val apply40: Cons[ItalianIban]       = ItalianIban
    implicit val apply401: Cons[USstates]         = USstates
    implicit val apply402: Cons[USstates1]        = USstates1
    implicit val apply53: Cons[USZipCode]         = USZipCode
    implicit val apply54: Cons[ItalianZipCode]    = ItalianZipCode
    implicit val apply541: Cons[USstreets]        = USstreets
    implicit val apply542: Cons[USstreetNumber]   = USstreetNumber
    implicit val apply543: Cons[GermanStreet]     = GermanStreet

    implicit val validatorItalianFiscalCode: Validator[ItalianFiscalCode] =
      Validator[ItalianFiscalCode](com.github.gekomad.regexcollection.Collection.validatorItalianFiscalCode.regexp, ItalianFiscalCode.toString)
    implicit val validatorItalianVAT: Validator[ItalianVAT]         = Validator[ItalianVAT](com.github.gekomad.regexcollection.Collection.validatorItalianVAT.regexp, ItalianVAT.toString)
    implicit val validatorItalianIban: Validator[ItalianIban]       = Validator[ItalianIban](com.github.gekomad.regexcollection.Collection.validatorItalianIban.regexp, ItalianIban.toString)
    implicit val validatorUSstates: Validator[USstates]             = Validator[USstates](com.github.gekomad.regexcollection.Collection.validatorUSstates.regexp, USstates.toString)
    implicit val validatorUSstates1: Validator[USstates1]           = Validator[USstates1](com.github.gekomad.regexcollection.Collection.validatorUSstates1.regexp, USstates1.toString)
    implicit val validatorUSZipCode: Validator[USZipCode]           = Validator[USZipCode](com.github.gekomad.regexcollection.Collection.validatorUSZipCode.regexp, USZipCode.toString)
    implicit val validatorItalianZipCode: Validator[ItalianZipCode] = Validator[ItalianZipCode](com.github.gekomad.regexcollection.Collection.validatorItalianZipCode.regexp, ItalianZipCode.toString)
    implicit val validatorUSstreets: Validator[USstreets]           = Validator[USstreets](com.github.gekomad.regexcollection.Collection.validatorUSstreets.regexp, USstreets.toString)
    implicit val validatorUSstreetNumber: Validator[USstreetNumber] = Validator[USstreetNumber](com.github.gekomad.regexcollection.Collection.validatorUSstreetNumber.regexp, USstreetNumber.toString)
    implicit val validatorGermanStreet: Validator[GermanStreet]     = Validator[GermanStreet](com.github.gekomad.regexcollection.Collection.validatorGermanStreet.regexp, GermanStreet.toString)

    // ConcurrencyOps
    case class UsdCurrency(value: String)
    case class EurCurrency(value: String)
    case class YenCurrency(value: String)

    implicit val apply41: Cons[UsdCurrency] = UsdCurrency
    implicit val apply42: Cons[EurCurrency] = EurCurrency
    implicit val apply43: Cons[YenCurrency] = YenCurrency

    implicit val validatorUsdCurrency: Validator[UsdCurrency] = Validator[UsdCurrency](com.github.gekomad.regexcollection.Collection.validatorUsdCurrency.regexp, UsdCurrency.toString)
    implicit val validatorEurCurrency: Validator[EurCurrency] = Validator[EurCurrency](com.github.gekomad.regexcollection.Collection.validatorEurCurrency.regexp, EurCurrency.toString)
    implicit val validatorYenCurrency: Validator[YenCurrency] = Validator[YenCurrency](com.github.gekomad.regexcollection.Collection.validatorYenCurrency.regexp, YenCurrency.toString)

    // StringsOps
    case class NotASCII(value: String)
    case class SingleChar(value: String)
    case class AZString(value: String)
    case class StringAndNumber(value: String)
    case class AsciiString(value: String)

    implicit val apply44: Cons[NotASCII]         = NotASCII
    implicit val apply441: Cons[SingleChar]      = SingleChar
    implicit val apply442: Cons[AZString]        = AZString
    implicit val apply443: Cons[StringAndNumber] = StringAndNumber
    implicit val apply444: Cons[AsciiString]     = AsciiString

    implicit val validatorNotASCII: Validator[NotASCII]       = Validator[NotASCII](com.github.gekomad.regexcollection.Collection.validatorNotASCII.regexp, NotASCII.toString)
    implicit val validatorSingleChar: Validator[SingleChar]   = Validator[SingleChar](com.github.gekomad.regexcollection.Collection.validatorSingleChar.regexp, SingleChar.toString)
    implicit val validatorAZString: Validator[AZString]       = Validator[AZString](com.github.gekomad.regexcollection.Collection.validatorAZString.regexp, AZString.toString)
    implicit val validatorAsciiString: Validator[AsciiString] = Validator[AsciiString](com.github.gekomad.regexcollection.Collection.validatorAsciiString.regexp, AsciiString.toString)
    implicit val validatorStringAndNumber: Validator[StringAndNumber] =
      Validator[StringAndNumber](com.github.gekomad.regexcollection.Collection.validatorStringAndNumber.regexp, StringAndNumber.toString)

    // LogsOps
    case class ApacheError(value: String)
    implicit val apply45: Cons[ApacheError] = ApacheError

    implicit val validatorApacheError: Validator[ApacheError] = Validator[ApacheError](com.github.gekomad.regexcollection.Collection.validatorApacheError.regexp, ApacheError.toString)

    // NumbersOps
    case class Number1(value: String)
    case class Unsigned32(value: String)
    case class Signed(value: String)
    case class Percentage(value: String)
    case class Scientific(value: String)
    case class SingleNumber(value: String)
    case class Celsius(value: String)
    case class Fahrenheit(value: String)

    implicit val apply46: Cons[Number1]       = Number1
    implicit val apply47: Cons[Unsigned32]    = Unsigned32
    implicit val apply48: Cons[Signed]        = Signed
    implicit val apply49: Cons[Percentage]    = Percentage
    implicit val apply491: Cons[Scientific]   = Scientific
    implicit val apply492: Cons[SingleNumber] = SingleNumber
    implicit val apply493: Cons[Celsius]      = Celsius
    implicit val apply494: Cons[Fahrenheit]   = Fahrenheit

    implicit val validatorNumber1: Validator[Number1]           = Validator[Number1](com.github.gekomad.regexcollection.Collection.validatorNumber1.regexp, Number1.toString)
    implicit val validatorUnsigned32: Validator[Unsigned32]     = Validator[Unsigned32](com.github.gekomad.regexcollection.Collection.validatorUnsigned32.regexp, Unsigned32.toString)
    implicit val validatorSigned: Validator[Signed]             = Validator[Signed](com.github.gekomad.regexcollection.Collection.validatorSigned.regexp, Signed.toString)
    implicit val validatorPercentage: Validator[Percentage]     = Validator[Percentage](com.github.gekomad.regexcollection.Collection.validatorPercentage.regexp, Percentage.toString)
    implicit val validatorScientific: Validator[Scientific]     = Validator[Scientific](com.github.gekomad.regexcollection.Collection.validatorScientific.regexp, Scientific.toString)
    implicit val validatorSingleNumber: Validator[SingleNumber] = Validator[SingleNumber](com.github.gekomad.regexcollection.Collection.validatorSingleNumber.regexp, SingleNumber.toString)
    implicit val validatorCelsius: Validator[Celsius]           = Validator[Celsius](com.github.gekomad.regexcollection.Collection.validatorCelsius.regexp, Celsius.toString)
    implicit val validatorFahrenheit: Validator[Fahrenheit]     = Validator[Fahrenheit](com.github.gekomad.regexcollection.Collection.validatorFahrenheit.regexp, Fahrenheit.toString)

    // CoordinatesOps
    case class Coordinate(value: String)
    case class Coordinate1(value: String)
    case class Coordinate2(value: String)

    implicit val apply50: Cons[Coordinate]  = Coordinate
    implicit val apply51: Cons[Coordinate1] = Coordinate1
    implicit val apply52: Cons[Coordinate2] = Coordinate2

    implicit val validatorCoordinate: Validator[Coordinate]   = Validator[Coordinate](com.github.gekomad.regexcollection.Collection.validatorCoordinate.regexp, Coordinate.toString)
    implicit val validatorCoordinate1: Validator[Coordinate1] = Validator[Coordinate1](com.github.gekomad.regexcollection.Collection.validatorCoordinate1.regexp, Coordinate1.toString)
    implicit val validatorCoordinate2: Validator[Coordinate2] = Validator[Coordinate2](com.github.gekomad.regexcollection.Collection.validatorCoordinate2.regexp, Coordinate2.toString)

  }
}

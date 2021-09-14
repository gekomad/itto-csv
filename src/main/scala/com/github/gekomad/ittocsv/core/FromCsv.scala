package com.github.gekomad.ittocsv.core

import com.github.gekomad.ittocsv.core.FromCsv.Decoder
import com.github.gekomad.ittocsv.core.Types.RegexValidator
import com.github.gekomad.ittocsv.core.Types.implicits.*
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import com.github.gekomad.ittocsv.util.StringUtils.tokenizeCsvLine
import com.github.gekomad.regexcollection.Collection.*
import java.time.*
import java.time.format.DateTimeFormatter.*
import scala.util.Try

object FromCsv:

  import scala.deriving.Mirror

  trait Decoder[A, B] extends (A => Either[List[String], B])

  object Decoder:
    given Decoder[String, String] = Right(_)

  given Decoder[String, java.util.UUID] = a =>
    Try(java.util.UUID.fromString(a)).toOption.toRight(List(s"$a value is not valid UUID"))
  given Decoder[String, LocalDateTime] = a =>
    Try(LocalDateTime.parse(a, ISO_LOCAL_DATE_TIME)).toOption.toRight(List(s"$a value is not valid LocalDateTime"))
  given Decoder[String, LocalDate] = a =>
    Try(LocalDate.parse(a, ISO_LOCAL_DATE)).toOption.toRight(List(s"$a value is not valid LocalDate"))
  given Decoder[String, LocalTime] = a =>
    Try(LocalTime.parse(a, ISO_LOCAL_TIME)).toOption.toRight(List(s"$a value is not valid LocalTime"))
  given Decoder[String, OffsetDateTime] = a =>
    Try(OffsetDateTime.parse(a, ISO_OFFSET_DATE_TIME)).toOption.toRight(List(s"$a value is not valid OffsetDateTime"))
  given Decoder[String, OffsetTime] = a =>
    Try(OffsetTime.parse(a, ISO_OFFSET_TIME)).toOption.toRight(List(s"$a value is not valid OffsetTime"))
  given Decoder[String, ZonedDateTime] = a =>
    Try(ZonedDateTime.parse(a, ISO_ZONED_DATE_TIME)).toOption.toRight(List(s"$a value is not valid ZonedDateTime"))
  given Decoder[String, Instant] = a => Try(Instant.parse(a)).toOption.toRight(List(s"$a value is not valid Instant"))

  given Decoder[String, Boolean] = a =>
    if (a.toLowerCase == "true") Right(true)
    else if (a.toLowerCase == "false") Right(false)
    else Left(List(s"$a value is not valid Boolean"))

  given Decoder[String, Int] = a => a.toIntOption.toRight(List(s"$a value is not valid Int"))

  given Decoder[String, Char] = a => {
    if (a.size == 1) a.headOption.toRight(List(s"$a value is not valid Char"))
    else
      Left(List(s"$a value is not valid Char"))
  }

  given Decoder[String, Double] = a => a.toDoubleOption.toRight(List(s"$a value is not valid Double"))

  given Decoder[String, SHA1] = a => RegexValidator[SHA1](validatorSHA1.regexp).validate(a)

  given Decoder[String, SHA256] = a => RegexValidator[SHA256](validatorSHA256.regexp).validate(a)

  given Decoder[String, IP] = a => RegexValidator[IP](validatorIP.regexp).validate(a)

  given Decoder[String, IP6] = a => RegexValidator[IP6](validatorIP_6.regexp).validate(a)

  given Decoder[String, BitcoinAdd] = a => RegexValidator[BitcoinAdd](validatorBitcoinAdd.regexp).validate(a)

  given Decoder[String, USphoneNumber] = a => RegexValidator[USphoneNumber](validatorUSphoneNumber.regexp).validate(a)

  given Decoder[String, ItalianMobilePhone] = a =>
    RegexValidator[ItalianMobilePhone](validatorItalianMobilePhone.regexp).validate(a)

  given Decoder[String, ItalianPhone] = a => RegexValidator[ItalianPhone](validatorItalianPhone.regexp).validate(a)

  given Decoder[String, Time24] = a => RegexValidator[Time24](validatorTime24.regexp).validate(a)

  given Decoder[String, MDY] = a => RegexValidator[MDY](validatorMDY.regexp).validate(a)

  given Decoder[String, MDY2] = a => RegexValidator[MDY2](validatorMDY2.regexp).validate(a)

  given Decoder[String, MDY3] = a => RegexValidator[MDY3](validatorMDY3.regexp).validate(a)

  given Decoder[String, MDY4] = a => RegexValidator[MDY4](validatorMDY4.regexp).validate(a)

  given Decoder[String, DMY] = a => RegexValidator[DMY](validatorDMY.regexp).validate(a)

  given Decoder[String, DMY2] = a => RegexValidator[DMY2](validatorDMY2.regexp).validate(a)

  given Decoder[String, DMY3] = a => RegexValidator[DMY3](validatorDMY3.regexp).validate(a)

  given Decoder[String, DMY4] = a => RegexValidator[DMY4](validatorDMY4.regexp).validate(a)

  given Decoder[String, Time] = a => RegexValidator[Time](validatorTime.regexp).validate(a)

  given Decoder[String, Cron] = a => RegexValidator[Cron](validatorCron.regexp).validate(a)

  given Decoder[String, ItalianFiscalCode] = a =>
    RegexValidator[ItalianFiscalCode](validatorItalianFiscalCode.regexp).validate(a)

  given Decoder[String, ItalianVAT] = a => RegexValidator[ItalianVAT](validatorItalianVAT.regexp).validate(a)

  given Decoder[String, ItalianIban] = a => RegexValidator[ItalianIban](validatorItalianIban.regexp).validate(a)

  given Decoder[String, USstates] = a => RegexValidator[USstates](validatorUSstates.regexp).validate(a)

  given Decoder[String, USstates1] = a => RegexValidator[USstates1](validatorUSstates1.regexp).validate(a)

  given Decoder[String, USZipCode] = a => RegexValidator[USZipCode](validatorUSZipCode.regexp).validate(a)

  given Decoder[String, ItalianZipCode] = a =>
    RegexValidator[ItalianZipCode](validatorItalianZipCode.regexp).validate(a)

  given Decoder[String, USstreets] = a => RegexValidator[USstreets](validatorUSstreets.regexp).validate(a)

  given Decoder[String, USstreetNumber] = a =>
    RegexValidator[USstreetNumber](validatorUSstreetNumber.regexp).validate(a)

  given Decoder[String, GermanStreet] = a => RegexValidator[GermanStreet](validatorGermanStreet.regexp).validate(a)

  given Decoder[String, UsdCurrency] = a => RegexValidator[UsdCurrency](validatorUsdCurrency.regexp).validate(a)

  given Decoder[String, EurCurrency] = a => RegexValidator[EurCurrency](validatorEurCurrency.regexp).validate(a)

  given Decoder[String, YenCurrency] = a => RegexValidator[YenCurrency](validatorYenCurrency.regexp).validate(a)

  given Decoder[String, NotASCII] = a => RegexValidator[NotASCII](validatorNotASCII.regexp).validate(a)

  given Decoder[String, SingleChar] = a => RegexValidator[SingleChar](validatorSingleChar.regexp).validate(a)

  given Decoder[String, AZString] = a => RegexValidator[AZString](validatorAZString.regexp).validate(a)

  given Decoder[String, AsciiString] = a => RegexValidator[AsciiString](validatorAsciiString.regexp).validate(a)

  given Decoder[String, StringAndNumber] = a =>
    RegexValidator[StringAndNumber](validatorStringAndNumber.regexp).validate(a)

  given Decoder[String, ApacheError] = a => RegexValidator[ApacheError](validatorApacheError.regexp).validate(a)

  given Decoder[String, Number1] = a => RegexValidator[Number1](validatorNumber1.regexp).validate(a)

  given Decoder[String, Unsigned32] = a => RegexValidator[Unsigned32](validatorUnsigned32.regexp).validate(a)

  given Decoder[String, Signed] = a => RegexValidator[Signed](validatorSigned.regexp).validate(a)

  given Decoder[String, Percentage] = a => RegexValidator[Percentage](validatorPercentage.regexp).validate(a)

  given Decoder[String, Scientific] = a => RegexValidator[Scientific](validatorScientific.regexp).validate(a)

  given Decoder[String, SingleNumber] = a => RegexValidator[SingleNumber](validatorSingleNumber.regexp).validate(a)

  given Decoder[String, Celsius] = a => RegexValidator[Celsius](validatorCelsius.regexp).validate(a)

  given Decoder[String, Fahrenheit] = a => RegexValidator[Fahrenheit](validatorFahrenheit.regexp).validate(a)

  given Decoder[String, Coordinate] = a => RegexValidator[Coordinate](validatorCoordinate.regexp).validate(a)

  given Decoder[String, Coordinate1] = a => RegexValidator[Coordinate1](validatorCoordinate1.regexp).validate(a)

  given Decoder[String, Coordinate2] = a => RegexValidator[Coordinate2](validatorCoordinate2.regexp).validate(a)

  given Decoder[String, Youtube] = a => RegexValidator[Youtube](validatorYoutube.regexp).validate(a)

  given Decoder[String, Facebook] = a => RegexValidator[Facebook](validatorFacebook.regexp).validate(a)

  given Decoder[String, Twitter] = a => RegexValidator[Twitter](validatorTwitter.regexp).validate(a)

  given Decoder[String, MACAddress] = a => RegexValidator[MACAddress](validatorMACAddress.regexp).validate(a)

  given Decoder[String, Email1] = a => RegexValidator[Email1](validatorEmail1.regexp).validate(a)

  given Decoder[String, Email] = a => RegexValidator[Email](validatorEmail.regexp).validate(a)

  given Decoder[String, EmailSimple] = a => RegexValidator[EmailSimple](validatorEmailSimple.regexp).validate(a)

  given Decoder[String, HEX] = a => RegexValidator[HEX](validatorHEX.regexp).validate(a)

  given Decoder[String, HEX1] = a => RegexValidator[HEX1](validatorHEX1.regexp).validate(a)

  given Decoder[String, HEX2] = a => RegexValidator[HEX2](validatorHEX2.regexp).validate(a)

  given Decoder[String, HEX3] = a => RegexValidator[HEX3](validatorHEX3.regexp).validate(a)

  given Decoder[String, URL] = a => RegexValidator[URL](validatorURL.regexp).validate(a)

  given Decoder[String, URL1] = a => RegexValidator[URL1](validatorURL1.regexp).validate(a)

  given Decoder[String, URL2] = a => RegexValidator[URL2](validatorURL2.regexp).validate(a)

  given Decoder[String, URL3] = a => RegexValidator[URL3](validatorURL3.regexp).validate(a)

  given Decoder[String, FTP] = a => RegexValidator[FTP](validatorFTP.regexp).validate(a)

  given Decoder[String, FTP1] = a => RegexValidator[FTP1](validatorFTP1.regexp).validate(a)

  given Decoder[String, FTP2] = a => RegexValidator[FTP2](validatorFTP2.regexp).validate(a)

  given Decoder[String, Domain] = a => RegexValidator[Domain](validatorDomain.regexp).validate(a)

  given Decoder[String, MD5] = a => RegexValidator[MD5](validatorMD5.regexp).validate(a)

  given [A](using f: Decoder[String, A]): Decoder[String, Option[A]] = s =>
    if (s == "") Right(None) else f(s).map(Some(_))

  given [A](using f: Decoder[String, A], csvFormat: IttoCSVFormat): Decoder[String, List[A]] = s =>
    val a = s.split(csvFormat.delimeter.toString, -1).toList.map(f(_))
    val (l, rights) = a.partitionMap(identity)
    val lefts = l.flatten
    if (lefts.isEmpty) Right(rights) else Left(lefts)

  given Decoder[List[String], EmptyTuple] =
    case Nil => Right(EmptyTuple)
    case s   => Left(List(s"$s empty list"))

  given [H, T <: Tuple](using dh: Decoder[String, H], dt: Decoder[List[String], T]): Decoder[List[String], H *: T] =
    case h :: t =>
      (dh(h), dt(t)) match
        case (Right(a), Right(b)) => Right(a *: b)
        case (Left(e), Left(e2))  => Left(e ::: e2)
        case (Left(e), _)         => Left(e)
        case (_, Left(e))         => Left(e)
    case Nil => Left(List("empty list"))

  def list2Product[A](
    xs: List[String]
  )(using m: Mirror.ProductOf[A], d: Decoder[List[String], m.MirroredElemTypes]): Either[List[String], A] =
    d(xs) match
      case Right(r) => Right(m.fromProduct(r))
      case Left(l)  => Left(l)

  /**
   * @param csvList
   *   is the List[String] to parse
   * @param csvFormat
   *   the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
   * @return
   *   `List[Either[List[String], A]]` based on the parsing of `csvList` any errors are reported
   */
  def fromCsv[A](
    csvList: List[String]
  )(using
    m: Mirror.ProductOf[A],
    d: Decoder[List[String], m.MirroredElemTypes],
    csvFormat: IttoCSVFormat
  ): List[Either[List[String], A]] = csvList match
    case Nil => Nil
    case l =>
      l.collect {
        case row if !row.isEmpty || !csvFormat.ignoreEmptyLines =>
          tokenizeCsvLine(row) match
            case None => Left(List(s"$csvList is not a valid csv string"))
            case Some(t) =>
              list2Product[A](t).match
                case Right(a) => Right(a)
                case Left(a)  => Left(a.toList)
      }

  def fromCsv[A](csv: String)(using
    m: Mirror.ProductOf[A],
    d: Decoder[List[String], m.MirroredElemTypes],
    csvFormat: IttoCSVFormat
  ): List[Either[List[String], A]] = fromCsv(csv.split(csvFormat.recordSeparator, -1).toList)

  def fromCsvL[A](
    x: String
  )(using dec: Decoder[String, A], csvFormat: IttoCSVFormat): List[Either[String, A]] =
    x.split(csvFormat.delimeter.toString, -1).toList.map(dec(_)).map {
      _ match
        case Left(a)  => Left(a.head)
        case Right(a) => Right(a)
    }

end FromCsv

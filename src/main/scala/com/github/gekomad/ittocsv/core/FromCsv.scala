package com.github.gekomad.ittocsv.core

/**
  * Converts a CSV to type
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information
  */
object FromCsv {

  import cats.data.NonEmptyList
  import com.github.gekomad.ittocsv.parser.IttoCSVFormat
  import com.github.gekomad.ittocsv.core.Header.{FieldNames, fieldNames}
  import com.github.gekomad.ittocsv.util.StringUtils.tokenizeCsvLine

  /**
    * @param csv       is the string to parse. It might contain record separator
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return `Seq[Either[NonEmptyList[ParseFailure], A]]` based on the parsing of `csv`, any errors are reported
    * {{{
    *  import com.github.gekomad.ittocsv.core.FromCsv._
    *  implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    *
    *  case class Bar(a: String, b: Int)
    *
    *  assert(fromCsv[Bar]("abc,42\r\nfoo,24") == List(Right(Bar("abc", 42)), Right(Bar("foo", 24))))
    *  assert(fromCsv[Bar]("abc,hi") == List(Left(cats.data.NonEmptyList(com.github.gekomad.ittocsv.core.FromCsv.ParseFailure("hi is not Int"), Nil))))
    *
    *  case class Foo(v: String, a: List[Int])
    *  assert(fromCsv[Foo]("abc,\"1,2,3\"") == List(Right(Foo("abc", List(1, 2, 3)))))
    * }}}
    */
  def fromCsv[A: FieldNames: Schema](csv: String)(implicit csvFormat: IttoCSVFormat): Seq[Either[NonEmptyList[ParseFailure], A]] =
    fromCsv(csv.split(csvFormat.recordSeparator, -1).toList)

  import com.github.gekomad.ittocsv.core.Conversions._

  /**
    *
    * @param csv       is the string to parse. It might contain record separator
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return `Seq[Either[ParseFailure, A]]` based on the parsing of `csv`, any errors are reported
    * {{{
    * import com.github.gekomad.ittocsv.core.FromCsv._
    *
    * implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    *
    * assert(fromCsvL[Double]("1.1,2.1,3.1") == List(Right(1.1), Right(2.1), Right(3.1)))
    * assert(fromCsvL[Double]("1.1,abc,3.1") == List(Right(1.1), Left(com.github.gekomad.ittocsv.core.FromCsv.ParseFailure("abc is not Double")), Right(3.1)))
    *}}}
    */
  def fromCsvL[A: ConvertTo](csv: String)(implicit csvFormat: IttoCSVFormat): Seq[Either[ParseFailure, A]] = {
    val x = csv.split(csvFormat.delimeter.toString, -1).toList
    x.map(a => convert[A](a))
  }

  /**
    * @param csvList   is the List[String] to parse
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return `Seq[Either[NonEmptyList[ParseFailure], A]]` based on the parsing of `csvList` any errors are reported
    * {{{
    *  import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    *  import com.github.gekomad.ittocsv.core.FromCsv._
    *  case class Foo(a: Int, b: Double, c: String, d: Boolean)
    *  implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default
    *  val p1 = fromCsv[Foo](List("1,3.14,foo,true", "2,3.14,bar,false"))
    *  assert(p1 == List(Right(Foo(1, 3.14, "foo", true)), Right(Foo(2, 3.14, "bar", false))))
    * }}}
    */
  def fromCsv[A: FieldNames: Schema](csvList: List[String])(implicit csvFormat: IttoCSVFormat): Seq[Either[NonEmptyList[ParseFailure], A]] = {
    if (csvList.isEmpty) Nil
    else
      csvList collect {
        case row if !row.isEmpty || !csvFormat.ignoreEmptyLines =>
          tokenizeCsvLine(row) match {
            case None => Left(NonEmptyList(ParseFailure(s"$csvList is not a valid csv string"), Nil))
            case Some(t) =>
              val schema                 = Schema.of[A]
              val p: Map[String, String] = fieldNames[A].zip(t).toMap
              schema.readFrom(p).toEither
          }
      }
  }
}

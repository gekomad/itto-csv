package com.github.gekomad.ittocsv.parser.files

import java.io.FileNotFoundException

import scala.util.{Failure, Success, Try}

/**
  * Reads a CSV file
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csb/blob/master/README.md]] for more information
  */
object FromFile {

  import cats.data.NonEmptyList
  import com.github.gekomad.ittocsv.core.FromCsv
  import com.github.gekomad.ittocsv.core.FromCsv.Schema
  import com.github.gekomad.ittocsv.core.FromCsv.Convert._
  import com.github.gekomad.ittocsv.core.Header.FieldNames
  import com.github.gekomad.ittocsv.parser.IttoCSVFormat

  /**
    * @param filePath   the file path of file to read
    * @param skipHeader if true doesn't read first row
    * @param csvFormat  the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return `Try[Seq[Either[NonEmptyList[FromCsvImpl.ParseFailure], A]]]`
    */
  def csvFromFile[A: FieldNames : Schema](filePath: String, skipHeader: Boolean)(implicit csvFormat: IttoCSVFormat): Try[Seq[Either[NonEmptyList[FromCsv.ParseFailure], A]]] = {

    import com.github.gekomad.ittocsv.core.FromCsv._

    import com.github.gekomad.ittocsv.core.FromCsv.Convert._
    try {
      val x = scala.io.Source.fromFile(filePath).getLines.map { line =>
        fromCsv[A](line).head
      }.toList
      Success(if (skipHeader) x.drop(1) else x)
    }
    catch {
      case e: FileNotFoundException => Failure(e)
    }
  }

}

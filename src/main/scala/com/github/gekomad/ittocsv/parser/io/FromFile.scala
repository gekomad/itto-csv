package com.github.gekomad.ittocsv.parser.io

import com.github.gekomad.ittocsv.core.ParseFailure

import scala.util.Try

/**
  * Reads a CSV file
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information
  */
object FromFile {

  import cats.data.NonEmptyList
  import com.github.gekomad.ittocsv.core.Schema
  import com.github.gekomad.ittocsv.core.Header.FieldNames
  import com.github.gekomad.ittocsv.parser.IttoCSVFormat

  /**
    * @param filePath   the file path of file to read
    * @param skipHeader if true doesn't read first row
    * @param csvFormat  the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return `Try[Seq[Either[NonEmptyList[FromCsvImpl.ParseFailure], A]]]`
    */
  def csvFromFile[A: FieldNames: Schema](filePath: String, skipHeader: Boolean)(implicit csvFormat: IttoCSVFormat): Try[Seq[Either[NonEmptyList[ParseFailure], A]]] = {

    import com.github.gekomad.ittocsv.core.FromCsv._

    Try {
      val file = scala.io.Source.fromFile(filePath)
      val x    = file.getLines.map(line => fromCsv[A](line).head).toList
      file.close()
      if (skipHeader) x.drop(1) else x
    }
  }

}

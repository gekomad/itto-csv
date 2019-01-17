package com.github.gekomad.ittocsv.parser.io

import com.github.gekomad.ittocsv.core.CsvStringEncoder
import com.github.gekomad.ittocsv.util.ListUtils

import scala.util.Try

/**
  * Write to CSV file
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information
  */
object ToFile {

  import com.github.gekomad.ittocsv.core.Header.FieldNames
  import com.github.gekomad.ittocsv.parser.IttoCSVFormat

  /**
    * @param list      the list to write
    * @param filePath  the file path of file to write
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return the filePath into a `Try[String]`
    */
  def csvToFile[A: FieldNames](list: Seq[A], filePath: String)(implicit csvFormat: IttoCSVFormat, enc: CsvStringEncoder[A]): Try[String] = {
    import com.github.gekomad.ittocsv.core.ToCsv._
    import com.github.gekomad.ittocsv.core.Header._

    val l = csvHeader[A] :: list.map(toCsv(_, printRecordSeparator = true)).toList

    ListUtils.writeFile(l, filePath, false)

  }

}

package com.github.gekomad.ittocsv.parser.io

import cats.effect.{ExitCode, IO}
import com.github.gekomad.ittocsv.core.CsvStringEncoder
import com.github.gekomad.ittocsv.util.ListUtils
import fs2.{Chunk, Pure}

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
    * @return the filePath into a ` fs2.Stream[IO, Unit]`
    */
  def csvToFile[A: FieldNames](
    list: Seq[A],
    filePath: String
  )(implicit csvFormat: IttoCSVFormat, enc: CsvStringEncoder[A]): IO[ExitCode] = {
    import com.github.gekomad.ittocsv.core.ToCsv._
    import com.github.gekomad.ittocsv.core.Header._

    val l: List[String] = csvHeader[A] :: list.map(toCsv(_, printRecordSeparator = true)).toList

    ListUtils.writeFile(l, filePath, false)

  }

  /**
    * @param stream    the stream to write
    * @param filePath  the file path of file to write
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return the filePath into a ` fs2.Stream[IO, Unit]`
    */
  def csvToFileStream[A: FieldNames](
    stream: fs2.Stream[Pure, A],
    filePath: String
  )(implicit csvFormat: IttoCSVFormat, enc: CsvStringEncoder[A]): IO[ExitCode] = {
    import com.github.gekomad.ittocsv.core.ToCsv._
    import com.github.gekomad.ittocsv.core.Header._

    val l: fs2.Stream[Pure, String] = stream.map(toCsv(_, printRecordSeparator = true)).cons(Chunk(csvHeader[A]))

    ListUtils.writeFileStream(l, filePath, false)

  }

}

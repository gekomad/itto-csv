package com.github.gekomad.ittocsv.parser.io

import cats.effect.{ExitCode, IO}
import com.github.gekomad.ittocsv.core.Header.csvHeader
import com.github.gekomad.ittocsv.core.ToCsv._
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import com.github.gekomad.ittocsv.util.ListUtils
import fs2.{Chunk, Pure}

/**
 * Write to CSV file
 *
 * @author
 *   Giuseppe Cannella
 * @since 0.0.1
 * @see
 *   See test code for more information
 * @see
 *   See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information
 */
object ToFile {

  /**
   * @param list
   *   the list to write
   * @param filePath
   *   the file path of file to write
   * @param csvFormat
   *   the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
   * @return
   *   the filePath into a ` fs2.Stream[IO, Unit]`
   */
  def csvToFile[T <: Tuple: RowEncoder](
    list: Seq[T],
    filePath: String,
    head: Option[String]
  )(using csvFormat: IttoCSVFormat): IO[ExitCode] = {

    val l: List[String] = (head match {
      case Some(h) => s"$h${csvFormat.recordSeparator}"
      case None    => ""
    }) :: list.map { y =>
      summon[RowEncoder[T]].encodeRow(y).mkString(csvFormat.delimeter.toString)
    }.toList

    ListUtils.writeFile(l, filePath, false)
  }

  /**
   * @param stream
   *   the stream to write
   * @param filePath
   *   the file path of file to write
   * @param csvFormat
   *   the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
   * @return
   *   the filePath into a ` fs2.Stream[IO, Unit]`
   */
  inline def csvToFileStream[A <: Product](
    s: fs2.Stream[Pure, A],
    filePath: String
  )(using
    m: scala.deriving.Mirror.ProductOf[A],
    e: RowEncoder[m.MirroredElemTypes],
    csvFormat: IttoCSVFormat
  ): IO[ExitCode] = {

    val l: fs2.Stream[Pure, String] = s.map(toCsv(_, printRecordSeparator = true)).cons(Chunk(csvHeader[A]))

    ListUtils.writeFileStream(l, filePath, false)
  }

}

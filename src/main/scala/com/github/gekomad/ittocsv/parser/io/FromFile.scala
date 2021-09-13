package com.github.gekomad.ittocsv.parser.io

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.github.gekomad.ittocsv.core.FromCsv.Decoder
import com.github.gekomad.ittocsv.core.FromCsv.fromCsv
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import fs2.io.file.Files
import fs2.{text, Stream}
import scala.deriving.Mirror
import scala.util.{Failure, Success, Try}

/**
 * Reads a CSV file
 *
 * @author
 *   Giuseppe Cannella
 * @since 1.0.1
 * @see
 *   See test code for more information
 * @see
 *   See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information
 */
object FromFile {

  /**
   * @param filePath
   *   the file path of file to read
   * @param skipHeader
   *   if true doesn't read first row
   * @param csvFormat
   *   the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
   * @return
   *   `Try[List[Either[NonEmptyList[FromCsvImpl.ParseFailure], A]]]`
   */
  def csvFromFileUnsafe[A](filePath: String, skipHeader: Boolean)(using
    m: Mirror.ProductOf[A],
    d: Decoder[List[String], m.MirroredElemTypes],
    csvFormat: IttoCSVFormat
  ): Try[List[Either[List[String], A]]] =
    csvFromFileStream(filePath, skipHeader).compile.toList.attempt.unsafeRunSync() match {
      case Left(e)      => Failure(e)
      case Right(value) => Success(value)
    }

  def csvFromFileStream[A](filePath: String, skipHeader: Boolean)(using
    m: Mirror.ProductOf[A],
    d: Decoder[List[String], m.MirroredElemTypes],
    csvFormat: IttoCSVFormat
  ): Stream[IO, Either[List[String], A]] = {
    val x: Stream[IO, Either[List[String], A]] =
      Files[IO]
        .readAll(fs2.io.file.Path(filePath))
        .through(text.utf8.decode)
        .through(text.lines)
        .map(line => fromCsv[A](line).head)

    if (skipHeader) x.drop(1) else x

  }
}

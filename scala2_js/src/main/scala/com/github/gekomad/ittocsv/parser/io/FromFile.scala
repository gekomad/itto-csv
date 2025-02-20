//package com.github.gekomad.ittocsv.parser.io
//
//import com.github.gekomad.ittocsv.core.FromCsv.fromCsv
//import com.github.gekomad.ittocsv.core.ParseFailure
//import fs2.io.file.Files
//import cats.effect.unsafe.implicits.global
//import scala.util.{Failure, Success, Try}
//
///** Reads a CSV file
//  *
//  * @author
//  *   Giuseppe Cannella
//  * @since 1.0.1
//  * @see
//  *   See test code for more information
//  * @see
//  *   See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information
//  */
//object FromFile {
//
//  import cats.data.NonEmptyList
//  import com.github.gekomad.ittocsv.core.Header.FieldNames
//  import com.github.gekomad.ittocsv.core.Schema
//  import com.github.gekomad.ittocsv.parser.IttoCSVFormat
//
//  /** @param filePath
//    *   the file path of file to read
//    * @param skipHeader
//    *   if true doesn't read first row
//    * @param csvFormat
//    *   the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
//    * @return
//    *   `Try[List[Either[NonEmptyList[FromCsvImpl.ParseFailure], A]]]`
//    */
//  def csvFromFileUnsafe[A: FieldNames: Schema](filePath: String, skipHeader: Boolean)(implicit
//    csvFormat: IttoCSVFormat
//  ): Try[List[Either[NonEmptyList[ParseFailure], A]]] =
//    csvFromFileStream(filePath, skipHeader).compile.toList.attempt.unsafeRunSync() match {
//      case Left(e)      => Failure(e)
//      case Right(value) => Success(value)
//    }
//
//  import java.nio.file.Paths
//
//  import cats.effect.IO
//  import fs2.{Stream, text}
//
//  def csvFromFileStream[A: FieldNames: Schema](filePath: String, skipHeader: Boolean)(implicit
//    csvFormat: IttoCSVFormat
//  ): Stream[IO, Either[NonEmptyList[ParseFailure], A]] = {
//    val x: Stream[IO, Either[NonEmptyList[ParseFailure], A]] =
//      Files[IO]
//        .readAll(fs2.io.file.Path(filePath))
//        .through(text.utf8.decode)
//        .through(text.lines)
//        .map(line => fromCsv[A](line).head)
//
//    if (skipHeader) x.drop(1) else x
//
//  }
//}

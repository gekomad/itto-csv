package com.github.gekomad.ittocsv.util
import scala.util.Try
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import fs2.{Stream, io, text}
import java.nio.file.Paths
import cats.effect.{IO, IOApp}
import fs2.{Stream, text}
import fs2.io.file.Files

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Utils for lists
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  */
object ListUtils {

  /**
    * @param list             the list to write
    * @param filePath         the file path of file to write
    * @param addLineSeparator if true add a line separator, default = true
    * @return the filePath into `Stream[IO, Unit]`
    */
  def writeFile(list: List[String], filePath: String, addLineSeparator: Boolean = true): IO[ExitCode] = {
    val a: Stream[IO, String] = Stream.emits(list)
    val b: Stream[IO, String] = if (addLineSeparator) a.map(_ + System.lineSeparator) else a
    b.through(text.utf8Encode)
      .through(Files[IO].writeAll(Paths.get(filePath)))
      .compile
      .drain
      .as(ExitCode.Success)
  }

  /**
    * @param stream           the stream to write
    * @param filePath         the file path of file to write
    * @param addLineSeparator if true add a line separator, default = true
    * @return the filePath into `Stream[IO, Unit]`
    */
  def writeFileStream(
    stream: fs2.Stream[IO, String],
    filePath: String,
    addLineSeparator: Boolean = true
  ): IO[ExitCode] = {
    val b: Stream[IO, String] = if (addLineSeparator) stream.map(_ + System.lineSeparator) else stream
    b.through(text.utf8Encode)
      .through(Files[IO].writeAll(Paths.get(filePath)))
      .compile
      .drain
      .as(ExitCode.Success)
  }

}

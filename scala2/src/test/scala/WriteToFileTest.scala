import java.time.LocalDateTime
import cats.effect.ExitCode
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import fs2.Pure

import cats.effect.unsafe.implicits.global
import scala.io.Source

class WriteToFileTest extends munit.FunSuite {

  test("write List to file") {
    import java.util.UUID

    final case class Bar(id: UUID, name: String, date: LocalDateTime)
    val filePath = java.nio.file.Files.createTempFile("test", ".csv").toString
    implicit val csvFormat: IttoCSVFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator("\n")

    {
      import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFile

      import com.github.gekomad.ittocsv.core.ToCsv._

      def toLocalDateTime(s: String): LocalDateTime =
        LocalDateTime.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

      val list = List(
        Bar(UUID.fromString("1CC3CCBB-C749-3078-E050-1AACBE064651"), "bob", toLocalDateTime("2018-11-20T09:10:25")),
        Bar(UUID.fromString("3CC3CCBB-C749-3078-E050-1AACBE064653"), "alice", toLocalDateTime("2018-11-20T10:12:24")),
        Bar(UUID.fromString("4CC3CCBB-C749-3078-E050-1AACBE064654"), "jim", toLocalDateTime("2018-11-20T11:18:17")),
        Bar(UUID.fromString("5CC3CCBB-C749-3078-E050-1AACBE064655"), "tom", toLocalDateTime("2018-11-20T11:36:04"))
      )

      csvToFile(list, filePath).attempt.unsafeRunSync() match {
        case Left(value)  => assert(false, value)
        case Right(value) => assert(value == ExitCode.Success)
      }

      val file  = Source.fromFile(filePath)
      val lines = file.getLines().mkString
      file.close()
      assert(
        lines == "id\tname\tdate1cc3ccbb-c749-3078-e050-1aacbe064651\tbob\t2018-11-20T09:10:253cc3ccbb-c749-3078-e050-1aacbe064653\talice\t2018-11-20T10:12:244cc3ccbb-c749-3078-e050-1aacbe064654\tjim\t2018-11-20T11:18:175cc3ccbb-c749-3078-e050-1aacbe064655\ttom\t2018-11-20T11:36:04"
      )
    }
    // read file through stream
    {
      import com.github.gekomad.ittocsv.core.Conversions.fromStringToLocalDateTime
      import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileStream

      val r = csvFromFileStream[Bar](filePath, skipHeader = true)
        .map(csvEither => println(csvEither))
        .compile
        .drain
        .attempt
        .unsafeRunSync() match {
        case Left(e) =>
          println("err " + e)
          false
        case _ => true
      }
      assert(r)
    }
  }

  test("write Stream to file") {
    import java.util.UUID
    final case class Bar(id: UUID, name: String, date: LocalDateTime)

    import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFileStream
    implicit val csvFormat =
      com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator("\n")
    import com.github.gekomad.ittocsv.core.ToCsv._

    def getDate(s: String): LocalDateTime =
      LocalDateTime.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    val stream = fs2.Stream[Pure, Bar](
      Bar(UUID.fromString("1CC3CCBB-C749-3078-E050-1AACBE064651"), "bob", getDate("2018-11-20T09:10:25")),
      Bar(UUID.fromString("3CC3CCBB-C749-3078-E050-1AACBE064653"), "alice", getDate("2018-11-20T10:12:24")),
      Bar(UUID.fromString("4CC3CCBB-C749-3078-E050-1AACBE064654"), "jim", getDate("2018-11-20T11:18:17")),
      Bar(UUID.fromString("5CC3CCBB-C749-3078-E050-1AACBE064655"), "tom", getDate("2018-11-20T11:36:04"))
    )

    val filePath = java.nio.file.Files.createTempFile("test", ".csv").toString

    csvToFileStream(stream, filePath).attempt.unsafeRunSync() match {
      case Left(value)  => assert(false, value)
      case Right(value) => assert(value == ExitCode.Success)
    }

    val file  = Source.fromFile(filePath)
    val lines = file.getLines().mkString
    file.close()
    assert(
      lines == "id\tname\tdate1cc3ccbb-c749-3078-e050-1aacbe064651\tbob\t2018-11-20T09:10:253cc3ccbb-c749-3078-e050-1aacbe064653\talice\t2018-11-20T10:12:244cc3ccbb-c749-3078-e050-1aacbe064654\tjim\t2018-11-20T11:18:175cc3ccbb-c749-3078-e050-1aacbe064655\ttom\t2018-11-20T11:36:04"
    )
  }

}

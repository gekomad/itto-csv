import java.time.LocalDateTime
import org.scalatest.funsuite.AnyFunSuite

import scala.io.Source
import scala.util.{Failure, Success, Try}

class WriteToFileTest extends AnyFunSuite {

  test("write to file") {
    import java.util.UUID
    case class Bar(id: UUID, name: String, date: LocalDateTime)

    import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFile
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator("\n")
    import com.github.gekomad.ittocsv.core.ToCsv._

    def getDate(s: String): LocalDateTime = LocalDateTime.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    val list = List(
      Bar(UUID.fromString("1CC3CCBB-C749-3078-E050-1AACBE064651"), "bob", getDate("2018-11-20T09:10:25")),
      Bar(UUID.fromString("3CC3CCBB-C749-3078-E050-1AACBE064653"), "alice", getDate("2018-11-20T10:12:24")),
      Bar(UUID.fromString("4CC3CCBB-C749-3078-E050-1AACBE064654"), "jim", getDate("2018-11-20T11:18:17")),
      Bar(UUID.fromString("5CC3CCBB-C749-3078-E050-1AACBE064655"), "tom", getDate("2018-11-20T11:36:04"))
    )

    val filePath       = "/tmp/out.csv"
    val a: Try[String] = csvToFile(list, filePath)
    a match {
      case Success(b)     => assert(b == filePath)
      case Failure(value) => assert(false, value)
    }
    val file  = Source.fromFile("/tmp/out.csv")
    val lines = file.getLines.mkString
    file.close()
    assert(
      lines == "id\tname\tdate1cc3ccbb-c749-3078-e050-1aacbe064651\tbob\t2018-11-20T09:10:253cc3ccbb-c749-3078-e050-1aacbe064653\talice\t2018-11-20T10:12:244cc3ccbb-c749-3078-e050-1aacbe064654\tjim\t2018-11-20T11:18:175cc3ccbb-c749-3078-e050-1aacbe064655\ttom\t2018-11-20T11:36:04"
    )

  }
}

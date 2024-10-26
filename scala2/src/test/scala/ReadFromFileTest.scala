import java.time.LocalDateTime
import java.util.UUID

import cats.data.NonEmptyList
import cats.effect.IO
import com.github.gekomad.ittocsv.core.ParseFailure

import cats.effect.unsafe.implicits.global
import scala.util.{Failure, Success, Try}

class ReadFromFileTest extends munit.FunSuite {

  test("read from file") {

    import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileUnsafe
    implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab

    import com.github.gekomad.ittocsv.core.Conversions.fromStringToLocalDateTime

    final case class Bar(id: UUID, name: String, date: LocalDateTime)

    val resList: List[Right[ParseFailure, Bar]] = {
      def getDate(s: String): LocalDateTime =
        LocalDateTime.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

      List(
        Right(Bar(UUID.fromString("1cc3ccbb-c749-3078-e050-1aacbe064651"), "bob", getDate("2018-11-20T09:10:25"))),
        Right(Bar(UUID.fromString("3cc3ccbb-c749-3078-e050-1aacbe064653"), "alice", getDate("2018-11-20T10:12:24"))),
        Right(Bar(UUID.fromString("4cc3ccbb-c749-3078-e050-1aacbe064654"), "jim", getDate("2018-11-20T11:18:17"))),
        Right(Bar(UUID.fromString("5cc3ccbb-c749-3078-e050-1aacbe064655"), "tom", getDate("2018-11-20T11:36:04")))
      )
    }

    {
      val path                                                     = getClass.getResource("/csv_with_header.csv").getPath
      val list: Try[List[Either[NonEmptyList[ParseFailure], Bar]]] = csvFromFileUnsafe[Bar](path, skipHeader = true)
      assert(list.isSuccess && list.get == resList)
    }

    {
      import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileStream
      val path = getClass.getResource("/csv_with_header.csv").getPath
      val list = csvFromFileStream[Bar](path, skipHeader = true).compile.toList.unsafeRunSync()

      assert(list == resList)
    }

    {
      val path = getClass.getResource("/csv_without_header.csv").getPath
      val list = csvFromFileUnsafe[Bar](path, skipHeader = false)
      assert(list.isSuccess && list.get == resList)
    }

    {
      val path = getClass.getResource("/empty_file.csv").getPath
      val list = csvFromFileUnsafe[Bar](path, skipHeader = true)
      assert(list.isSuccess && list.get == Nil)
    }

    {

      val errList: List[Either[NonEmptyList[ParseFailure], Bar]] = {
        def getDate(s: String): LocalDateTime =
          LocalDateTime.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        List(
          Right(Bar(UUID.fromString("1cc3ccbb-c749-3078-e050-1aacbe064651"), "bob", getDate("2018-11-20T09:10:25"))),
          Right(Bar(UUID.fromString("3cc3ccbb-c749-3078-e050-1aacbe064653"), "alice", getDate("2018-11-20T10:12:24"))),
          Left(NonEmptyList(ParseFailure("xxx is not UUID"), Nil)),
          Right(Bar(UUID.fromString("5cc3ccbb-c749-3078-e050-1aacbe064655"), "tom", getDate("2018-11-20T11:36:04")))
        )
      }

      val path = getClass.getResource("/csv_with_error.csv").getPath
      val res  = csvFromFileUnsafe[Bar](path, skipHeader = true)

      assert(res == Success(errList))
      //read from file - unsafe mode
      intercept[Exception] {
        val _l: Seq[Bar] = {
          res match {
            case Failure(a) => throw new Exception(a)
            case Success(a) =>
              a.map {
                case Right(rr) => rr
                case Left(rr)  => throw new Exception(rr.toString())
              }
          }
        }
      }
    }
  }

}

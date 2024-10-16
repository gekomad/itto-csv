import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.github.gekomad.ittocsv.parser.IttoCSVFormat

import java.time.LocalDateTime
import java.util.UUID
import scala.util.{Failure, Success, Try}

class ReadFromFileTest extends munit.FunSuite:

  test("readFromFile") {
    import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileUnsafe

    given IttoCSVFormat = IttoCSVFormat.tab
    final case class Bar(id: UUID, name: String, date: LocalDateTime)

    val resList: List[Right[String, Bar]] = {
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
      val path = getClass.getResource("/csv_with_header.csv").getPath
      val list: Try[List[Either[List[String], Bar]]] = csvFromFileUnsafe[Bar](path, skipHeader = true)
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

      val errList: List[Either[List[String], Bar]] = {
        def getDate(s: String): LocalDateTime =
          LocalDateTime.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        List(
          Right(Bar(UUID.fromString("1cc3ccbb-c749-3078-e050-1aacbe064651"), "bob", getDate("2018-11-20T09:10:25"))),
          Right(Bar(UUID.fromString("3cc3ccbb-c749-3078-e050-1aacbe064653"), "alice", getDate("2018-11-20T10:12:24"))),
          Left(List("xxx value is not valid UUID")),
          Right(Bar(UUID.fromString("5cc3ccbb-c749-3078-e050-1aacbe064655"), "tom", getDate("2018-11-20T11:36:04")))
        )
      }

      val path = getClass.getResource("/csv_with_error.csv").getPath
      val res = csvFromFileUnsafe[Bar](path, skipHeader = true)

      assert(res == Success(errList))
      //read from file - unsafe mode
      val a = Try {
        val _: Seq[Bar] = {
          res match
            case Failure(a) => throw new Exception(a)
            case Success(a) =>
              a.map {
                case Right(rr) => rr
                case Left(rr)  => throw new Exception(rr.mkString)
              }
        }
      }
      a match
        case Failure(a) => assert(a.getMessage == "xxx value is not valid UUID")
        case _          => assert(false)

    }
  }

end ReadFromFileTest

import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import org.scalatest.funsuite.AnyFunSuite

class CsvLineTest extends AnyFunSuite {

  test("csv string to list") {

    implicit val csvFormat: IttoCSVFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
    import com.github.gekomad.ittocsv.util.StringUtils._
    {
      val csvString = """1,"foo,bar",y,"2,e,","2ne","a""bc""z""""

      assert(tokenizeCsvLine(csvString) == Some(List("1", "foo,bar", "y", "2,e,", "2ne", "a\"bc\"z")))
    }

    {
      val csvString = "1,foo"

      assert(tokenizeCsvLine(csvString) == Some(List("1", "foo")))
    }

    {
      val csvString = "1,\"foo"

      assert(tokenizeCsvLine(csvString) == None)
    }
  }

}

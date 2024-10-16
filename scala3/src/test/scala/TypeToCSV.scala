import com.github.gekomad.ittocsv.core.*
import com.github.gekomad.ittocsv.core.ToCsv.*
import com.github.gekomad.ittocsv.parser.IttoCSVFormat

import java.util.UUID

class TypeToCSV extends munit.FunSuite:
  given IttoCSVFormat = IttoCSVFormat.default
  test("CSVtoList1") {

    case class Bar(a: String, b: Int)
    val x = Bar("侍", 42)
    val a = toCsv(x)
    assert(a == "侍,42")
  }

  test("CSVtoList2") {

    case class Bar(a: String, b: UUID)
    val x = Bar("侍", UUID.fromString("889bd28a-00b6-45ab-9481-92060ba1ce7b"))
    val a = toCsv(x)

    assert(a == "侍,889bd28a-00b6-45ab-9481-92060ba1ce7b")
  }
end TypeToCSV

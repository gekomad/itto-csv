import org.junit.{Assert, Test}

import java.util.UUID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.github.gekomad.ittocsv.core.*
import com.github.gekomad.ittocsv.core.ToCsv
import com.github.gekomad.ittocsv.core.ToCsv.*
import com.github.gekomad.ittocsv.parser.IttoCSVFormat

class TypeToCSV {
  given IttoCSVFormat = IttoCSVFormat.default
  @Test def CSVtoList1(): Unit = {

    case class Bar(a: String, b: Int)
    val x = Bar("侍", 42)
    val a = toCsv(x)
    assert(a == "侍,42")
  }

  @Test def CSVtoList2(): Unit = {

    case class Bar(a: String, b: UUID)
    val x = Bar("侍", UUID.fromString("889bd28a-00b6-45ab-9481-92060ba1ce7b"))
    val a = toCsv(x)

    assert(a == "侍,889bd28a-00b6-45ab-9481-92060ba1ce7b")
  }
}

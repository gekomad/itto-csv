import com.github.gekomad.ittocsv.core.Header.csvHeader
import com.github.gekomad.ittocsv.core.FromCsv.Decoder
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import org.junit.{Assert, Test}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListToCSV :

  @Test def listToCsv(): Unit = {

    import com.github.gekomad.ittocsv.core.ToCsv._
    given IttoCSVFormat = IttoCSVFormat.default
    import com.github.gekomad.ittocsv.core.Header._
    case class Bar(a: String, b: Int)
    val a = List(Bar("Bar", 42), Bar("Foo", 24))

    val aa = toCsvL(a)

    assert(aa == "a,b\r\nBar,42\r\nFoo,24")
  }

end ListToCSV
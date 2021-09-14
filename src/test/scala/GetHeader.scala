import com.github.gekomad.ittocsv.core.FromCsv.Decoder
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import org.junit.{Assert, Test}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GetHeader :

  @Test def GetHeader(): Unit = {
    case class Foo(i: Int, d: Double, s: Option[String], b: Boolean)
    import com.github.gekomad.ittocsv.core.Header._

    {
      given IttoCSVFormat = IttoCSVFormat.default
      assert(csvHeader[Foo] == "i,d,s,b")
    }

    {
      given IttoCSVFormat = IttoCSVFormat.default.withDelimiter('|').withForceQuote(true)
      assert(csvHeader[Foo] == """"i"|"d"|"s"|"b"""")
    }

  }
end GetHeader

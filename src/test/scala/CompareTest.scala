import com.github.gekomad.ittocsv.parser.Constants.*
import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import org.apache.commons.csv.{CSVFormat, CSVPrinter}
import org.junit.{Assert, Test}

import java.io.StringWriter

class CompareTest:

  @Test def TODO(): Unit = {
    case class Bar(id: String, name: String)
    import com.github.gekomad.ittocsv.core.Header.csvHeader
    import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFile
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    import com.github.gekomad.ittocsv.core.ToCsv._

    given IttoCSVFormat = IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator("\n")
    val filePath: String = "/tmp/out.csv"
    val list = List(Bar("A1", "Jack"), Bar("A2", "Bob"))
    val tuple = list.map(a => Tuple.fromProductTyped(a))
    csvToFile(tuple, filePath, Some(csvHeader[Bar]))
  }
  @Test def CompareWithApacheXommons_csv(): Unit = {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}

    def fildParser(field: String, csvFormat: CSVFormat): String = {
      val csvPrinter = new CSVPrinter(new StringWriter, csvFormat)
      csvPrinter.print(field)
      csvPrinter.getOut.toString
    }

    def doTest(p: IttoCSVFormat, fmt: CSVFormat) = {
      def compare(s: String) = {
        given IttoCSVFormat = p
        val ittoScala = StringToCsvField.stringToCsvField(s)
        val apache = fildParser(s, fmt)
        assert(ittoScala == apache, s"            csvScala: $ittoScala apache: $apache")
      }

      compare("\"\"")
      compare("\"")
      compare("\n")
      compare("\r")
      compare("\r\n")
      compare(",")
      compare("\",\"")
      compare("\"a\"")
      compare(";")
      compare("")
      compare(" ")
      compare("aaa")
      compare("aa\na")
      compare("aa\"b")
      compare("aa\"\"b")
      compare("aa,a")
      compare("aa;a")
      compare("aa,;a")
      compare("aa,\"b")
      compare("aa,\"b")
      compare("aa;\"b")
      compare("aa;\"b")
      compare("aa;\tb")
      compare("8 ")
      compare("a.f")
      compare("#")
      compare("*\u0000!")
      compare("\u0000")
      compare("\u0000 ")
      compare("#\u0000 ")
      compare("\u0000*")
      compare("\u0082")
      compare("么ꄍ횊獎更ἆ쌀ᯞ腒䭗䘍뚞瘅鰅몐説ₒᤑ犫槅䪇꣫힆⏽蜁ᅚ竁퇈醮듑㧖\u0018")
    }

    val csvFormats = List(
      (IttoCSVFormat.default.withQuoteEmpty(true).withQuoteLowerChar(true), CSVFormat.DEFAULT),
      (IttoCSVFormat.tab.withQuoteEmpty(true).withQuoteLowerChar(true), CSVFormat.TDF)
    )
    val delimiters = List(COMMA, SEMICOLON, PIPE)
    val recordSeparators = List(LF, CRLF)
    val quotes = List(PIPE, DOUBLE_QUOTE)

    csvFormats.foreach(f => doTest(f._1, f._2))

    for {
      (format1, format2) <- csvFormats
      delimiter <- delimiters
      recordSeparator <- recordSeparators
      quote <- quotes
      if quote != delimiter
    } yield doTest(
      format1.withDelimiter(delimiter).withRecordSeparator(recordSeparator).withQuote(quote),
      format2.withDelimiter(delimiter).withRecordSeparator(recordSeparator).withQuote(quote)
    )

  }
end CompareTest

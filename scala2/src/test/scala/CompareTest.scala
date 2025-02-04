import org.apache.commons.csv.CSVFormat

import com.github.gekomad.ittocsv.parser.Constants._

class CompareTest extends munit.FunSuite {

  test("Compare with Apache commons-csv") {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    def doTest(p: IttoCSVFormat, f: CSVFormat) = {
      def compare(s: String) = {

        val ittoScala = StringToCsvField.stringToCsvField(s)(p)
        val apache    = ApacheCommonCsvHelper.fildParser(s)(f)
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
    val delimiters       = List(COMMA, SEMICOLON, PIPE)
    val recordSeparators = List(LF, CRLF)
    val quotes           = List(PIPE, DOUBLE_QUOTE)

    csvFormats.foreach(f => doTest(f._1, f._2))

    for {
      (format1, format2) <- csvFormats
      delimiter          <- delimiters
      recordSeparator    <- recordSeparators
      quote              <- quotes
      if quote != delimiter
    } yield doTest(
      format1.withDelimiter(delimiter).withRecordSeparator(recordSeparator).withQuote(quote),
      format2.builder().setDelimiter(delimiter).setRecordSeparator(recordSeparator).setQuote(quote).get()
    )

  }
}

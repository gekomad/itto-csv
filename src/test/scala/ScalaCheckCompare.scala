import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
import org.apache.commons.csv.CSVFormat
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}

/**
  * compare with apache common-csv
  */
object ScalaCheckCompare extends Properties("Scalacheck - Compare with apache common-csv") {

  import org.scalacheck.Prop.forAll
  import com.github.gekomad.ittocsv.parser.Constants._

  val generators       = List(Gen.asciiPrintableStr, Gen.asciiStr, Arbitrary.arbitrary[String])
  val csvFormats       = List((IttoCSVFormat.default.withQuoteEmpty(true).withQuoteLowerChar(true), CSVFormat.DEFAULT), (IttoCSVFormat.tab.withQuoteEmpty(true).withQuoteLowerChar(true), CSVFormat.TDF))
  val delimiters       = List(COMMA, SEMICOLON, PIPE)
  val recordSeparators = List(LF, CRLF)
  val quotes           = List(PIPE, DOUBLE_QUOTE)

  def stringToCsvFieldTest(p: IttoCSVFormat, f: CSVFormat, gen: Gen[String]): Prop = forAll(gen) { l =>
    StringToCsvField.stringToCsvField(l)(p) == ApacheCommonCsvHelper.fildParser(l)(f)
  }

  for {
    generator          <- generators
    (format1, format2) <- csvFormats
  } yield stringToCsvFieldTest(format1, format2, generator)

  for {
    generator          <- generators
    (format1, format2) <- csvFormats
    delimiter          <- delimiters
    recordSeparator    <- recordSeparators
    quote              <- quotes
    if quote != delimiter
  } yield
    property("stringToCsvFieldTest") = stringToCsvFieldTest(
      format1.withDelimiter(delimiter).withRecordSeparator(recordSeparator).withQuote(quote),
      format2.withDelimiter(delimiter).withRecordSeparator(recordSeparator).withQuote(quote),
      generator
    )
}

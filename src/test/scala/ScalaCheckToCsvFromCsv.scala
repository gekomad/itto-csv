import com.github.gekomad.ittocsv.parser.Constants._
import com.github.gekomad.ittocsv.parser.CsvFieldToString._
import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}

/**
  * from string to csv and to string again, the strings should be the same
  */
object ScalaCheckToCsvFromCsv extends Properties("Scalacheck - from string to csv and to string again") {

  import org.scalacheck.Prop.forAll

  val generators = List(Gen.asciiPrintableStr, Gen.asciiStr, Arbitrary.arbitrary[String])
  val csvFormats = List(IttoCSVFormat.default.withQuoteLowerChar(true), IttoCSVFormat.tab.withQuoteLowerChar(true))

  val delimiters       = List(COMMA, SEMICOLON, PIPE)
  val recordSeparators = List(LF, CRLF)
  val quotes           = List(PIPE, DOUBLE_QUOTE)

  def doubleTrasformation(format: IttoCSVFormat, gen: Gen[String]): Prop = forAll(gen) { orig =>
    val csv    = StringToCsvField.stringToCsvField(orig)(format)
    val string = csvFieldToString(csv)(format)
    orig == string
  }

  for {
    generator <- generators
    format1   <- csvFormats
  } yield doubleTrasformation(format1, generator)

  for {
    generator       <- generators
    format1         <- csvFormats
    delimiter       <- delimiters
    recordSeparator <- recordSeparators
    quote           <- quotes
    if quote != delimiter
  } yield
    property("csvFieldToString") = doubleTrasformation(
      format1.withDelimiter(delimiter).withRecordSeparator(recordSeparator).withQuote(quote),
      generator
    )

}

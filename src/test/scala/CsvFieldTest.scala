import org.scalatest.FunSuite

class CsvFieldTest extends FunSuite {
  test("stringToCsvField trim") {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default.withTrim(true)

    assert(StringToCsvField.stringToCsvField("a ") == "a")
    assert(StringToCsvField.stringToCsvField(" a ") == "a")
    assert(StringToCsvField.stringToCsvField("  ") == "")
  }

  test("stringToCsvField force quote") {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default.withForceQuote(true)

    assert(StringToCsvField.stringToCsvField("\"") == "\"\"\"\"")
    assert(StringToCsvField.stringToCsvField(",") == "\",\"")
    assert(StringToCsvField.stringToCsvField("\",\"") == "\"\"\",\"\"\"")
    assert(StringToCsvField.stringToCsvField("\"a\"") == "\"\"\"a\"\"\"")
    assert(StringToCsvField.stringToCsvField("") == "\"\"")
    assert(StringToCsvField.stringToCsvField("aa") == "\"aa\"")
    assert(StringToCsvField.stringToCsvField(" ") == "\" \"")
    assert(StringToCsvField.stringToCsvField("aaa") == "\"aaa\"")
    assert(StringToCsvField.stringToCsvField("aa\na") == "\"aa\na\"")
    assert(StringToCsvField.stringToCsvField("aa\"b") == "\"aa\"\"b\"")
    assert(StringToCsvField.stringToCsvField("aa\"\"b") == "\"aa\"\"\"\"b\"")
    assert(StringToCsvField.stringToCsvField("aa,a") == "\"aa,a\"")
    assert(StringToCsvField.stringToCsvField("aa,\"b") == "\"aa,\"\"b\"")
    assert(StringToCsvField.stringToCsvField("aa,\"b") == "\"aa,\"\"b\"")
  }

  test("stringToCsvField 0") {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default.withQuoteLowerChar(true)
    assert(StringToCsvField.stringToCsvField(" ") == "\" \"")
  }

  test("stringToCsvField 1") {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default.withQuoteLowerChar(true)

    assert(StringToCsvField.stringToCsvField("\"") == "\"\"\"\"")
    assert(StringToCsvField.stringToCsvField(",") == "\",\"")
    assert(StringToCsvField.stringToCsvField("\",\"") == "\"\"\",\"\"\"")
    assert(StringToCsvField.stringToCsvField("\"a\"") == "\"\"\"a\"\"\"")
    assert(StringToCsvField.stringToCsvField("") == "")
    assert(StringToCsvField.stringToCsvField(" ") == "\" \"")
    assert(StringToCsvField.stringToCsvField("aaa") == "aaa")
    assert(StringToCsvField.stringToCsvField("aa\na") == "\"aa\na\"")
    assert(StringToCsvField.stringToCsvField("aa\"b") == "\"aa\"\"b\"")
    assert(StringToCsvField.stringToCsvField("aa\"\"b") == "\"aa\"\"\"\"b\"")
    assert(StringToCsvField.stringToCsvField("aa,a") == "\"aa,a\"")
    assert(StringToCsvField.stringToCsvField("aa,\"b") == "\"aa,\"\"b\"")
    assert(StringToCsvField.stringToCsvField("aa,\"b") == "\"aa,\"\"b\"")
  }

  test("stringToCsvField 2") {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default.withQuoteEmpty(true).withQuoteLowerChar(true)

    assert(StringToCsvField.stringToCsvField("\"") == "\"\"\"\"")
    assert(StringToCsvField.stringToCsvField(",") == "\",\"")
    assert(StringToCsvField.stringToCsvField("\",\"") == "\"\"\",\"\"\"")
    assert(StringToCsvField.stringToCsvField("\"a\"") == "\"\"\"a\"\"\"")
    assert(StringToCsvField.stringToCsvField("") == "\"\"")
    assert(StringToCsvField.stringToCsvField(" ") == "\" \"")
    assert(StringToCsvField.stringToCsvField("aaa") == "aaa")
    assert(StringToCsvField.stringToCsvField("aa\na") == "\"aa\na\"")
    assert(StringToCsvField.stringToCsvField("aa\"b") == "\"aa\"\"b\"")
    assert(StringToCsvField.stringToCsvField("aa\"\"b") == "\"aa\"\"\"\"b\"")
    assert(StringToCsvField.stringToCsvField("aa,a") == "\"aa,a\"")
    assert(StringToCsvField.stringToCsvField("aa,\"b") == "\"aa,\"\"b\"")
    assert(StringToCsvField.stringToCsvField("aa,\"b") == "\"aa,\"\"b\"")
  }

  test("csvFieldToString") {
    import com.github.gekomad.ittocsv.parser.CsvFieldToString.csvFieldToString
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default.withQuoteEmpty(true)

    assert(csvFieldToString("\"\"\",\"\"\"") == "\",\"")
    assert(csvFieldToString("\"aa\na\"") == "aa\na")
    assert(csvFieldToString("\"\"\"\"\"\"") == "\"\"")

    assert(csvFieldToString("\"\"\"\"") == "\"")
    assert(csvFieldToString("\",\"") == ",")
    assert(csvFieldToString("\"\"") == "")
    assert(csvFieldToString("\"\"\"a\"\"\"") == "\"a\"")

    assert(csvFieldToString("\" \"") == " ")
    assert(csvFieldToString("aaa") == "aaa")

    assert(csvFieldToString("\"aa\"\"b\"") == "aa\"b")
    assert(csvFieldToString("\"aa\"\"\"\"b\"") == "aa\"\"b")
    assert(csvFieldToString("\"aa,a\"") == "aa,a")
    assert(csvFieldToString("\"aa,\"\"b\"") == "aa,\"b")
    assert(csvFieldToString("\"aa,\"\"b\"") == "aa,\"b")
  }

  test("both 1") {
    import com.github.gekomad.ittocsv.parser.CsvFieldToString.csvFieldToString
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default.withDelimiter('A')
    val orig                              = "aAc"
    val csv                               = StringToCsvField.stringToCsvField(orig)
    val string                            = csvFieldToString(csv)
    assert(orig == string)
  }
}

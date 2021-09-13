import org.junit.Test

class CsvFieldTest {

  @Test def stringToCsvFieldTrim(): Unit = {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    given IttoCSVFormat = IttoCSVFormat.default.withTrim(true)

    assert(StringToCsvField.stringToCsvField("a ") == "a")
    assert(StringToCsvField.stringToCsvField(" a ") == "a")
    assert(StringToCsvField.stringToCsvField("  ") == "")
  }

  @Test def stringToCsvFieldForceQuote(): Unit = {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    given IttoCSVFormat = IttoCSVFormat.default.withForceQuote(true)

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

  @Test def stringToCsvField0(): Unit = {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    given IttoCSVFormat = IttoCSVFormat.default.withQuoteLowerChar(true)
    assert(StringToCsvField.stringToCsvField(" ") == "\" \"")
  }

  @Test def stringToCsvField1(): Unit = {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    given IttoCSVFormat = IttoCSVFormat.default.withQuoteLowerChar(true)

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

  @Test def stringToCsvField2(): Unit = {
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    given IttoCSVFormat = IttoCSVFormat.default.withQuoteEmpty(true).withQuoteLowerChar(true)

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

  @Test def csvFieldToStringTest(): Unit = {
    import com.github.gekomad.ittocsv.parser.CsvFieldToString.csvFieldToString
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat
    given IttoCSVFormat = IttoCSVFormat.default.withQuoteEmpty(true)

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

  @Test def both1(): Unit = {
    import com.github.gekomad.ittocsv.parser.CsvFieldToString.csvFieldToString
    import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
    given IttoCSVFormat = IttoCSVFormat.default.withDelimiter('A')
    val orig = "aAc"
    val csv = StringToCsvField.stringToCsvField(orig)
    val string = csvFieldToString(csv)
    assert(orig == string)
  }
}

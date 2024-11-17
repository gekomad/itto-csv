import com.github.gekomad.ittocsv.parser.IttoCSVFormat

class ListToCSV extends munit.FunSuite:

  test("listToCsv") {

    import com.github.gekomad.ittocsv.core.ToCsv.*
    given IttoCSVFormat = IttoCSVFormat.default
    import com.github.gekomad.ittocsv.core.Header.*
    case class Bar(a: String, b: Int)
    val a = List(Bar("Bar", 42), Bar("Foo", 24))

    val aa = toCsvL(a)

    assert(aa == "a,b\r\nBar,42\r\nFoo,24")
  }

end ListToCSV

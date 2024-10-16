import com.github.gekomad.ittocsv.parser.IttoCSVFormat

class GetHeader extends munit.FunSuite:

  test("GetHeader") {
    case class Foo(i: Int, d: Double, s: Option[String], b: Boolean)
    import com.github.gekomad.ittocsv.core.Header.*

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

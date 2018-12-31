import org.scalatest.FunSuite

class StringUtilTest extends FunSuite {

  test("split") {
    import com.github.gekomad.ittocsv.util.StringUtils._
    {
      val string     = "a;b;c"
      val separators = List(1, 3)
      val res        = split(string, separators)
      assert(res == List("a", "b", "c"))
    }

    {
      val string     = "a;;c"
      val separators = List(1, 2)
      val res        = split(string, separators)
      assert(res == List("a", "", "c"))
    }

    {
      val string     = "a;b;"
      val separators = List(1, 3)
      val res        = split(string, separators)
      assert(res == List("a", "b", ""))
    }

    {
      val string     = ";;"
      val separators = List(0, 1)
      val res        = split(string, separators)
      assert(res == List("", "", ""))
    }

    {
      val string     = ""
      val separators = List()
      val res        = split(string, separators)
      assert(res == List(""))
    }

    {
      val string     = """1,"foo,bar",y,"2,e,","2ne","a""bc""z""""
      val separators = List(1, 11, 13, 20, 26)
      val res        = split(string, separators)
      assert(res == List("1", "\"foo,bar\"", "y", "\"2,e,\"", "\"2ne\"", "\"a\"\"bc\"\"z\""))
    }

  }

}

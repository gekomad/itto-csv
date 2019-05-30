import com.github.gekomad.ittocsv.core.ParseFailure
import org.scalatest.funsuite.AnyFunSuite

class TreeTest extends AnyFunSuite {

  test("encode/decode Tree[Int]") {
    object OTree {

      //thanks to amitayh https://gist.github.com/amitayh/373f512c50222e15550869e2ff539b25
      case class Tree[A](value: A, left: Option[Tree[A]] = None, right: Option[Tree[A]] = None)

      object Serializer {
        val pattern         = """^(\d+)\((.*)\)$""".r
        val treeOpen        = '('
        val treeClose       = ')'
        val separator       = ','
        val separatorLength = 1

        def serialize[A](nodeOption: Option[Tree[A]]): String = nodeOption match {
          case Some(Tree(value, left, right)) =>
            val leftStr  = serialize(left)
            val rightStr = serialize(right)
            s"$value$treeOpen$leftStr$separator$rightStr$treeClose"

          case None => ""
        }

        def deserialize[A](str: String, f: String => A): Option[Tree[A]] = str match {
          case pattern(value, inner) =>
            val (left, right) = splitInner(inner)
            Some(Tree(f(value), deserialize(left, f), deserialize(right, f)))
          case _ => None
        }

        def splitInner(inner: String): (String, String) = {
          var balance = 0
          val left = inner.takeWhile {
            case `treeOpen`                  => balance += 1; true
            case `treeClose`                 => balance -= 1; true
            case `separator` if balance == 0 => false
            case _                           => true
          }

          val right = inner.drop(left.length + separatorLength)

          (left, right)
        }
      }

    }
    import com.github.gekomad.ittocsv.core.CsvStringEncoder
    import OTree.Serializer._
    import OTree._
    import com.github.gekomad.ittocsv.parser.IttoCSVFormat

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    case class Foo(v: String, a: Tree[Int])

    //encode
    import com.github.gekomad.ittocsv.core.ToCsv._

    implicit def _f(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[Tree[Int]] = createEncoder { node =>
      csvConverter.stringToCsvField(serialize(Some(node)))
    }

    val tree: Tree[Int] = Tree(1, Some(Tree(2, Some(Tree(3)))), Some(Tree(4, Some(Tree(5)), Some(Tree(6)))))

    val serialized: String = toCsv(Foo("abc", tree))

    assert(serialized == "abc,\"1(2(3(,),),4(5(,),6(,)))\"")

    //decode
    import com.github.gekomad.ittocsv.core.FromCsv._
    import com.github.gekomad.ittocsv.core.FromCsv._
    implicit def _l(implicit csvFormat: IttoCSVFormat): String => Either[ParseFailure, Tree[Int]] =
      (str: String) =>
        deserialize(str, _.toInt) match {
          case None    => Left(ParseFailure(s"Not a Node[Short] $str"))
          case Some(a) => Right(a)
      }

    assert(fromCsv[Foo](serialized) == List(Right(Foo("abc", tree))))

  }
}

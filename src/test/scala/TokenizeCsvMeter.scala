import com.github.gekomad.ittocsv.parser.IttoCSVFormat
import org.scalacheck.Gen
import org.scalameter.{Key, Warmer, config}
import org.scalatest.FunSuite

class TokenizeCsvMeter extends FunSuite {

  ignore("tokenizeCsvMeter") {

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default
    import com.github.gekomad.ittocsv.util.StringUtils._
    val standardConfig = config(
      Key.exec.minWarmupRuns -> 20,
      Key.exec.maxWarmupRuns -> 100000,
      Key.exec.benchRuns -> 1000,
      Key.verbose -> false
    ) withWarmer new Warmer.Default

    val asciiStringGen = Gen.asciiPrintableStr.map(_.mkString.take(40))
    val l = Gen.listOfN(10000, asciiStringGen).sample.get


    val time = standardConfig measure {
      l.foreach { s =>
        tokenizeCsvLine(s)
      }
    }
    printf(f"** tokenizeCsvMeter time: ${time.value}%1.2f **\n")

  }
}

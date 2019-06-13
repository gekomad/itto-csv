//import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
//import org.apache.commons.csv.CSVFormat
//import org.scalacheck.Gen
//import org.scalameter.{Key, Warmer, config}
//import org.scalatest.FunSuite
//
//class StringToCsvFieldMeter extends FunSuite { TODO
//
//  ignore("StringToCsvFieldMeter") {
//
//    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default
//
//    val standardConfig = config(Key.exec.minWarmupRuns -> 20, Key.exec.maxWarmupRuns -> 100000, Key.exec.benchRuns -> 200, Key.verbose -> false) withWarmer new Warmer.Default
//
//    val asciiStringGen = Gen.asciiPrintableStr.map(_.mkString.take(20))
//    val l              = Gen.listOfN(100000, asciiStringGen).sample.get
//
//    {
//      implicit val _a = CSVFormat.DEFAULT
//      val time = standardConfig measure {
//        l.foreach(ApacheCommonCsvHelper.fildParser)
//      }
//      printf(f"** ScalameterApache time: ${time.value}%1.2f **\n")
//    }
//
//    {
//      val time = standardConfig measure {
//        l.foreach(StringToCsvField.stringToCsvField)
//      }
//      printf(f"** Scalameter time: ${time.value}%1.2f **\n")
//    }
//  }
//
//}

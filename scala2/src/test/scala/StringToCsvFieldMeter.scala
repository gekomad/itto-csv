import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}
import org.apache.commons.csv.CSVFormat
import org.scalacheck.Gen
import org.scalameter.{config, Key, KeyValue, Warmer}

class StringToCsvFieldMeter extends munit.FunSuite {

  test("StringToCsvFieldMeter") {

    implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default

    val standardConfig = config(
      KeyValue((Key.exec.minWarmupRuns, 20)),
      KeyValue((Key.exec.maxWarmupRuns, 100000)),
      KeyValue((Key.exec.benchRuns, 200)),
      KeyValue((Key.verbose, false))
    ).withWarmer(new Warmer.Default)

    val asciiStringGen = Gen.asciiPrintableStr.map(_.mkString.take(20))
    val l              = Gen.listOfN(100, asciiStringGen).sample.get

    val apacheTime = {
      implicit val _a = CSVFormat.DEFAULT
      standardConfig.measure(l.foreach(ApacheCommonCsvHelper.fildParser))
    }

    {
      val ittoTime = standardConfig.measure(l.foreach(StringToCsvField.stringToCsvField))
      printf(f"** itto-csv time: ${ittoTime.value}%1.2f apache-csv time: ${apacheTime.value}%1.2f**\n")
    }
  }

}

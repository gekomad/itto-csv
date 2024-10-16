import java.io.StringWriter

import org.apache.commons.csv.{CSVFormat, CSVPrinter}

object ApacheCommonCsvHelper {

  def fildParser(field: String)(implicit csvFormat: CSVFormat): String = {
    val csvPrinter = new CSVPrinter(new StringWriter, csvFormat)
    csvPrinter.print(field)
    csvPrinter.getOut.toString
  }
}

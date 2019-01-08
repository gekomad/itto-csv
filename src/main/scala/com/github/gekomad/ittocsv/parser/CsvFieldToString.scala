package com.github.gekomad.ittocsv.parser

/**
  * Trasforms a single CSV field to string
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information.
  */
object CsvFieldToString {

  /**
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @return trims the string according to csvFormat
    */
  def trim(field: String)(implicit csvFormat: IttoCSVFormat): String = if (csvFormat.trim) field.trim else field

  /**
    * @return trasforms a CSV field to string
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @param field     the string to trasform
    * {{{
    * csvFieldToString("\"\"\",\"\"\"")   //  "\",\""
    * csvFieldToString("\"aa\na\"")       //  "aa\na"
    * csvFieldToString("\"\"\"\"\"\"")    //  "\"\""
    * csvFieldToString("\"\"\"\"")        //  "\""
    * csvFieldToString("\",\"")           //  ","
    * csvFieldToString("\"\"")            //  ""
    * csvFieldToString("\"\"\"a\"\"\"")   //  "\"a\""
    * csvFieldToString("\" \"")           //  " "
    * csvFieldToString("aaa")             //  "aaa"
    * csvFieldToString("\"aa\"\"b\"")     //  "aa\"b"
    * csvFieldToString("\"aa\"\"\"\"b\"") //  "aa\"\"b"
    * csvFieldToString("\"aa,a\"")        //  "aa,a"
    * csvFieldToString("\"aa,\"\"b\"")    //  "aa,\"b"
    * csvFieldToString("\"aa,\"\"b\"")    //  "aa,\"b"
    * }}}
    *
    */
  def csvFieldToString(field: String)(implicit csvFormat: IttoCSVFormat): String = {

    /*
     * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
     * @param field     the string to trasform
     * {{{
     *  "aaaa,bbbb" => aaaa,bbbb
     *  "aaaa\nbbbb" => aaaa\nbbbb
     *  "" => { }
     *  "aaaabbbb " => {aaaabbbb }
     *  "#aaaabbbb" => #aaaabbbb
     * }}}
     *
     */
    def parseBorders(a: String)(implicit csvFormat: IttoCSVFormat): String = {
      val Q = csvFormat.quote.toString
      if (a.length > 1 && a.startsWith(Q) && a.endsWith(Q)) a.init.drop(1) else a
    }

    /*
     * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
     * @param field     the string to trasform
     * {{{
     * "aaaa""bbbb" => aaaa"bbbb
     * "aaaa""""bbbb" => aaaa""bbbb
     * }}}
     *
     */
    def parseQuote(a: String)(implicit csvFormat: IttoCSVFormat): String =
      a.replace(s"${csvFormat.quote}${csvFormat.quote}", s"${csvFormat.quote}")

    parseQuote(parseBorders(trim(field)))
  }
}

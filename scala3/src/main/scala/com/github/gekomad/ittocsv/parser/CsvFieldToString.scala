package com.github.gekomad.ittocsv.parser

/**
 * Trasforms a single CSV field to string
 *
 * @author
 *   Giuseppe Cannella
 * @since 0.0.1
 */
object CsvFieldToString:

  /**
   * @param csvFormat
   *   the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
   * @return
   *   trims the string according to csvFormat
   */
  private def trim(field: String)(using csvFormat: IttoCSVFormat): String = if (csvFormat.trim) field.trim else field

  /**
   * @return
   *   trasforms a CSV field to string
   * @param csvFormat
   *   the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
   * @param field
   *   the string to trasform
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
   */
  def csvFieldToString(field: String)(using csvFormat: IttoCSVFormat): String = {

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
    def parseBorders(a: String)(using csvFormat: IttoCSVFormat): String = {
      val q = csvFormat.quote.toString
      if (a.length > 1 && a.startsWith(q) && a.endsWith(q)) a.init.drop(1) else a
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
    def parseQuote(a: String)(using csvFormat: IttoCSVFormat): String =
      a.replace(s"${csvFormat.quote}${csvFormat.quote}", s"${csvFormat.quote}")

    parseQuote(parseBorders(trim(field)))
  }
end CsvFieldToString

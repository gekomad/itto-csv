package com.github.gekomad.ittocsv.parser

import Constants._

/**
  * Trasforms a string to CSV field
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information
  */
object StringToCsvField {

  /**
    * @return trasforms a string to CSV field
    * @param csvFormat the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
    * @param field     the string to trasform
    * {{{
    * stringToCsvField("\"")      // "\"\"\"\""
    * stringToCsvField(",")       // "\",\""
    * stringToCsvField("\",\"")   // "\"\"\",\"\"\""
    * stringToCsvField("\"a\"")   // "\"\"\"a\"\"\""
    * stringToCsvField("")        // "\"\""
    * stringToCsvField("aa")      // "\"aa\""
    * stringToCsvField(" ")       // "\" \""
    * stringToCsvField("aaa")     // "\"aaa\""
    * stringToCsvField("aa\na")   // "\"aa\na\""
    * stringToCsvField("aa\"b")   // "\"aa\"\"b\""
    * stringToCsvField("aa\"\"b") // "\"aa\"\"\"\"b\""
    * stringToCsvField("aa,a")    // "\"aa,a\""
    * stringToCsvField("aa,\"b")  // "\"aa,\"\"b\""
    * stringToCsvField("aa,\"b")  // "\"aa,\"\"b\""
    * }}}
    *
    */
  def stringToCsvField(field: String)(implicit csvFormat: IttoCSVFormat): String = {

    def trim(s: String): String = if (csvFormat.trim) s.trim else s

    def parseQuote(string: String)(implicit csvFormatter: IttoCSVFormat): String = {

      val q = csvFormat.quote
      if (string == s"$q$q") s"$q$q$q$q$q$q"
      else if (string == s"$q") s"$q$q$q$q"
      else
        string match {
          case "" => if (csvFormatter.quoteEmpty || csvFormatter.forceQuote) s"$q$q" else string
          case s =>
            var c             = 0
            var containsQuote = false

            do {
              if (s(c) == q || s(c) == csvFormat.delimeter || s(c) == CR_char || s(c) == LF_char) containsQuote = true
              c = c + 1
            } while (!containsQuote && c < s.length)

            val p = if (containsQuote) s.replace(csvFormat.quote.toString, s"$q$q") else s

            if (containsQuote || csvFormat.forceQuote || csvFormat.quoteLowerChar && (s(s.length - 1) <= SP || s(0) <= COMMENT))
              csvFormat.quote + p + csvFormat.quote
            else p
        }
    }

    parseQuote(trim(field))
  }
}

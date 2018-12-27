package com.github.gekomad.ittocsv.parser

import Constants._

/**
  * Trasforms a string to CSV field
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/itto-csb/blob/master/README.md]] for more information
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
      if (string == s"$q$q") s"$q$q$q$q$q$q" else if (string == s"$q") s"$q$q$q$q" else
        string match {
          case "" => if (csvFormatter.quoteEmpty || csvFormatter.forceQuote) s"$q$q" else string
          case s =>

            var c = 0
            var containsQuote = false

            do {
              if (s(c) == q || s(c) == csvFormat.delimeter || s(c) == CR_char || s(c) == LF_char) containsQuote = true
              c = c + 1
            } while (!containsQuote && c < s.length)

            val p = if (containsQuote) s.replace(csvFormat.quote.toString, s"$q$q") else s

            if (containsQuote || csvFormat.forceQuote || s(s.length - 1) <= SP || s(0) <= COMMENT)
              csvFormat.quote + p + csvFormat.quote else p
        }
    }

    parseQuote(trim(field))
  }
  /* TODO
  def stringToCsvFieldOrig(field: String)(implicit csvFormat: IttoCSVFormat): String = {

   def trim(s: String): String = if (csvFormat.trim) s.trim else s

   // aaaa,bbbb => "aaaa,bbbb"
   def parseSeparator(implicit parameter: IttoCSVFormat): State[(String, ForceQuote)] = a =>
     if (a._2 || csvFormat.forceQuote)
       a.copy(_2 = true)
     else if (a._1.contains(csvFormat.delimeter)) {
     a.copy(_2 = true)
   } else a

   // aaaa"bbbb => "aaaa""bbbb"
   // aaaa""bbbb => "aaaa""""bbbb"
   def parseQuote(implicit parameter: IttoCSVFormat): State[(String, ForceQuote)] = a =>
     if (a._1.contains(csvFormat.quote)) {
       (a._1.replace(csvFormat.quote.toString, s"${csvFormat.quote}${csvFormat.quote}"), true)
     } else a

   // aaaa\nbbbb => "aaaa\nbbbb"
   def parseCR: State[(String, ForceQuote)] = a => if (a._2 || csvFormat.forceQuote) a.copy(_2 = true) else if (a._1.contains(LF) || a._1.contains(CR)) {
     a.copy(_2 = true)
   } else a

   // { } => ""
   def parseEmpty: State[(String, ForceQuote)] = a => if (a._2 || csvFormat.forceQuote) a.copy(_2 = true) else if (csvFormat.quoteEmpty && a._1.trim.isEmpty) {
     a.copy(_2 = true)
   } else a

   // {aaaabbbb } => "aaaabbbb "
   def parseLastLessSP: State[(String, ForceQuote)] = a => if (a._2 || csvFormat.forceQuote) a.copy(_2 = true) else if (csvFormat.quoteEmpty) a._1.takeRight(1).toList match {
     case x :: Nil if x <= SP =>
       a.copy(_2 = true)
     case _ => a
   } else a

   // #aaaabbbb => "#aaaabbbb"
   def parseLastLessPound: State[(String, ForceQuote)] = a => if (a._2 || csvFormat.forceQuote) a.copy(_2 = true) else if (csvFormat.quoteEmpty) a._1.take(1).toList match {
     case x :: Nil if x <= COMMENT =>
       a.copy(_2 = true)
     case _ => a
   } else a

   def quote(field: String, addQuote: Boolean): String = if (addQuote || csvFormat.forceQuote)
     csvFormat.quote + field + csvFormat.quote else field

   (quote _).tupled {
     (parseSeparator andThen parseQuote andThen parseCR andThen parseEmpty andThen parseLastLessSP andThen parseLastLessPound) ((trim(field), false))
   }
 }
 * */
}

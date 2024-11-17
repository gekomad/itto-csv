package com.github.gekomad.ittocsv.util

import com.github.gekomad.ittocsv.parser.IttoCSVFormat

import scala.annotation.tailrec

/** Utils for strings
  *
  * @author
  *   Giuseppe Cannella
  * @since 0.0.1
  */
object StringUtils {

  /** @return
    *   splits the string at positions of separators
    * @param string
    *   the string to split
    */
  def split(string: String, separators: List[Int]): List[String] = {

    def sp(s: String, sep: List[Int]): List[String] = sep match {
      case Nil => List(s)

      case x0 :: x1 :: xs =>
        val a = s.substring(x0 + 1, x1)
        val b = sp(s, x1 :: xs)
        a :: b
      case x0 :: _ => List(s.substring(x0 + 1, s.length))
    }

    sp(string, -1 :: separators)
  }

  def getDelimiter(s: String): Char = {
    @tailrec
    def go(s: String, delimiter: Int): Int = if (s.contains(delimiter.toChar)) go(s, delimiter - 1) else delimiter

    go(s, 0xffff).toChar
  }

  /** @return
    *   trasforms a CSV string to List of strings
    * @param csvFormat
    *   the CSV formatter
    * @param csv
    *   the string to trasform
    *   {{{
    * tokenizeCsvLine("""1,"foo,bar",y,"2,e,","2ne","a""bc""z"""") // List("1", "foo,bar", "y", "2,e,", "2ne", "a\"bc\"z"))
    * tokenizeCsvLine("1,foo") //   Some(List("1", "foo"))
    * tokenizeCsvLine("1,\"foo") //  None
    *   }}}
    */
  def tokenizeCsvLine(csv: String)(implicit csvFormat: IttoCSVFormat): Option[List[String]] = csv match {
    case _ if !csv.contains(csvFormat.quote) => Some(csv.split(csvFormat.delimeter.toString, -1).toList)
    case _ if csv.count(_ == csvFormat.quote) % 2 != 0 => None
    case _ =>
      val delimiter = getDelimiter(csv)
      val string    = csv.replace(s"${csvFormat.quote}${csvFormat.quote}", delimiter.toString)

      var inside = false
      val arr    = string.toCharArray
      val commas = scala.collection.mutable.ListBuffer.empty[Int]

      var c = 0
      while (c < arr.length) {
        if (arr(c) == csvFormat.quote) inside = !inside
        else if (!inside && arr(c) == csvFormat.delimeter) commas += c
        c = c + 1
      }

      val l: List[String] = split(arr.mkString, commas.toList)
      val p: List[String] = l.map(_.replace(s"${csvFormat.quote}", "").replace(delimiter, csvFormat.quote))
      Some(p)
  }

}

package com.github.gekomad.ittocsv.core

import com.github.gekomad.ittocsv.parser.{IttoCSVFormat, StringToCsvField}

object Header:

  import scala.compiletime.{constValue, erasedValue}
  import scala.deriving._

  private inline def toNames[T <: Tuple]: List[String] =
    inline erasedValue[T] match {
      case _: (head *: tail) =>
        (inline constValue[head] match {
          case str: String => str
        }) :: toNames[tail]
      case _ => Nil
    }

  inline def fieldNames[P](using mirror: Mirror.Of[P]): List[String] = toNames[mirror.MirroredElemLabels]

  /**
   * @param csvFormat
   *   the [[com.github.gekomad.ittocsv.parser.IttoCSVFormat]] formatter
   * @return
   *   the string with class's fields name encoded according with csvFormat
   */
  inline def csvHeader[T](using mirror: Mirror.Of[T], csvFormat: IttoCSVFormat): String =
    fieldNames[T].map(StringToCsvField.stringToCsvField).mkString(csvFormat.delimeter.toString)

end Header

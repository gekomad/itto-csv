package com.github.gekomad.ittocsv.parser

import Constants._

/**
 * The formatter determines how CSV will be generated, two formatters are available:
 *
 * | Method                         |         Description          | Default Formatter | Tab formatter |
 * |:-------------------------------|:----------------------------:|------------------:|--------------:|
 * | withDelimiter(c: Char)         | the separator between fields |                 , |            \t |
 * | withQuote(c: Char)             |   the quoteChar character    |                 " |             " |
 * | withQuoteEmpty(c: Boolean)     |    quotes field if empty     |             false |         false |
 * | withForceQuote(c: Boolean)     |      quotes all fields       |             false |         false |
 * | withPrintHeader(c: Boolean)    |  if true prints the header   |             false |         false |
 * | withTrim(c: Boolean)           |       trims the field        |             false |         false |
 * | withRecordSeparator(c: String) |      the rows separator      |              \r\n |          \r\n |
 *
 * it's possible to create custom foramtters editing the default ones, example:
 *
 * {{{
 * implicit val newFormatter = default.withForceQuote(true).withRecordSeparator("\n").with.....
 * }}}
 *
 * @author
 *   Giuseppe Cannella
 * @since 0.0.1
 * @param delimeter
 *   the separator between fields
 * @param quote
 *   the quoteChar character
 * @param recordSeparator
 *   the record separator
 * @param quoteEmpty
 *   if true quotes the empty field
 * @param forceQuote
 *   if true quotes all fields
 * @param printHeader
 *   if true prints the header
 * @param trim
 *   if true trims the fields
 * @param ignoreEmptyLines
 *   if true skip empty lines
 * @param quoteLowerChar
 *   if true quotes lower chars
 * @see
 *   See test code for more information
 * @see
 *   See [[https://github.com/gekomad/itto-csv/blob/master/README.md]] for more information.
 */
final case class IttoCSVFormat(
  delimeter: Char,
  quote: Char,
  recordSeparator: String,
  quoteEmpty: Boolean,
  forceQuote: Boolean,
  printHeader: Boolean,
  trim: Boolean,
  ignoreEmptyLines: Boolean,
  quoteLowerChar: Boolean
) {
  def withDelimiter(c: Char): IttoCSVFormat = this.copy(delimeter = c)

  def withQuote(c: Char): IttoCSVFormat = this.copy(quote = c)

  def withQuoteEmpty(c: Boolean): IttoCSVFormat = this.copy(quoteEmpty = c)

  def withForceQuote(c: Boolean): IttoCSVFormat = this.copy(forceQuote = c)

  def withPrintHeader(c: Boolean): IttoCSVFormat = this.copy(printHeader = c)

  def withTrim(c: Boolean): IttoCSVFormat = this.copy(trim = c)

  def withRecordSeparator(c: String): IttoCSVFormat = this.copy(recordSeparator = c)

  def withIgnoreEmptyLines(c: Boolean): IttoCSVFormat = this.copy(ignoreEmptyLines = c)

  def withQuoteLowerChar(c: Boolean): IttoCSVFormat = this.copy(quoteLowerChar = c)
}

object IttoCSVFormat {

  /**
   * | Method                           |                Descrizione                | default |
   * |:---------------------------------|:-----------------------------------------:|--------:|
   * | withDelimiter(c: Char)           |       the separator between fields        |       , |
   * | withQuote(c: Char)               |          the quoteChar character          |       " |
   * | withQuoteEmpty(c: Boolean)       |           quotes field if empty           |   false |
   * | withForceQuote(c: Boolean)       |             quotes all fields             |   false |
   * | withPrintHeader(c: Boolean)      | if true prints the header (method toCsvL) |   false |
   * | withTrim(c: Boolean)             |              trims the field              |   false |
   * | withRecordSeparator(c: String)   |            the rows separator             |    \r\n |
   * | withIgnoreEmptyLines(c: Boolean) |        skips empty lines    false         |         |
   * | withQuoteLowerChar(c: Boolean)   |            quotes lower chars             |   false |
   */
  val default: IttoCSVFormat = IttoCSVFormat(
    quote = DOUBLE_QUOTE,
    delimeter = COMMA,
    recordSeparator = CRLF,
    quoteEmpty = false,
    forceQuote = false,
    printHeader = true,
    trim = false,
    ignoreEmptyLines = false,
    quoteLowerChar = false
  )

  /*
   * | Method   |    Descrizione        | default|
   * |----------|:-------------:|------:|-:|
   * | withDelimiter(c: Char)   |  the separator between fields  |\t|
   * | withQuote(c: Char)        |    the quoteChar character    |"|
   * | withQuoteEmpty(c: Boolean)  | quotes field if empty |false|
   * | withForceQuote(c: Boolean) | quotes all fields  |false|
   * | withPrintHeader(c: Boolean)  | if true prints the header (method toCsvL)  |false|
   * | withTrim(c: Boolean)   | trims the field | false|
   * | withRecordSeparator(c: String) | the rows separator |\r\n|
   * | withIgnoreEmptyLines(c: Boolean) | skips empty lines  |  false |
   * | withQuoteLowerChar(c: Boolean) | quotes lower chars|    false |
   */
  val tab: IttoCSVFormat = IttoCSVFormat(
    quote = DOUBLE_QUOTE,
    delimeter = TAB,
    recordSeparator = CRLF,
    quoteEmpty = false,
    forceQuote = false,
    printHeader = true,
    trim = false,
    ignoreEmptyLines = false,
    quoteLowerChar = false
  )
}

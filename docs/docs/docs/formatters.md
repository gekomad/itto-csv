---
layout: docs
title: Formatters
permalink: docs/
---

### Formatters
The formatter determines how CSV will be generated

```scala
case class IttoCSVFormat(
    delimeter: Char,
    quote: Char,
    recordSeparator: String,
    quoteEmpty: Boolean,
    forceQuote: Boolean,
    printHeader: Boolean,
    trim: Boolean,
    ignoreEmptyLines: Boolean,
    quoteLowerChar: Boolean)
 ```
Two formatters are available:


| Method   |    Description        |  Default Formatter| Tab formatter|
|----------|:-------------:|------:|-:|
| withDelimiter(Char)   |  the separator between fields | , |\t|
| withQuote(Char)        |    the quoteChar character   | " |"|
| withQuoteEmpty(Boolean)  | quotes field if empty |    false |false|
| withForceQuote(Boolean) | quotes all fields |    false |false|
| withPrintHeader(Boolean)  | if true prints the header (method toCsvL) |    false |false|
| withTrim(Boolean)   | trims the field |    false |false|
| withRecordSeparator(String) | the rows separator|    \r\n |\r\n|
| withIgnoreEmptyLines(Boolean) | skips empty lines |   false |false |
| withQuoteLowerChar(Boolean) | quotes lower chars|    false |false |

It's possible to create custom formatters editing the default ones, example:
```scala
given csvFormat: IttoCSVFormat = IttoCSVFormat.default.withForceQuote(true).withRecordSeparator("\n").with.....
```
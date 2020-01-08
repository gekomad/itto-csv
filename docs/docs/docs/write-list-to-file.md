---
layout: docs
title: Write/Read List to file
permalink: docs/write-list-to-file/
---

### Write List to file

```scala
case class Bar(id: String, name: String)

import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFile
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator("\n")
import com.github.gekomad.ittocsv.core.ToCsv._

val list = List(Bar("A1","Jack"),Bar("A2","Bob"))

val filePath: String = ???
csvToFile(list, filePath)
 ```


### Read List from file

```scala
import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileUnsafe
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab

case class Bar(id: String, name: String, date: String)

val path: String = ???
val list = csvFromFileUnsafe[Bar](path, skipHeader = true) // Try[List[Either[NonEmptyList[ParseFailure], Bar]]]
```
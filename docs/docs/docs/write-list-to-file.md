---
layout: docs
title: Write/Read List to file
permalink: docs/write-list-to-file/
---

### Write List to file

```scala
case class Bar(id: String, name: String)
import com.github.gekomad.ittocsv.core.Header.csvHeader
import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFile
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator("\n")
import com.github.gekomad.ittocsv.core.ToCsv._
val filePath: String = "/tmp/out.csv"
val list = List(Bar("A1", "Jack"), Bar("A2", "Bob"))
val tuple = list.map(a => Tuple.fromProductTyped(a))
val a: IO[ExitCode] = csvToFile(tuple, filePath, Some(csvHeader[Bar]))
 ```

### Read List from file

```scala
import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileUnsafe
given IttoCSVFormat = IttoCSVFormat.tab
case class Bar(id: String, name: String, date: String)
val filePath: String = "/tmp/out.csv"
csvFromFileUnsafe[Bar](path, skipHeader = true) // Success(List(Right(Bar(A1,JackA2,Bob))))
```
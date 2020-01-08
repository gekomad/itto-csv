---
layout: docs
title: FS2 stream
permalink: docs/fs2-stream/
---

### Write fs2.Stream to file

```scala
case class Bar(id: String, name: String)

import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFileStream
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator("\n")
import com.github.gekomad.ittocsv.core.ToCsv._

val stream = fs2.Stream(Bar("A1", "Jack"), Bar("A2", "Bob")) // fs2.Stream[Pure, Bar]

val filePath: String = ???
csvToFileStream(stream, filePath).unsafeRunSync()
 ```


### Read fs2.Stream from file

 ```scala
import com.github.gekomad.ittocsv.core.Conversions.fromStringToLocalDateTime
import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileStream

final case class Bar(id: UUID, name: String, date: LocalDateTime)
implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default
csvFromFileStream[Bar](filePath, skipHeader = true)
  .map(csvEither => println(csvEither))
  .compile
  .drain
  .attempt
  .unsafeRunSync() match {
  case Left(e) =>
    println("err " + e)
    false
  case _ => true
}

```
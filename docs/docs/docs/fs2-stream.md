---
layout: docs
title: FS2 stream
permalink: docs/fs2-stream/
---

### Write fs2.Stream to file

```scala
case class Bar(id: String, name: String)

import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFileStream
given csvFormat: IttoCSVFormat =
com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator("\n")
import com.github.gekomad.ittocsv.core.ToCsv._

val stream = fs2.Stream(Bar("A1", "Jack"), Bar("A2", "Bob"))

val filePath: String = "/tmp/a.csv"
val io: IO[ExitCode] = csvToFileStream(stream, filePath)
 ```


### Read fs2.Stream from file

 ```scala
import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileStream
given IttoCSVFormat = IttoCSVFormat.default

val filePath = ???
case class Bar(id: UUID, name: String, date: LocalDateTime)

val s: fs2.Stream[IO, Unit] = csvFromFileStream[Bar](filePath, skipHeader = true)
  .map { csvEither =>
    csvEither match {
      case Left(l)    => ??? // l: List[String]
      case Right(bar) => ??? // Bar
    }
  }

```
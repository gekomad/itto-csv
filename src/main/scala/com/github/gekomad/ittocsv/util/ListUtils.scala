package com.github.gekomad.ittocsv.util
import scala.util.Try

/**
  * Utils for lists
  *
  * @author Giuseppe Cannella
  * @since 0.0.1
  */
object ListUtils {

  /**
    * @param list             the list to write
    * @param filePath         the file path of file to write
    * @param addLineSeparator if true add a line separator, default = true
    * @return the filePath into `Try[String]`
    */
  def writeFile(list: List[String], filePath: String, addLineSeparator: Boolean = true): Try[String] = {
    import java.io.{BufferedWriter, File, FileWriter}

    import scala.util.{Failure, Success}
    val file = new File(filePath)
    var bw: BufferedWriter = null
    try {
      bw = new BufferedWriter(new FileWriter(file))

      if (addLineSeparator)
        list.foreach(a => bw.write(a + System.lineSeparator))
      else
        list.foreach(bw.write)

      Success(filePath)
    }
    catch {
      case e: Throwable => Failure(e)
    }
    finally {
      if (bw != null)
        bw.close()
    }
  }

}

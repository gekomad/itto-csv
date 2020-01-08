object Util {
  def deleteFile(fileName: String): Unit = {
    import scala.reflect.io.File
    val file = File(fileName)
    if (file.isFile && file.exists) {
      file.delete()
    }
  }

}

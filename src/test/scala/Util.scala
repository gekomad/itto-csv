object Util {
  def deleteFile(fileName: String): Unit = {
    import java.io.File
    val file = File(fileName)
    if (file.isFile && file.exists) {
      file.delete()
    }
  }
}

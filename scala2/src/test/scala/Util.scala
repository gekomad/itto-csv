import java.io.File

object Util {
  def deleteFile(fileName: String): Unit = {
    val file = new File(fileName)
    if (file.isFile && file.exists) {
      file.delete()
    }
  }

}

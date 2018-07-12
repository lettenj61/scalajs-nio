package java.io

import java.net.URI
import java.nio.file.Path

import scalajsnio.FileOps
import scalajsnio.JSEnvironment.isNodeJS

/* Basically copied from Scalameta implementation at:
 * https://github.com/scalameta/scalameta/blob/0a7bee1a1e85597f614f86a8a7a80d5641c4d1e0/scalameta/io/js/src/main/scala/java/io/File.scala
 *
 * Also some behaviors are from Apache Harmony
 */

class File(path: String) {
  def this(parent: String, child: String) =
    this(parent + File.separator + child)
  def this(parent: File, child: String) =
    this(parent.getPath, child)
  def this(uri: URI) =
    this(
      if (uri.getScheme != "file") {
        throw new IllegalArgumentException("URI scheme is not \"file\"")
      } else {
        uri.getPath
      }
    )
  def toPath: Path =
    FileOps.platformPathInstance(path)
  def toURI: URI = {
    val file = getAbsoluteFile.toString
    val uripath = if (file.startsWith("/")) file else "/" + file.replace(File.separator, "/")
    val withslash = if (isDirectory && !uripath.endsWith("/")) uripath + "/" else uripath
    new URI("file", null, withslash, null)
  }
  def getAbsoluteFile: File =
    toPath.toAbsolutePath.toFile
  def getAbsolutePath: String =
    getAbsoluteFile.toString
  def getParentFile: File =
    toPath.getParent.toFile
  def mkdir(): Boolean =
    FileOps.mkdir(path)
  def mkdirs(): Boolean =
    FileOps.mkdirs(path)
  def getPath: String =
    path
  def exists(): Boolean =
    FileOps.exists(path)
  def isFile: Boolean =
    FileOps.isFile(path)
  def isDirectory: Boolean =
    FileOps.isDirectory(path)
  override def toString: String =
    path
}

object File {
  def listRoots(): Array[File] = Array(
    new File(FileOps.root)
  )

  def separatorChar: Char =
    separator.charAt(0)

  def separator: String =
    FileOps.separator

  def pathSeparator: String =
    FileOps.delimiter
}
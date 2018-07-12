package java.nio.file

import java.io.File
import java.net.URI

import scalajsnio.FileOps.{ constructPath, platformPathInstance }

/*
 * Cloned from :
 * https://github.com/scalameta/scalameta/blob/0a7bee1a1e85597f614f86a8a7a80d5641c4d1e0/scalameta/io/js/src/main/scala/java/nio/file/Paths.scala
 */

object Paths {
  // NOTE: We can't use Scala-style varargs since those have a different jvm
  // signature than Java-style varargs. The boot classpath contains nio.file.Path
  // so call-sites to `get` will resolve to the original java.nio.file.Paths.get,
  // which results in a Scala.js linking error when using Scala varargs.
  def get(first: String, more: Array[String] = Array.empty): Path =
    platformPathInstance(constructPath(first, more))

  def get(uri: URI): Path = {
    if (uri.getScheme != "file")
      throw new IllegalArgumentException("only file: URIs are supported")

    val path = uri.getPath
    val parts = path.split('/').toList
    val (_, trailing) = parts.span(_ == "")
    trailing match {
      case drive :: _ if (drive.length == 2 && drive(1) == ':') =>
        platformPathInstance(trailing.mkString("\\"))
      case _ =>
        platformPathInstance(path)
    }
  }
}

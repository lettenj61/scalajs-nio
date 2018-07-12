package scalajsnio

import java.io.File
import java.net.URI
import java.nio.file.{ Path, Paths }

import scala.scalajs.js

import scalajsnio.nodejs.{ Path => NodePath, Fs => NodeFs }
import FileOps.{ escapedSeparator, paths }

abstract class AbstractPath extends Path {

  def filename: String
  protected def factory: String => Path

  protected def adjustIndex(index: Int): Int =
    if (isAbsolute) index + 1 else index

  protected def adjustPath(resolved: Path): Path =
    if (isAbsolute) resolved
    else FileOps.workingDirectory.relativize(resolved)

  def isAbsolute: Boolean =
    FileOps.isAbsolutePathLike(filename)

  def getRoot: Path =
    if (!isAbsolute) null
    else factory(FileOps.root)

  def getFileName: Path = {
    val newPath =
      paths(filename).toList match {
        case Nil      => ""
        case x :: Nil => x
        case xs       => xs.last
      }
    if (isAbsolute && newPath == "") null
    else factory(newPath)
  }

  def getParent: Path = {
    if (filename == FileOps.root) null
    else {
      val parts = paths(filename)
      if (parts.length < 2) factory(FileOps.root)
      else factory(parts.init.mkString(FileOps.separator))
    }
  }

  def getNameCount: Int = {
    val stripped =
      if (filename.length > 1 && filename(1) == ':')
        filename.substring(2)
      else filename
    val (fst, remaining) = stripped.split(escapedSeparator + "+").span(_.isEmpty)
    if (remaining.isEmpty) fst.length else remaining.length
  }

  def getName(index: Int): Path =
    factory(
      paths(filename)
        .lift(adjustIndex(index))
        .getOrElse { throw new IllegalArgumentException }
    )

  def subpath(beginIndex: Int, endIndex: Int): Path =
    factory(
      paths(filename)
        .slice(adjustIndex(beginIndex), adjustIndex(endIndex))
        .mkString
    )

  def startsWith(other: Path): Boolean =
    startsWith(other.toString)
  def startsWith(other: String): Boolean =
    paths(filename).startsWith(paths(other))

  def endsWith(other: Path): Boolean =
    endsWith(other.toString)
  def endsWith(other: String): Boolean =
    paths(filename).endsWith(paths(other))

  def normalize(): Path =
    this

  def resolve(other: Path): Path =
    resolve(other.toString)
  def resolve(other: String): Path =
    if (FileOps.isAbsolutePathLike(other)) factory(other)
    else factory(FileOps.constructPath(filename, paths(other)))

  def resolveSibling(other: Path): Path =
    resolveSibling(other.toString)
  def resolveSibling(other: String): Path =
    if (FileOps.isAbsolutePathLike(other) || getParent == null) factory(other)
    else getParent.resolve(other)

  def relativize(other: Path): Path =
    if (isAbsolute != other.isAbsolute) {
      throw new IllegalArgumentException("'other' is different type of Path")
    } else {
      if (this == other) factory("")
      else factory(FileOps.makeRelativePath(filename, other.toString))
    }

  def toUri: URI = toFile.toURI
  def toAbsolutePath: Path =
    if (isAbsolute) this
    else FileOps.workingDirectory.resolve(this)
  def toFile: File = new File(filename)
  override def toString: String =
    filename
}

final case class EmulatedPath(filename: String) extends AbstractPath {
  def factory: String => Path = new EmulatedPath(_)
}

final case class NodeJSPath(filename: String) extends AbstractPath {
  self =>

  def factory: String => Path = new NodeJSPath(_)

  override def isAbsolute(): Boolean =
    NodePath.isAbsolute(filename)
  override def getParent(): Path =
    factory(NodePath.dirname(filename))
  override def getFileName(): Path =
    factory(NodePath.basename(filename))
  override def normalize(): Path =
    factory(NodePath.normalize(filename))
  override def resolve(other: String): Path =
    adjustPath(factory(NodePath.resolve(filename, other)))
  override def resolveSibling(other: String): Path = {
    val newPath = NodePath.resolve(NodePath.dirname(filename), other)
    adjustPath(factory(newPath))
  }
  override def relativize(other: Path): Path =
    factory(NodePath.relative(filename, other.toString))
}

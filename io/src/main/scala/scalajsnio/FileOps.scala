package scalajsnio

import java.io.File
import java.nio.file.Path
import java.util.regex.Pattern

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{ global => g }

import scalajsnio.nodejs.{ Process, Path => NodePath, Fs => NodeFs }
import JSEnvironment.isNodeJS

/**
 * Actual file system operations for JavaScript environment.
 */
object FileOps {
  private[this] lazy val process: Process =
    g.process.asInstanceOf[Process]

  lazy val escapedSeparator: String =
    Pattern.quote(separator())

  lazy val workingDirectory: Path =
    platformPathInstance(cwd())

  def isAbsolutePathLike(path: String): Boolean =
    path.startsWith(FileOps.separator)

  def platformPathInstance(file: String): Path =
    if (isNodeJS) new NodeJSPath(file)
    else new EmulatedPath(file)

  def paths(name: String): Array[String] =
    name.split(escapedSeparator)

  def constructPath(first: String, more: Array[String] = Array.empty): String = {
    if (more.isEmpty) first
    else first + separator + more.mkString(separator)
  }

  def makeRelativePath(from: String, to: String): String = {
    var ups = 0
    val diff = js.Array[String]()
    val these = paths(from)
    val others = paths(to)
    var commonPaths = these.headOption == others.headOption
    these.zipAll(paths(to), "", "").foreach {
      case (l, r) if l == r && commonPaths =>
      case (_, "") => ups += 1
      case ("", r) => diff += r
      case (l, r) =>
        commonPaths = false
        ups += 1
        diff += r
    }
    (Seq.fill(ups)("..") ++ diff).mkString(separator)
  }

  def mkdir(path: String): Boolean =
    isNodeJS && {
      try {
        NodeFs.mkdirSync(path)
        true
      } catch {
        case _: Exception => false
      }
    }

  def mkdirs(path: String): Boolean =
    isNodeJS && {
      var entry = path
      val rootString = root()
      val dirs: js.Array[String] = js.Array()
      while (entry != "" && entry != rootString) {
        dirs.push(entry)
        entry = NodePath.dirname(entry)
      }
      var pass = true
      while (pass && dirs.length > 0) {
        val dir = dirs.pop()
        pass = NodeFs.existsSync(dir) || mkdir(dir)
      }
      pass
    }

  def cwd(): String =
    if (isNodeJS) process.cwd() else "/"

  def root(): String =
    if (isNodeJS) NodePath.parse(NodePath.resolve()).root
    else "/"

  def separator(): String =
    if (isNodeJS) NodePath.sep
    else "/"

  def delimiter(): String =
    if (isNodeJS) NodePath.delimiter
    else ":"

  def exists(path: String): Boolean =
    isNodeJS && NodeFs.existsSync(path)

  def isFile(path: String): Boolean =
    exists(path) && NodeFs.lstatSync(path).isFile()

  def isDirectory(path: String): Boolean =
    exists(path) && NodeFs.lstatSync(path).isDirectory()
}

package scalajsnio
package nodejs

import scala.scalajs.js
import scala.scalajs.js.annotation.{ JSBracketAccess, JSImport }

/* Based on Scalameta implementation at:
 * https://github.com/scalameta/scalameta/blob/9fc15145a5fb98491df382006dc1e04d9e347155/scalameta/io/js/src/main/scala/scala/meta/internal/io/JSIO.scala
 */

@js.native
trait Process extends js.Any {
  def cwd(): String = js.native
}

@js.native
trait Buffer extends js.Object {
  @JSBracketAccess
  def apply(index: Int): Int = js.native
  val length: Int = js.native
}

@js.native
trait Fs extends js.Any {

  /** Returns the file contents as Buffer using blocking apis.
    *
    * NOTE: The actual return value is a Node.js buffer and not js.Array[Int].
    * However, both support .length and angle bracket access (foo[1]).
    **/
  def readFileSync(path: String): Buffer = js.native

  /** Returns the file contents as String using blocking apis */
  def readFileSync(path: String, encoding: String): String = js.native

  /** Writes file contents using blocking apis */
  def writeFileSync(path: String, buffer: js.Array[Int]): Unit = js.native

  /** Returns an array of filenames excluding '.' and '..'. */
  def readdirSync(path: String): js.Array[String] = js.native

  /** Returns an fs.Stats for path. */
  def lstatSync(path: String): Stats = js.native

  /** Returns true if the file exists, false otherwise. */
  def existsSync(path: String): Boolean = js.native

  /** Synchronously creates a directory. */
  def mkdirSync(path: String): Unit = js.native
}

@js.native
@JSImport("fs", JSImport.Namespace, globalFallback = "fs")
object Fs extends Fs

/** Facade for nodejs class fs.Stats.
  *
  * @see https://nodejs.org/api/fs.html#fs_class_fs_stats
  */
@js.native
trait Stats extends js.Any {
  def isFile(): Boolean = js.native
  def isDirectory(): Boolean = js.native
}

/** Facade for path object returned from Node.js `path.parse` API.
  *
  * @see https://nodejs.org/api/path.html
  */
@js.native
trait PathObject extends js.Object {
  def root: String = js.native
  def dir: String = js.native
  def base: String = js.native
  def ext: String = js.native
  def name: String = js.native
}

/** Facade for native nodejs module "path".
  *
  * @see https://nodejs.org/api/path.html
  */
@js.native
trait Path extends js.Any {
  def sep: String = js.native
  def delimiter: String = js.native
  def isAbsolute(path: String): Boolean = js.native
  def parse(path: String): PathObject = js.native
  def resolve(paths: String*): String = js.native
  def normalize(path: String): String = js.native
  def basename(path: String): String = js.native
  def dirname(path: String): String = js.native
  def relative(from: String, to: String): String = js.native
  def join(first: String, more: String*): String = js.native
}

@js.native
@JSImport("path", JSImport.Namespace, globalFallback = "path")
object Path extends Path

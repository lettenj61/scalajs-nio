package scalajsnio

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{ global => g }

object JSEnvironment {
  lazy val isNodeJS: Boolean =
    js.typeOf(g.process) != "undefined" && !js.isUndefined(g.process.cwd)
  
  def runInNode[T](label: String, f: => T): T =
    if (isNodeJS) f
    else {
      throw new IllegalStateException(
        s"Operation $label is not supported in this environment")
    }
}

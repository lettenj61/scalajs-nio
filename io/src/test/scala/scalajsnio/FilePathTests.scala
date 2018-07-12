package scalajsnio

import java.io.File
import java.nio.file.{ Path, Paths }

import scala.scalajs.js
import scala.scalajs.js.annotation._

import utest._

@js.native
@JSImport("os", JSImport.Namespace)
object Os extends js.Object {
  def tmpdir(): String = js.native
}

object FilePathTests extends TestSuite {
  val tests = Tests {
    "Path" - {
      "universal" - {
        val p: Path = Paths.get("/home/scala/workspace")
        "instance" - {
          p
        }
        "API" - {
          "resolve" - {
            p.resolve("bad.js")
          }
          "resolveSibling" - {
            p.resolveSibling("foo-dir")
          }
        }
      }
    }
    "File" - {
      val f: File = new File("/home/scala/somewhere")

      "NodeJS" - {
        val f0 = Paths.get(Os.tmpdir(), ".scalajsnio-test", "deep1").toFile
        "mkdir" - {
          f0.mkdirs() ==> true
        }
      }
    }
  }
}
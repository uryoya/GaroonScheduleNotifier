package gsm

import pureconfig._
import pureconfig.generic.auto._

package object config {
  lazy val config: Config = ConfigSource.default.loadOrThrow[Config]
}

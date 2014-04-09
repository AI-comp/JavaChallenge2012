/**
 * @author Daniel Perez
 */

package net.javachallenge.util.settings

import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner.{ JUnitSuiteRunner, JUnit }
import net.javachallenge.util.internationalization.Locale

@RunWith(classOf[JUnitSuiteRunner])
class EnvironmentSpecTest extends Specification with JUnit {

  "Environment" should {

    doLast {
      Environment.setEnvironment(Defaults.ENV)
    }

    "initialize to default" in {
      Environment.current must_== Defaults.ENV
    }

    "yield proper folder path" in {
      Environment.setEnvironment(Production)
      Environment.current.folder must_== "main"
      Environment.setEnvironment(Test)
      Environment.current.folder must_== "test"
    }
  }
}
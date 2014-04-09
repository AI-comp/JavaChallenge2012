/**
 * @author Daniel Perez
 */

package net.javachallenge.util.settings

import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner.{ JUnitSuiteRunner, JUnit }
import net.javachallenge.util.internationalization.Locale
import net.javachallenge.util.internationalization.I18n

@RunWith(classOf[JUnitSuiteRunner])
class XMLSettingsParserSpecTest extends Specification with JUnit {

  "XMLSettingsParser" should {

    doFirst {
      Environment.setEnvironment(Test)
      Locale.clear
      SettingsLoader.loadSettings
    }

    doLast {
      Environment.setEnvironment(Defaults.ENV)
      Locale.clear
    }

    "load translation settings" in {
      Locale.has("ja") must beTrue
      I18n.get("hello_world") must_== "こんにちは世界。"
    }
  }
}
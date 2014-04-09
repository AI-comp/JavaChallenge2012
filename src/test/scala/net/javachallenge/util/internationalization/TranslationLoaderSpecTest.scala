package net.javachallenge.util.internationalization

import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner.{ JUnitSuiteRunner, JUnit }

@RunWith(classOf[JUnitSuiteRunner])
class TranslationLoaderSpecTest extends Specification with JUnit {

  "Translation loader" should {
    doFirst {
      XMLTranslationLoader.generateLocales("src/test/resources/translations")
      Locale.set("ja")
    }

    "not have inexistent locales" in {
      Locale.has("it") must beFalse
    }

    "have existent locales" in {
      Locale.has("ja") must beTrue
    }

    "not translate inexistent keys" in {
      I18n.get("foo") must_== "foo"
    }

    "translate existent keys" in {
      I18n.get("hello_world") must_== "こんにちは世界。"
    }
  }

}
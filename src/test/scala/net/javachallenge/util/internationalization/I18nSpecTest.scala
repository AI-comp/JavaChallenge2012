package net.javachallenge.util.internationalization

import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner.{ JUnitSuiteRunner, JUnit }

@RunWith(classOf[JUnitSuiteRunner])
class I18nSpecTest extends Specification with JUnit {
  "I18n" should {
    doFirst {
      val helloworld = "hello_world"
      val en = Locale("en", "US")
      val fr = Locale("fr")
      val es = Locale("es")
      en.addWord(helloworld, "Hello World!")
      en.addWord("hi", "Hey!")
      fr.addWord(helloworld, "Bonjour le Monde !")
      es.addWord(helloworld, "¡Hola Mundo!")
    }

    "not respond to unregistered locales" in {
      Locale.has("ja") must beFalse
      Locale.has("it") must beFalse
    }

    "respond to registered locales" in {
      Locale.has("en") must beTrue
      Locale.has("fr") must beTrue
    }

    "return the key when locale is not found" in {
      Locale.set("ja")
      I18n("hello_world").translate must_== "hello_world"
    }

    "return the key when word is not found" in {
      Locale.set("en")
      I18n("foo").translate must_== "foo"
    }

    "translate the word when it is found" in {
      Locale.set("fr")
      I18n("hello_world").translate must_== "Bonjour le Monde !"
    }

    "translate word with fallback if not found in current locale" in {
      Locale.setFallback("en")
      I18n("hi").translate must_== "Hey!"
    }
  }

}

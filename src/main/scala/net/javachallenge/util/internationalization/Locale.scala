/**
 * @author Daniel Perez
 */

package net.javachallenge.util.internationalization

import scala.collection.mutable

/**
 * A locale used in the application
 *
 * @constructor creates a locale with a language and a country
 * @param language the name of the locale's language (eg. 'ja')
 * @param country the name of the locale's country (eg. 'JP')
 */
class Locale(val language: String, val country: String) {
  /**
   * Registers the new locale when created
   */
  Locale.registerLocale(this)

  /**
   * A container for the translations in the locale's language
   */
  val map: mutable.Map[String, String] = mutable.Map()

  /**
   * Returns a readable name for the locale.
   *
   * @return the locale name (eg. 'ja_JP')
   */
  override def toString: String = (language + "_" + country)

  /**
   * Adds a word for translation in the locale's language.
   *
   * @param key the key of the word (eg. 'hello_world')
   * @param word the translation for the key (eg. 'こんにちは世界。')
   */
  def addWord(key: String, word: String) = (map += (key -> word))

  /**
   * Translates the given key in the locale's language or
   * returns the key if the locale is not found.
   *
   * @param key the key to translate
   * @return the translation for the key
   */
  def translate(key: String): String = map get (key) match {
    case None => Locale.fallback match {
      case Some(locale) if locale != this => locale.translate(key)
      case _ => key
    }
    case Some(string) => string
  }
}

/**
 * Helpers for [[net.javachallenge.util.internationalization.Locale]] instances
 */
object Locale {
  /**
   * The current locale for the application.
   */
  var current: Option[Locale] = None

  /**
   * The fallback to use when key not found in current locale.
   */
  var fallback: Option[Locale] = None

  /**
   * A container for the available locales
   * The key used is the language of the locale (eg. 'ja')
   */
  var locales: mutable.Map[String, Locale] = mutable.Map()

  /**
   * Constructs a locale with the given language if it does not already exists.
   * Set the locale's country to the language name in uppercase.
   *
   * @param language the locale's language
   * @return the new locale
   */
  def apply(language: String): Locale = getOrCreate(language, language toUpperCase)

  /**
   * Constructs a locale with the given language if it does not already exists.
   *
   * @param language the locale's language
   * @return the new locale
   */
  def apply(name: String, country: String): Locale = getOrCreate(name, country)

  /**
   * Returns the locale if it exists or create a new one if not.
   *
   * @param language the name of the locale's language (eg. 'ja')
   * @param country the name of the locale's country (eg. 'JP')
   * @return the existent or newly created locale
   */
  protected def getOrCreate(language: String, country: String): Locale = {
    locales.get(language) match {
      case Some(locale) => locale
      case None => new Locale(language, country)
    }
  }

  /**
   * Clear all the infos of the locales
   */
  def clear: Unit = {
    locales.clear
    current = None
    fallback = None
  }

  /**
   * Adds a new locale to the available locales.
   *
   * @param locale the language name for the locale (eg. 'ja')
   */
  def registerLocale(locale: Locale): Unit = (locales += (locale.language -> locale))

  /**
   * Set the current locale for the application.
   *
   * @param locale the language name for the locale (eg. 'ja')
   */
  def set(locale: String): Unit = (current = locales.get(locale))

  /**
   * Set the current fallback for the application.
   *
   * @param locale the language name for the locale (eg. 'ja')
   */
  def setFallback(locale: String): Unit = (fallback = locales.get(locale))

  /**
   * Checks if the locale exists or not.
   *
   * @param locale the language name for the locale (eg. 'ja')
   */
  def has(locale: String): Boolean = locales.isDefinedAt(locale)
}
/**
 * @author Daniel Perez
 */

package net.javachallenge.util.internationalization

/**
 * An internationalized word
 *
 * @constructor creates a word that can be translated
 * @param string representing the key of the word
 */
class I18n(val key: String) {
  /**
   * Translates the word or returns it if no translation is found.
   *
   * @return the translated word
   */
  def translate = Locale.current match {
    case None => key
    case Some(locale) => locale.translate(key)
  }
}

/**
 * Implicit conversion and helpers for
 * [[net.javachallenge.util.internationalization.I18n]] instances.
 */
object I18n {
  def apply(key: String) = new I18n(key)

  /**
   * Translates the given word with the current locale.
   *
   * @param word the word to translate
   * @return the translated word
   */
  def get(word: String) = I18n(word).translate

  implicit def stringToI18n(str: String) = I18n(str)
  implicit def i18nToStr(word: I18n) = word.translate
}
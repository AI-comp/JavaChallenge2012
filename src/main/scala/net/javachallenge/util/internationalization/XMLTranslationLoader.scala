/**
 * @author Daniel Perez
 */

package net.javachallenge.util.internationalization

import java.io.File
import scala.xml._

/**
 * Loader for the translations file.
 *
 * Contains method to load all the translations in the given folder.
 *
 * @constructor creates a loader with the given path.
 * @param translationsFolderPath the path of the folder containtng translation
 * files
 */
class XMLTranslationLoader(translationsFolderPath: String) {
  /**
   * Searches in the folder for translation files and generates the locales and
   * their translation.
   *
   * @throws NullPointerException if the folder does not exist
   */
  def makeLocales: Unit = {
    val folder = new File(translationsFolderPath)
    folder.listFiles.foreach({
      file =>
        {
          val fileName = file toString
          val extension = fileName substring (fileName.lastIndexOf(".") + 1)
          if (extension == "xml") {
            val node = XML.loadFile(file)
            node.attribute("language") match {
              case Some(lang) => generateLocale(lang toString, node)
              case None =>
            }
          }
        }
    })
  }

  /**
   * Creates the locale with the given name and parses the XML
   * tree to register all the translations.
   *
   * @param lang the name of the language for the locale
   * @param node the XML tree to parse
   */
  protected def generateLocale(lang: String, node: Elem) = {
    val country: String = node.attribute("country") match {
      case Some(c) => c.toString
      case None => lang.toUpperCase
    }
    val locale = Locale(lang, country)
    node.child.foreach({
      entry =>
        entry.attribute("key") match {
          case Some(key) => locale.addWord(key toString, entry text)
          case None =>
        }
    })
  }
}

/**
 * Contains helper method to generate locales
 */
object XMLTranslationLoader {
  def generateLocales(translationsFolderPath: String): Unit = {
    new XMLTranslationLoader(translationsFolderPath).makeLocales
  }
}

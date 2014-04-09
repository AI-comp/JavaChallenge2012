/**
 * @author Daniel Perez
 */

package net.javachallenge.util.settings

import scala.xml._
import net.javachallenge.util.internationalization._

/**
 * A parser to load settings saved in an XML file
 */
class XMLSettingsParser extends SettingsParser {

  /**
   * The facade function to load the settings.
   *
   * @param filePath the pah of the XML file
   */
  def loadSettings(filePath: String): Unit = {
    val xml: Elem = XML.load(filePath)

    //The settings in the XML file should be in <application> tag.
    if (xml.label != "application") {
      throw new IllegalSettingsException("Settings must be in tag" +
        "<application>.")
    }

    xml.child.filterNot(isEmptyNode(_)) foreach { child => loadSetting(child label, child) }
  }

  /**
   * Checks if the node is empty.
   * A child is empty if it 'does not have any text',
   * 'does not have any child' and 'does not have any attribute'
   *
   * @param node the node to evaluate
   * @return if the node is empty or not
   */
  def isEmptyNode(node: Node): Boolean = {
    node.text.trim.isEmpty &&
      node.attributes.isEmpty &&
      node.child.isEmpty
  }

  /**
   * Loads a single setting.
   * TODO Add parsable settings by adding cases in the pattern matching
   *
   * @param settingName the name of the setting
   * @param setting the XML node containing the settings
   */
  def loadSetting(settingName: String, setting: Node): Unit = settingName match {
    case "translator" => loadTranslatorSettings(setting)
    case x => throw new IllegalSettingsException("Unknown setting '%s'".format(x))
  }

  /**
   * Loads the settings for the translator.
   *
   * @param node the XML node containing the settings for the translator
   */
  def loadTranslatorSettings(node: Node): Unit = {

    //Loads the translation files in the <folder> tags
    (node \ "resources").headOption match {
      case Some(resources) => (resources \\ "folder") foreach {
        folder => XMLTranslationLoader.generateLocales(folder.text toString)
      }
      case None =>
    }

    //Looks for a default locale or loads the one present in [[net.javachallenge.util.Defaults]] object if not existent
    node attribute ("default-locale") match {
      case Some(language) => Locale.set(language toString)
      case None => Locale.set(Defaults.LOCALE)
    }

    //Looks for a fallback or loads the one present in [[net.javachallenge.util.Defaults]] object if not existent
    node attribute ("fallback") match {
      case Some(fallback) => Locale.setFallback(fallback toString)
      case None => Locale.setFallback(Defaults.FALLBACK)
    }
  }
}

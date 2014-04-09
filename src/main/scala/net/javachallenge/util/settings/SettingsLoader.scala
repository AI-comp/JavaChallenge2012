/**
 * @author Daniel Perez
 */

package net.javachallenge.util.settings

/**
 * A general wrapper to load settings from files
 */
object SettingsLoader {

  /**
   *  Gets the loader class name depending on the format and current settings
   */
  def loadSettings: Unit = EffectiveSettings.settingsFormat match {
    case "xml" => load(EffectiveSettings.xmlSettingsParserClassName)
    case x => throw new IllegalSettingsException("No parser found " +
      "for format %s.".format(x))
  }

  /**
   * Loads the settings using the format and file defined in
   * [[net.javachallenge.util.settings.Defaults]] and
   * [[net.javachallenge.util.settings.EffectiveSettings]] with
   * the given loader
   *
   * @param parserClass the name of the class used to load the settings
   */
  def load(parserClass: String): Unit = {
    val file = "%s/config.%s".format(Defaults.SETTINGS_PATH,
      EffectiveSettings.settingsFormat)

    val parser = Class.forName(parserClass).newInstance.asInstanceOf[SettingsParser]

    parser.loadSettings(file)
  }
}
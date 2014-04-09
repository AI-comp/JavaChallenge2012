/**
 * @author Daniel Perez
 */

package net.javachallenge.util.settings

/**
 * Contain general settings for the application
 */
object EffectiveSettings {

  /**
   * The XML settings parser class name in use
   */
  var xmlSettingsParserClassName = Defaults.XML_SETTINGS_PARSER_CLASS_NAME

  /**
   * The setting file format in use
   */
  var settingsFormat = Defaults.SETTINGS_FORMAT
}
/**
 * @author Daniel Perez
 */

package net.javachallenge.util.settings

/**
 * A trait used as an interface to have a general settings parser
 * not dependant on the file format
 */
trait SettingsParser {
  /**
   * Loads the settings in the given file
   *
   * @param filePath the path of the file containing the settings
   */
  def loadSettings(filePath: String): Unit
}
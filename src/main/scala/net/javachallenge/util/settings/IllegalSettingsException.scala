/**
 * @author Daniel Perez
 */

package net.javachallenge.util.settings

/**
 * An exception to throw when the settings cannot be parsed correctly
 */
case class IllegalSettingsException(text: String) extends Exception(text)
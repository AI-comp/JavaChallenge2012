package net.javachallenge.api;

/**
 * The Logger class is used to print logs of the operations occurring during the game.
 */
public class Logger {

  /**
   * Logs text using the current display function.
   * 
   * @param text the text to log
   */
  public static void log(String text) {
    net.javachallenge.Main$.MODULE$.log(text);
  }
}

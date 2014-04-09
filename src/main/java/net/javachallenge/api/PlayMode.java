package net.javachallenge.api;

/**
 * The {@link PlayMode} class is used to control the different game external parameters.
 */
public class PlayMode {

  private int fps;
  private int availableVeinSelectMilliseconds;
  private int availableTurnMilliseconds;
  private UserInterfaceMode userInterfaceMode;
  private boolean ignoringExceptions;

  /**
   * Returns the number of FPS of the game.
   * 
   * @return the number of FPS of the game
   */
  public int getFps() {
    return fps;
  }

  /**
   * Returns the maximum milliseconds per a turn.
   * 
   * @return the maximum milliseconds per a turn
   */
  public int getAvailableTurnMilliseconds() {
    return availableTurnMilliseconds;
  }

  /**
   * Returns the maximum milliseconds per a vein selection.
   * 
   * @return the maximum milliseconds per a vein selection
   */
  public int getAvailableVeinSelectMilliseconds() {
    return availableVeinSelectMilliseconds;
  }

  /**
   * Returns the mode of the user interface.
   * 
   * @return the mode of the user interface
   */
  public UserInterfaceMode getUserInterfaceMode() {
    return userInterfaceMode;
  }

  /**
   * Returns the ignoring exception status.
   * 
   * @return the ignoring exception status
   */
  public boolean isIgnoringExceptions() {
    return ignoringExceptions;
  }

  /**
   * Constructs a PlayMode with the given parameters.
   * 
   * @param fps the fps which is speed for advancing the game
   * @param availableVeinSelectMilliseconds the maximum milliseconds per a vein selection
   * @param availableTurnMilliseconds the maximum milliseconds per a turn.
   * @param userInterfaceMode the user interface mode
   * @param ignoringExceptions the ignoring exception status (true to ignore)
   */
  public PlayMode(int fps, int availableVeinSelectMilliseconds, int availableTurnMilliseconds,
      UserInterfaceMode userInterfaceMode, boolean ignoringExceptions) {
    this.fps = fps;
    this.availableVeinSelectMilliseconds = availableVeinSelectMilliseconds;
    this.availableTurnMilliseconds = availableTurnMilliseconds;
    this.userInterfaceMode = userInterfaceMode;
    this.ignoringExceptions = ignoringExceptions;
  }
}

package net.javachallenge.api;

/**
 * The {@link PlayModeBuilder} class is used to construct instances of the PlayMode class.
 */
public class PlayModeBuilder {
  private int fps;
  private int availableTurnMilliseconds;
  private int availableVeinSelectMilliseconds;
  private UserInterfaceMode userInterfaceMode;
  private boolean ignoringExceptions;

  /**
   * Constructs a {@link PlayModeBuilder} with default parameters.
   */
  public PlayModeBuilder() {
    PlayMode mode = net.javachallenge.PlayModeHelper.defaultInstance();
    this.fps = mode.getFps();
    this.availableTurnMilliseconds = mode.getAvailableTurnMilliseconds();
    this.availableVeinSelectMilliseconds = mode.getAvailableVeinSelectMilliseconds();
    this.userInterfaceMode = mode.getUserInterfaceMode();
    this.ignoringExceptions = mode.isIgnoringExceptions();
  }

  /**
   * Builds a {@link PlayMode} instance with this instance parameters.
   */
  public PlayMode build() {
    return net.javachallenge.PlayModeHelper.build(this);
  }

  /**
   * Returns the fps which is speed for advancing the game.
   * 
   * @return the fps which is speed for advancing the game
   */
  public int getFps() {
    return fps;
  }

  /**
   * Set the fps which is speed for advancing the game.
   * 
   * @param fps the fps to set
   * @return the updated instance of PlayModeBuilder
   */
  public PlayModeBuilder setFps(int fps) {
    this.fps = fps;
    return this;
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
   * Set the maximum milliseconds per a turn.
   * 
   * @param availableTurnMilliseconds the maximum milliseconds per a turn
   * @return the updated instance of PlayModeBuilder
   */
  public PlayModeBuilder setAvailableTurnMilliseconds(int availableTurnMilliseconds) {
    this.availableTurnMilliseconds = availableTurnMilliseconds;
    return this;
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
   * Set the maximum milliseconds per a vein selection.
   * 
   * @param availableVeinSelectMilliseconds the maximum milliseconds per a vein selection.
   * @return the updated instance of PlayModeBuilder
   */
  public PlayModeBuilder setAvailableVeinSelectMilliseconds(int availableVeinSelectMilliseconds) {
    this.availableVeinSelectMilliseconds = availableVeinSelectMilliseconds;
    return this;
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
   * Set the mode of the user interface.
   * 
   * @param userInterfaceMode the mode of the user interface
   * @return the updated instance of PlayModeBuilder
   */
  public PlayModeBuilder setUserInterfaceMode(UserInterfaceMode userInterfaceMode) {
    this.userInterfaceMode = userInterfaceMode;
    return this;
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
   * Set the ignoring exception status.
   * 
   * @param ignoringExceptions the ignoring exception status
   * @return the updated instance of PlayModeBuilder
   */
  public PlayModeBuilder setIgnoringExceptions(boolean ignoringExceptions) {
    this.ignoringExceptions = ignoringExceptions;
    return this;
  }
}

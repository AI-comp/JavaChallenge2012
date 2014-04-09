package net.javachallenge.api;

/**
 * A utility class to construct some instances.
 */
public class Make {

  private Make() {}

  /**
   * Constructs a builder for a game setting.
   * 
   * @return a builder for a game setting
   */
  public static GameSettingBuilder gameSettingsBuilder() {
    return new GameSettingBuilder();
  }

  /**
   * Constructs a builder for a play mode.
   * 
   * @return a builder for a play mode
   * */
  public static PlayModeBuilder playModeBuilder() {
    return new PlayModeBuilder();
  }

  /**
   * Constructs a location which represents a vertex of a tile.
   * 
   * @return a location which represents a vertex of a tile
   */
  public static TrianglePoint point(int x, int y) {
    return new net.javachallenge.entity.TrianglePoint(x, y);
  }
}

package net.javachallenge.api;

/**
 * The {@link GameSettingBuilder} class is used to initialize the settings of the game.
 */
public class GameSettingBuilder {
  private int alienTradeMargin;
  private int initialMoney;
  private int mapSize;
  private int maxRound;
  private int veinCount;

  /**
   * Constructs a {@link GameSettingBuilder} with default settings.
   */
  public GameSettingBuilder() {
    GameSetting gs = net.javachallenge.entity.GameSetting$.MODULE$.defaultInstance();
    alienTradeMargin = gs.getAlienTradeMargin();
    initialMoney = gs.getInitialMoney();
    mapSize = gs.getMapSize();
    maxRound = gs.getMaxRound();
    veinCount = gs.getVeinCount();
  }

  /**
   * Returns a {@link GameSetting} instance with the settings of this instance.
   * 
   * @return settings for the game
   */
  public GameSetting build() {
    return net.javachallenge.entity.GameSetting$.MODULE$.build(this);
  }

  /**
   * Returns the alien trade margin.
   * 
   * @return the alien trade margin
   */
  public int getAlienTradeMargin() {
    return alienTradeMargin;
  }

  /**
   * Set the alien trade margin to alienTradeMargin.
   * 
   * @param alienTradeMargin the margin of the alien during trades
   */
  public GameSettingBuilder setAlienTradeMargin(int alienTradeMargin) {
    this.alienTradeMargin = alienTradeMargin;
    return this;
  }

  /**
   * Return the initial amount of money.
   * 
   * @return the initial amount of money
   */
  public int getInitialMoney() {
    return initialMoney;
  }

  /**
   * Set the initial amount of money.
   * 
   * @param initialMoney the initial amount of money
   * @return the updated instance of GameSettingBuilder
   */
  public GameSettingBuilder setInitialMoney(int initialMoney) {
    this.initialMoney = initialMoney;
    return this;
  }

  /**
   * Returns the size of an edge of the map in tiles number.
   * 
   * @return the size of an edge of the map in tiles number
   */
  public int getMapSize() {
    return mapSize;
  }

  /**
   * Set the size of the map with the size of an edge in tiles number.
   * 
   * @param mapSize the number of the tiles in an edge
   * @return the updated instance of GameSettingBuilder
   */
  public GameSettingBuilder setMapSize(int mapSize) {
    this.mapSize = mapSize;
    return this;
  }

  /**
   * Returns the maximum number of rounds in a game.
   * 
   * @return the maximum number of rounds in a game
   */
  public int getMaxRound() {
    return maxRound;
  }

  /**
   * Set the maxiumum numbers of round in a game.
   * 
   * @param maxRound the maxiumum numbers of round in a game
   * @return the updated instance of GameSettingBuilder
   */
  public GameSettingBuilder setMaxRound(int maxRound) {
    this.maxRound = maxRound;
    return this;
  }

  /**
   * Returns the number of veins on the field.
   * 
   * @return the number of veins on the field.
   */
  public int getVeinCount() {
    return veinCount;
  }

  /**
   * Set the number of veins on the field.
   * 
   * @param veinCount the number of veins on the field.
   * @return the updated instance of GameSettingBuilder
   */
  public GameSettingBuilder setVeinCount(int veinCount) {
    this.veinCount = veinCount;
    return this;
  }
}

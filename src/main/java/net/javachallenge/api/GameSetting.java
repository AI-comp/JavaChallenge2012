package net.javachallenge.api;


/**
 * A class which represents game setting.
 * 
 * Note that this interface is immutable, thus, its object stores the information of just this turn
 * and will not be updated.
 */
public interface GameSetting {
  /**
   * Returns the maximum round number. Note that the round number is more than or equal to 0 and
   * less than or equal to the maximum number.
   */
  int getMaxRound();

  /**
   * Returns the initial money for the players.
   */
  int getInitialMoney();

  /**
   * Returns the initial amount of the specified material for the players.
   */
  int getInitialMaterial(Material material);

  /**
   * Returns the amount of the specified material required to upgrade material rank from 1 to 2.
   * 
   * @return the amount of the specified material required to upgrade material rank from 1 to 2
   */
  int getMaterialsForUpgradingMaterialRankFrom1To2(Material material);

  /**
   * Returns the amount of the specified material required to upgrade material rank from 2 to 3.
   * 
   * @return the amount of the specified material required to upgrade material rank from 2 to 3
   */
  int getMaterialsForUpgradingMaterialRankFrom2To3(Material material);

  /**
   * Returns the amount of the specified material required to upgrade robot rank from 1 to 2.
   * 
   * @return the amount of the specified material required to upgrade robot rank from 1 to 2
   */
  int getMaterialsForUpgradingRobotRankFrom1To2(Material material);

  /**
   * Returns the amount of the specified material required to upgrade robot rank from 2 to 3.
   * 
   * @return the amount of the specified material required to upgrade robot rank from 2 to 3
   */
  int getMaterialsForUpgradingRobotRankFrom2To3(Material material);

  /**
   * Returns the map size. Note that the size must be 10 in JavaChallenge2012.
   */
  int getMapSize();

  /**
   * Returns the number of the veins. Note that the number must be 40 in JavaChallenge2012.
   */
  int getVeinCount();

  /**
   * Return the margin of the alien trade. This margin is used to calculate only selling price.
   */
  int getAlienTradeMargin();
}

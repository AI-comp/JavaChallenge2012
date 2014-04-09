package net.javachallenge.api;

/**
 * The player interface is the abstract representation of a player in the game.
 * 
 * Note that this interface is immutable, thus, its object stores the information of just this turn
 * and will not be updated.
 */
public interface Player {

  /**
   * Returns the id of the player.
   * 
   * @return the id of the player
   */
  int getId();

  /**
   * Returns the quantity of the given material owned by the player.
   * 
   * @param material the material to get informations about
   * @return the quantity of the given material owned by the player
   */
  int getMaterial(Material material);

  /**
   * Returns the amount of money owned by the player.
   * 
   * @return the amount of money owned by the player
   */
  int getMoney();

  /**
   * Returns the time to live, i.e., the number of the last round where the player were active
   * (considered as living).
   * 
   * @return the time to live
   */
  int getTimeToLive();

  /**
   * Please use getTimeToLive instead of this method.
   */
  @Deprecated
  int getLastActiveRound();
}

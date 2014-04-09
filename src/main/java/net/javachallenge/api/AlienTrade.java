package net.javachallenge.api;

/**
 * An interface which represents the alien trade for providing market information.
 * 
 * Note that this interface is immutable, thus, its object stores the information of just this turn
 * and will not be updated.
 */
public interface AlienTrade {
  /**
   * Returns the price for buying the specified material from the alien.
   * 
   * @param material a material to buy
   * @return the price for buying the specified material from the alien
   */
  int getBuyPriceOf(Material material);

  /**
   * Returns the price for selling the specified material to the alien.
   * 
   * @param material a material to sell
   * @return the price for selling the specified material to the alien.
   */
  int getSellPriceOf(Material material);
}

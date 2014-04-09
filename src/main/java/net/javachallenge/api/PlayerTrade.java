package net.javachallenge.api;

/**
 * The {@link PlayerTrade} class represents a trade between players. It can be an offer or a demand.
 * 
 * Note that this interface is immutable, thus, its object stores the information of just this turn
 * and will not be updated.
 * 
 */
public interface PlayerTrade {

  /**
   * Returns the id of the player doing the trade.
   * 
   * @return the id of the player doing the trade
   */
  int getPlayerId();

  /**
   * Returns the material of the trade.
   * 
   * @return the material of the trade
   */
  Material getMaterial();

  /**
   * Returns the amount of material in the trade.
   * 
   * @return the amount of material in the trade
   */
  int getAmount();

  /**
   * Returns the price per unit of material in this trade.
   * 
   * @return the price per unit of material in this trade
   */
  int getPricePerOneMaterial();

  /**
   * Returns the type of the trade.
   * 
   * @return the type of the trade
   */
  TradeType getTradeType();
}

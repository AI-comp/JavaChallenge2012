package net.javachallenge.api;

import java.util.ArrayList;

/**
 * A class that represents the whole state of the game. The all information of the game can be
 * retrieved from the instance of this class.
 * 
 * Note that this interface is immutable, thus, its object stores the information of just this turn
 * and will not be updated.
 */
public interface Game {
  /**
   * Returns the neutral player id, which is constant value.
   * 
   * @return the neutral player id
   */
  int getNeutralPlayerId();

  /**
   * Returns your player id, which is the active player, because your program is executed when your
   * player is active.
   * 
   * @return your player id, which is the active player
   */
  int getMyPlayerId();

  /**
   * Returns the all players participating in the game without the neutral player as a copied
   * mutable list.
   * 
   * @return the all players participating in the game without the neutral player as a copied
   *         mutable list
   */
  ArrayList<Player> getPlayers();

  /**
   * Please use getSurvivingPlayers instead of this method.
   */
  @Deprecated
  ArrayList<Player> getSurvivedPlayers();

  /**
   * Returns the surviving players participating in the game without the neutral player as a copied
   * mutable list.
   * 
   * @return the surviving players participating in the game without the neutral player as a copied
   *         mutable list
   */
  ArrayList<Player> getSurvivingPlayers();

  /**
   * Returns the {@link Player} instance with the specified player id.
   * 
   * @param id the player id to retrieve the {@link Player} instance
   * @return the player
   */
  Player getPlayer(int id);

  /**
   * Returns your player, which is the active player, because your program is executed when your
   * player is active.
   * 
   * @return your player, which is the active player
   */
  Player getMyPlayer();

  /**
   * Returns the boolean whether the specified player is surviving.
   * 
   * @param playerId the player id to check surviving
   * @return the boolean whether the specified player is surviving
   */
  boolean isSurvivingPlayer(int playerId);

  /**
   * Returns the alien trade for providing the market rate information.
   * 
   * @return the alien trade for providing the market rate information
   */
  AlienTrade getAlienTrade();

  /**
   * Returns the field which contains the veins and the squads.
   * 
   * @return the field which contains the veins and the squads
   */
  Field getField();

  /**
   * Returns the existing offer list with the specified material as a copied mutable list.
   * 
   * @param material the material to get offers
   * @return the existing offer list with the specified material as a copied mutable list
   */
  ArrayList<PlayerTrade> getOffers(Material material);

  /**
   * Returns the existing demand list with the specified material as a copied mutable list.
   * 
   * @param material the material to get demands
   * @return the existing demand list with the specified material as a copied mutable list
   */
  ArrayList<PlayerTrade> getDemands(Material material);

  /**
   * Returns the existing offer list with the specified player as a copied mutable list.
   * 
   * @param playerId the player id to get offers
   * @return the existing offer list with the specified player as a copied mutable list
   */
  ArrayList<PlayerTrade> getOffers(int playerId);

  /**
   * Returns the existing demand list with the specified player as a copied mutable list.
   * 
   * @param playerId the player id to get demands
   * @return the existing demand list with the specified player as a copied mutable list
   */
  ArrayList<PlayerTrade> getDemands(int playerId);

  /**
   * Returns the existing offer with the specified material from the specified player or, null if no
   * offer.
   * 
   * @param playerId the player id to get offer
   * @param material the material to get offer
   * @return the existing offer with the specified material from the specified player or, null if no
   *         offer
   */
  PlayerTrade getOffer(int playerId, Material material);

  /**
   * Returns the existing demand with the specified material from the specified player or, null if
   * no offer.
   * 
   * @param playerId the player id to get demand
   * @param material the material to get demand
   * @return the existing demand with the specified material from the specified player or, null if
   *         no offer
   */
  PlayerTrade getDemand(int playerId, Material material);

  /**
   * Returns the existing trades (offers and demands) as a copied mutable list.
   * 
   * @return the existing trades (offers and demands) as a copied mutable list
   */
  ArrayList<PlayerTrade> getPlayerTrades();

  /**
   * Returns the settings of this game.
   * 
   * @return the settings of this game
   */
  GameSetting getSetting();

  /**
   * Returns the number of the players.
   * 
   * @return the number of the players
   */
  int getPlayerCount();

  /**
   * Returns the number of the current round. Note that you can get the maximum round number from
   * the {@link GameSetting} instance.
   * 
   * @return the number of the current round
   */
  int getRound();

  /**
   * Returns the total money which is sum of the and all own materials of the bank with the
   * specified player id.
   * 
   * @param playerId player id to calculate total money
   * @return the total money
   */
  int getTotalMoneyWhenSellingAllMaterials(int playerId);
}

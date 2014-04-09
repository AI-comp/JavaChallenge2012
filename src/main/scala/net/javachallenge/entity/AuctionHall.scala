package net.javachallenge.entity

import scala.collection.mutable
import scala.collection.immutable

/**
 * A container class to stock and manage trades between players.
 *
 * @constructor creates a hall auction
 * @param The trades currently available.
 * @param The trades that have been completed or canceled.
 */
case class AuctionHall(val trades: Map[(Int, Material), Trade] = Map.empty) {
  require(trades != null)

  /**
   * Clear the specified player's trades.
   */
  def clear(player: Player) = {
    var newTrades = trades
    var newPlayer = player
    for (material <- Material.all) {
      newPlayer = newTrades.get((player.id, material)) match {
        case Some(t) => t.cancel(newPlayer)
        case _ => newPlayer
      }
      newTrades -= ((player.id, material))
    }
    (this.copy(trades = newTrades), newPlayer)
  }

  /**
   * Adds a trade to the auction hall.
   */
  private def addTrade(trade: Trade) = {
    if (trades.contains((trade.publisherId, trade.material))) {
      throw new IllegalArgumentException("You have already porposed the trade for the " + trade.material);
    }
    this.copy(trades = trades + ((trade.publisherId, trade.material) -> trade))
  }

  /**
   * Creates and adds an offer to the hall auction.
   */
  def addOffer(game: Game, publisherId: Int, material: Material, amount: Int, price: Int) = {
    val (offer, player) = Offer.publish(game, publisherId, material, amount, price)
    (addTrade(offer), player)
  }

  /**
   * Creates and adds a demand to the hall auction.
   */
  def addDemand(game: Game, publisherId: Int, material: Material, amount: Int, price: Int) = {
    val (demand, player) = Demand.publish(game, publisherId, material, amount, price)
    (addTrade(demand), player)
  }

  /**
   * Executes a transaction and archives the trade if done.
   */
  def makeTransaction(game: Game, customerId: Int, trade: Trade, amount: Int) = {
    val (trades, publisher, customer) = trade.makeTransaction(game, customerId, amount)
    (this.copy(trades = this.trades - ((trade.publisherId, trade.material)) ++ trades), publisher, customer)
  }
}

package net.javachallenge.entity

import net.javachallenge.api.TradeType

/**
 * Abstract class to represent a trade.
 *
 * @param id the id of the trade
 * @param publisherId the id of the player publishing the trade
 * @param material the material of the offer
 * @param amount the amount of material in the offer
 * @param price the price for ONE unity of the material
 * @param done the boolean indicating if the transaction has been completed or not
 * @param canceled the boolean indicating if the transaction has been canceled
 */
abstract sealed class Trade private[entity] (val publisherId: Int, val material: Material, val amount: Int, val price: Int)
    extends net.javachallenge.api.PlayerTrade {

  require(material != null)
  require(amount > 0, "Amount must be positive")
  require(price > 0, "Price must be positive")

  override def getPlayerId = publisherId
  override def getMaterial = material
  override def getAmount = amount
  override def getPricePerOneMaterial = price
  override def getTradeType = if (this.isInstanceOf[Offer]) TradeType.Offer else TradeType.Demand

  def publisher(game: Game) = game.players(publisherId)

  /**
   * Executes the core of the transaction, changing the amount of material left in the trade.
   *
   * @param game the game instance which contains whole game states
   * @param customerId the id of the customer who wants to make the transaction with the trade
   * @param transactionAmount the amount of material to be traded in the transaction
   * @throws TradeException if the transaction cannot be achieved
   */
  protected def makeCoreTransaction(game: Game, customerId: Int, transactionAmount: Int): (Map[(Int, Material), Trade], Player, Player)

  /**
   * Executes the transaction, changing the amount of material left in the trade.
   *
   * @param game the game instance which contains whole game states
   * @param customerId the id of the customer who wants to make the transaction with the trade
   * @param transactionAmount the amount of material to be traded in the transaction
   * @throws TradeException if the transaction cannot be achieved
   */
  def makeTransaction(game: Game, customerId: Int, transactionAmount: Int) = {
    if (transactionAmount > amount) {
      throw new InvalidCommandException("The material amound for selling/buying should be less than the offered/demanded amount.")
    }
    makeCoreTransaction(game, customerId, transactionAmount)
  }

  /**
   * Cancels the transaction.
   */
  def cancel(player: Player): Player
}

/**
 * An offer put by a person who wants to sell material.
 * {@inheritdoc}
 * @constructor creates a new offer
 */
case class Offer private[entity] (sellerId: Int, offerMaterial: Material, offerAmount: Int, offerPrice: Int)
    extends Trade(sellerId, offerMaterial, offerAmount, offerPrice) {

  /**
   * {@inheritdoc}
   * Changes the amount of money and material of both players in the transaction.
   */
  def makeCoreTransaction(game: Game, buyerId: Int, transactionAmount: Int) = {
    val totalPrice = transactionAmount * price
    val buyer = game.players(buyerId)
    val newSeller = publisher(game).changeMoney(totalPrice)
    val newBuyer = buyer.changeMoney(-totalPrice).changeMaterial(material, transactionAmount)
    val newAmount = offerAmount - transactionAmount

    (if (newAmount == 0) Map.empty else Map((publisherId, material) -> this.copy(offerAmount = newAmount)),
      newSeller, newBuyer)
  }

  /**
   * {@inheritdoc}
   */
  override def cancel(player: Player) = {
    require(player.id == sellerId)
    player.changeMaterial(material, amount)
  }
}

/**
 * Companion object for Offer class containing factory method.
 */
object Offer {
  def publish(game: Game, sellerId: Int, material: Material, amount: Int, price: Int) = {
    val seller = game.players(sellerId)
    (new Offer(sellerId, material, amount, price),
      seller.changeMaterial(material, -amount))
  }
}

/**
 * A demand put by a person who wants to buy material.
 * {@inheritdoc}
 * @constructor creates a new demand
 */
case class Demand private[entity] (buyerId: Int, demandMaterial: Material, demandAmount: Int, demandPrice: Int)
    extends Trade(buyerId, demandMaterial, demandAmount, demandPrice) {

  /**
   * {@inheritdoc}
   * Changes the amount of money and material of both players in the transaction.
   */
  override def makeCoreTransaction(game: Game, sellerId: Int, transactionAmount: Int) = {
    val totalPrice = transactionAmount * price
    val seller = game.players(sellerId)
    val newBuyer = publisher(game).changeMaterial(material, transactionAmount)
    val newSeller = seller.changeMoney(totalPrice).changeMaterial(material, -transactionAmount)
    val newAmount = demandAmount - transactionAmount

    (if (newAmount == 0) Map.empty else Map((publisherId, material) -> this.copy(demandAmount = newAmount)),
      newBuyer, newSeller)
  }

  /**
   * {@inheritdoc}
   */
  override def cancel(player: Player) = {
    require(player.id == buyerId)
    player.changeMoney(price * amount)
  }
}

/**
 * Companion object for Demand class containing factory method.
 */
object Demand {
  def publish(game: Game, buyerId: Int, material: Material, amount: Int, price: Int) = {
    val buyer = game.players(buyerId)
    val totalPrice: Int = price * amount
    (new Demand(buyerId, material, amount, price), buyer.changeMoney(-totalPrice))
  }
}

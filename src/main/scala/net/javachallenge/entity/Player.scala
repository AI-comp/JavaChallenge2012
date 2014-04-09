package net.javachallenge.entity

import scala.collection.mutable
import scala.collection.immutable
import scala.collection.immutable.TreeMap

/**
 * A player of the game.
 *
 * @constructor creates a player
 * @param name the name of the player to be created
 * @param money the money owned by the player
 * @param materials owned by the player.
 */
case class Player(val id: Int, val name: String, val money: Int, val materials: Map[Material, Int], val timeToLive: Int)
    extends net.javachallenge.api.Player {

  require(money >= 0, "initialMoney must be greater than or equal to zero")
  require(!materials.values.exists { amount => amount < 0 },
    "material amount must be greater than or equal to zero")

  override def getId = id

  override def getMaterial(material: net.javachallenge.api.Material) = {
    require(material != null)

    materials(Material(material.name()))
  }

  override def getMoney = money

  override def getLastActiveRound = getTimeToLive

  override def getTimeToLive = timeToLive

  override def toString = {
    id + ". " + name + "(" + money + "J): " + materials + ", timeToLive: " + timeToLive
  }

  def isNeutral = id == Player.neutralPlayer.id

  def earnRobots(game: Game) = {
    val veins = mutable.Map(game.field.veins.toStream: _*)
    for ((point, vein) <- game.field.owingVeins(this)) {
      val newVein = vein.earnRobot()
      veins += (point -> newVein)
    }
    TreeMap(veins.toStream: _*)
  }

  /**
   * Starts this player's turn.
   *
   * @param game the game instance containing all states
   */
  def earnMaterials(game: Game) = {
    val newMaterials = mutable.Map(materials.toStream: _*)
    for ((point, vein) <- game.field.owingVeins(this)) {
      newMaterials(vein.material) += vein.materialIncome
    }
    this.copy(materials = newMaterials.toMap)
  }

  def updateTimeToLive(game: Game) = {
    this.copy(timeToLive = game.round)
  }

  def sellMaterial(material: Material, amount: Int, pricePerEachMaterial: Int) = {
    if (amount <= 0) {
      throw new InvalidCommandException("The material amount should be grater than 0.")
    }
    if (pricePerEachMaterial <= 0) {
      throw new InvalidCommandException("The price should be grater than 0.")
    }

    this.changeMaterial(material, -amount)
      .changeMoney(pricePerEachMaterial * amount)
  }

  def buyMaterial(material: Material, amount: Int, pricePerEachMaterial: Int) = {
    if (amount <= 0) {
      throw new InvalidCommandException("The material amount should be grater than 0.")
    }
    if (pricePerEachMaterial <= 0) {
      throw new InvalidCommandException("The price should be grater than 0.")
    }

    this.changeMaterial(material, amount)
      .changeMoney(-pricePerEachMaterial * amount)
  }

  /**
   * Adds money of this player to the current money.
   *
   * @param addedAmount the amount to be added
   */
  def changeMoney(addedAmount: Int) = {
    val newMoney = money + addedAmount
    if (newMoney < 0) {
      throw new InvalidCommandException("You don't have enough money.")
    }
    this.copy(money = newMoney)
  }

  /**
   * Adds the specified material amount of this player to the current amount.
   *
   * @param materialKind the kind of the material to be added
   * @param addedAmount the amount to be added
   */
  def changeMaterial(materialKind: Material, addedAmount: Int) = {
    val newAmount = materials(materialKind) + addedAmount
    if (newAmount < 0) {
      throw new InvalidCommandException("You don't have enough materials.")
    }
    this.copy(materials = materials + (materialKind -> newAmount))
  }
}

/**
 * Companion object for Player class containing factory method.
 */
object Player {
  /**
   * Neutral player who does nothing (it just holds the vein).
   */
  val neutralPlayer = new Player(-1, "neutral", 0, Map().withDefaultValue(0), 0)

  /**
   * Creates a player.
   */
  def apply(id: Int, name: String, setting: GameSetting): Player = {
    new Player(id, name, setting.initialMoney, setting.initialMaterials, 0)
  }
}

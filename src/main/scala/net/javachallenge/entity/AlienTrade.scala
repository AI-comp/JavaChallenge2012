package net.javachallenge.entity

import net.javachallenge.util.settings.Defaults
import scala.Mutable
import scala.math

case class AlienTrade(val prices: Map[Material, Int], val margin: Int)
    extends net.javachallenge.api.AlienTrade {
  require(!prices.exists { case (m, p) => p <= 0 }, "Prices of materials should be grater than 0.")
  require(prices.size == Material.all.size, "Prices of materials should be prepared for all materials.")

  override def getBuyPriceOf(material: net.javachallenge.api.Material) = {
    require(material != null)

    buyPriceOf(material)
  }

  override def getSellPriceOf(material: net.javachallenge.api.Material) = {
    require(material != null)

    sellPriceOf(material)
  }

  /**
   * Returns buying price of the material
   *
   * @param material
   */
  def buyPriceOf(material: Material): Int = {
    prices(material)
  }

  /**
   * Returns selling price of the material
   *
   * @param material
   */
  def sellPriceOf(material: Material): Int = {
    prices(material) / margin
  }

  /**
   * Update bank value using player materials
   *
   */
  def update(game: Game) = {
    // compute each player's total amount of materials
    var totalMaterials = Map[Material, Long]().withDefaultValue(0L)
    var total = 0L
    for (player <- game.survivedPlayers) {
      for ((m, amount) <- player.materials) {
        totalMaterials += (m -> (totalMaterials(m) + amount))
        total += amount
      }
    }
    this.copy(prices = totalMaterials
      .map {
        case (m, amount) =>
          (m, (math.min(100L * (total + 300L) / (amount + 100) * (total + 300L) / (amount + 100) / 9, Int.MaxValue)).toInt)
      }.toMap)
  }
}

object AlienTrade {
  def apply(settings: GameSetting): AlienTrade = new AlienTrade(Material.all.map(m => (m, 100)).toMap, settings.bankMargin)
}
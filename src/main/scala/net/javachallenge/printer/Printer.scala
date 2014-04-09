package net.javachallenge.printer

import jp.ac.waseda.cs.washi.gameaiarena.api.Point2
import net.javachallenge.entity.Vein
import net.javachallenge.entity.Game
import net.javachallenge.util.internationalization.I18n
import net.javachallenge.entity.Material

/**
 * An console printer for game objects.
 */
object Printer {

  /**
   * Prints all the veins.
   *
   * @param veins the veins to display
   */
  def veins(game: Game, print: String => Unit) {
    for ((p, vein) <- game.field.veins) {
      print("(%+3d,%+3d): %s".format(p.x, p.y, vein))
    }
  }

  /**
   * Prints the given location and the given vein.
   *
   * @param p the location of the vein to print
   * @param vein the vein to print
   */
  def vein(p: Point2, vein: Vein, print: String => Unit) {
    print("(%+3d,%+3d): %s".format(p.x, p.y, vein))
  }

  /**
   * Prints all the veins.
   *
   * @param veins the veins to display
   */
  def squads(game: Game, print: String => Unit) {
    for ((squad, index) <- game.field.squads.zipWithIndex) {
      print("> %s".format(index, squad))
    }
  }

  /**
   * Prints all the veins.
   *
   * @param veins the veins to display
   */
  def players(game: Game, print: String => Unit) {
    for (player <- game.players) {
      print("> %s".format(player.id, player))
    }
  }

  /**
   * Displays all the trades.
   *
   * @param trades a map containing all the available trades with their index
   */
  def trades(game: Game, print: String => Unit) {
    print("-" * 50)
    print("%s: %s %s %s".format(I18n.get("id"), I18n.get("material"),
      I18n.get("amount"), I18n.get("price")))
    print("-" * 50)
    game.auctionHall.trades.values.zipWithIndex foreach {
      case (trade, id) =>
        print("%d: %s %d %d".format(id, trade.material, trade.amount, trade.price))
    }
    print("-" * 50)
  }

  def rate(game: Game, print: String => Unit) {
    for (material <- Material.all) {
      print("A price of " + material + " to buy from the bank: " + game.alienTrade.buyPriceOf(material))
      print("A price of " + material + " to sell to the bank: " + game.alienTrade.sellPriceOf(material))
    }
  }
}
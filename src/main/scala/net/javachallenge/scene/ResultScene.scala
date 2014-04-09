package net.javachallenge.scene

import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.GameEnvironment
import net.javachallenge.entity.Player
import scala.util.Sorting
import net.javachallenge.util.misc.ImageLoader
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints

trait ResultScene extends CommandBaseScene {

  var sortedPlayers: Array[Player] = null

  override def initialize() = {
    game = game.clearAuctionHall()
    displayLine("The game ends!")
    val veinCounts = game.field.veinCounts
    displayLine("time to live: " + game.players.map(p => p.name + ":" + p.timeToLive).mkString(", "))
    displayLine("veins: " + game.players.map(p => p.name + ":" + veinCounts(p.id)).mkString(", "))
    displayLine("robots: " + game.players.map(p => p.name + ":" + game.field.sumRobots(p.id)).mkString(", "))
    displayLine("money: " + game.players.map(p => p.name + ":" + game.getTotalMoneyWhenSellingAllMaterials(p.id)).mkString(", "))
    sortedPlayers = Sorting.stableSort(game.players, (p1: Player, p2: Player) => {
      if (p1.timeToLive != p2.timeToLive)
        p1.timeToLive > p2.timeToLive
      else if (veinCounts(p1.id) != veinCounts(p2.id))
        veinCounts(p1.id) > veinCounts(p2.id)
      else if (game.field.sumRobots(p1.id) != game.field.sumRobots(p2.id))
        game.field.sumRobots(p1.id) > game.field.sumRobots(p2.id)
      else if (game.getTotalMoneyWhenSellingAllMaterials(p1.id) != game.getTotalMoneyWhenSellingAllMaterials(p2.id))
        game.getTotalMoneyWhenSellingAllMaterials(p1.id) > game.getTotalMoneyWhenSellingAllMaterials(p2.id)
      else
        p1.id < p2.id
    })
  }

  override def draw() {
    val backImg = ImageLoader.loadResultBackground(renderer)
    val numbers = ImageLoader.loadResultNumber(renderer)
    val lamp = ImageLoader.loadLamp(renderer)

    val fontColor = new Color(255, 190, 104)

    val font = new Font(Font.SERIF, Font.BOLD, 24);
    val graphics = renderer.getPanel().getGraphics().asInstanceOf[Graphics2D]
    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

    def drawNumber(value: Int, right: Int, top: Int, size: Int) {
      var x = right;
      value.toString.reverse.foreach { d =>
        x -= size / 2

        renderer.drawImage(numbers(size)(d.toInt - '0'.toInt), x, top)
      }
    }

    renderer.drawImage(backImg, 0, 0)
    for ((player, index) <- sortedPlayers.zipWithIndex) {
      renderer.drawImage(lamp(player.id), 72, 56 + 72 * index)
      renderer.drawString(player.name, 136, 104 + 72 * index, fontColor, font)
      drawNumber(player.timeToLive, 392 + 96, 56 + 72 * index, 64)
      drawNumber(game.field.veinCounts(player.id), 544 + 64, 56 + 72 * index, 64)
      drawNumber(game.field.sumRobots(player.id), 680 + 144, 72 + 72 * index, 48)
      drawNumber(game.getTotalMoneyWhenSellingAllMaterials(player.id), 880 + 144, 72 + 72 * index, 48)
    }
  }
}

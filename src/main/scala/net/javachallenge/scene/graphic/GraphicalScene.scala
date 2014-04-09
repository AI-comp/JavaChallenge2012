package net.javachallenge.scene.graphic

import scala.math.abs
import scala.collection.mutable
import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import jp.ac.waseda.cs.washi.gameaiarena.api.Point2
import net.javachallenge.GameEnvironment
import net.javachallenge.entity.Game
import net.javachallenge.scene.AbstractScene
import net.javachallenge.util.misc.ImageLoader
import net.javachallenge.entity.Material
import net.javachallenge.scene.CommandBaseScene
import java.awt.Color
import java.awt.Font

trait GraphicalScene extends AbstractScene {
  /* Image sizes (width, height) */
  val materialImageSize = Size(16, 16)
  val veinImageSize = Size(38, 25)
  val plusImageSize = Size(5, 5)
  val robotImageSize = Size(10, 9)
  val numberImageSize = Size(6, 9)
  val roundTitleImageSize = Size(128, 32)
  val roundSlashImageSize = Size(32, 32)
  val roundNumberImageSize = Size(20, 32)
  val veinTopMargin = veinImageSize.x - veinImageSize.y
  val font = new Font(Font.MONOSPACED, Font.PLAIN, 12);

  class Size(val x: Int, val y: Int)
  object Size {
    def apply(x: Int, y: Int) = new Size(x, y)
  }

  def drawTile() {
    val tileImg = ImageLoader.loadTiles(renderer)(env.tileSize)
    for (y <- -(game.setting.mapSize - 1) to (game.setting.mapSize - 1)) {
      for (x <- -(game.setting.mapSize - 1) to (game.setting.mapSize - 1) - abs(y)) {
        val (px, py) = env.toHexPoint(x, y)
        renderer.drawImage(tileImg, px + env.offsetX, py + env.offsetY)
      }
    }
  }

  def drawRound() {
    //    val roundTitle = ImageLoader.loadRoundTitle(renderer)
    val roundSlash = ImageLoader.loadRoundSlash(renderer)
    val roundPosition = new Point2(10, 10)

    //    renderer.drawImage(roundTitle, roundPosition.x, roundPosition.y)
    renderer.drawImage(roundSlash, roundPosition.x + roundTitleImageSize.x / 2 - roundSlashImageSize.x / 2 + 2,
      roundPosition.y)

    def drawRoundNumber(value: Int, right: Int, top: Int) {
      val numberImages = ImageLoader.loadRoundNumber(renderer)
      var x = right;
      value.toString.reverse.foreach { d =>
        x -= roundNumberImageSize.x

        renderer.drawImage(numberImages(d.toInt - '0'.toInt), x, top)
      }
    }
    drawRoundNumber(math.min(game.round, game.setting.maxRound), 60, roundPosition.y)
    drawRoundNumber(game.setting.maxRound, roundPosition.x + roundTitleImageSize.x / 2 + roundSlashImageSize.x / 2 + roundNumberImageSize.x * 3, roundPosition.y)

  }

  /**
   * NOTE align: right
   */
  def drawNumber(format: String, number: Int, x: Int, y: Int, ownerId: Int = -1) {
    val numberImages = ImageLoader.loadNumbers(renderer)
    var drawX = x;
    format.format(number).reverse.foreach { d =>
      drawX -= numberImageSize.x
      renderer.drawImage(numberImages(ownerId, d.toString.toInt), drawX, y)
    }
  }

  def drawVeins() {
    val materialImages = ImageLoader.loadMaterials(renderer)
    val veinImages = ImageLoader.loadVeins(renderer)
    val plusImages = ImageLoader.loadPlusMarks(renderer)
    val numberImages = ImageLoader.loadNumbers(renderer)

    
    for ((p, v) <- game.field.veins) {
      val px = env.trianglePointToPixelPoint(p)
      val veinLeft = px.x - veinImageSize.x / 2
      val veinTop = px.y - (veinImageSize.y + veinTopMargin) / 2
      val veinRight = px.x + veinImageSize.x / 2
      val veinBottom = px.y + (veinImageSize.y + veinTopMargin) / 2

      renderer.drawImage(materialImages(v.material), px.x - materialImageSize.x / 2, px.y - materialImageSize.y / 2)
      renderer.drawImage(veinImages(v.ownerId), veinLeft, veinTop + veinTopMargin)

      /* Draw plus images according to ranks */
      for (i <- 1 to 3) {
        renderer.drawImage(plusImages(if (v.materialRank >= i) v.ownerId else -1), veinLeft + (i - 2) * plusImageSize.x, veinTop + veinTopMargin)
        renderer.drawImage(plusImages(if (v.robotRank >= i) v.ownerId else -1), veinRight + (i - 3) * plusImageSize.x, veinTop + veinTopMargin)
      }
      drawNumber("%02d", v.materialIncome,
        veinLeft - 3 + plusImageSize.x * 2,
        veinTop + veinTopMargin + plusImageSize.y + 1,
        v.ownerId)
      drawNumber("%02d", v.robotIncome,
        veinRight - 8 + plusImageSize.x * 2,
        veinTop + veinTopMargin + plusImageSize.y + 1,
        v.ownerId)

      drawNumber("%03d", v.robot,
        px.x - numberImageSize.x / 2 * 3 + 1 + plusImageSize.x * 3,
        veinBottom - numberImageSize.y,
        v.ownerId)
    }
  }

  def drawPlayers() = {
    val numberImages = ImageLoader.loadNumbers(renderer)
    val infoImages = ImageLoader.loadPlayerInformationBackgrounds(renderer)
    val highlightImage = ImageLoader.loadPlayerHighlightFrame(renderer)
    val activeStarImage = ImageLoader.loadActivePlayerStar(renderer)

    val buyIconImage = ImageLoader.loadBuyIcon(renderer)
    val sellIconImage = ImageLoader.loadSellIcon(renderer)

    val infoImageSize = Size(176, 68)
    val infoMargin = 0
    val infoAreaX = if (env.tileSize == 32) 672 else 992
    val infoAreaY = 0

    val pid2Material2Income = game.field.totalMaterialIncomes
    val pid2VeinRobotAndIncome = game.field.totalVeinRobotAndIncomes
    val pid2SquadRobot = game.field.totalSquadRobots

    for (player <- game.players) {
      // playerArea: area for individual players
      val playerAreaTop = infoAreaY + (infoImageSize.y + infoMargin) * player.id
      renderer.drawImage(infoImages(player.id),
        infoAreaX,
        playerAreaTop)

      /* Highlight active player */
      if (player == game.currentPlayer) {
        renderer.drawImage(highlightImage,
          infoAreaX,
          playerAreaTop)
        renderer.drawImage(activeStarImage,
          infoAreaX - 32,
          playerAreaTop)

      }

      /* Name */
      val g = renderer.getPanel().getGraphics()
      renderer.drawString(player.name,
        infoAreaX + 5,
        playerAreaTop + 14, // 14 -> font size
        Color.WHITE, font)

      /* Status */

      /* vein Count */
      {

        val x = infoAreaX + infoImageSize.x + 106
        var numX = x
        val y = playerAreaTop + 5

        game.field.countVeins(player.id).toString.reverse.foreach { n =>
          renderer.drawImage(numberImages(-1, n.toString.toInt), numX, y)
          numX -= numberImageSize.x
        }
      }

      /* money */
      {
        val x = infoAreaX + 186
        var numX = x
        val y = playerAreaTop + 19

        player.getMoney.toString.reverse.foreach { n =>
          renderer.drawImage(numberImages(-1, n.toString.toInt), numX, y)
          numX -= numberImageSize.x
        }
      }

      /* totalmoney */
      {
        val x = infoAreaX + 282
        var numX = x
        val y = playerAreaTop + 19

        game.getTotalMoneyWhenSellingAllMaterials(player.id).toString.reverse.foreach { n =>
          renderer.drawImage(numberImages(-1, n.toString.toInt), numX, y)
          numX -= numberImageSize.x
        }
      }

      val offsetX = 0
      val offsetY = 5
      for (i <- 0 to 2) {
        val y = playerAreaTop + (infoImageSize.y / 5) * (i + 2) - 1 * i // -1*i because of higher top area

        /* material info (number of materials, number of veins for that material) */
        val materialInfo = List(
          player.materials.getOrElse(Material.all(i), 0),
          pid2Material2Income(player.id)(Material.all(i)))

        for (j <- 0 to 1) {
          val x = infoAreaX + 55 + 48 * j - numberImageSize.x

          var numX = x
          materialInfo(j).toString.reverse.foreach { n =>
            renderer.drawImage(numberImages(-1, n.toString.toInt), numX + offsetX, y + offsetY)
            numX -= numberImageSize.x
          }
        }
      }

      /* Total robots and veins */
      val totalrobot = pid2VeinRobotAndIncome(player.id)._1 + pid2SquadRobot(player.id)
      val numbers = List(totalrobot, pid2VeinRobotAndIncome(player.id)._2)
      for (j <- 0 to 1) {
        val y = playerAreaTop + (infoImageSize.y / 5) + 1
        val x = infoAreaX + 55 + 48 * j - numberImageSize.x

        var numX = x
        numbers(j).toString.reverse.foreach { n =>
          renderer.drawImage(numberImages(-1, n.toString.toInt), numX + offsetX, y + offsetY)
          numX -= numberImageSize.x
        }
      }

      /* Trade */

      for (i <- 0 to 2) {
        val y = playerAreaTop + (infoImageSize.y / 5) * (i + 2) - 1 * i // -1*i because of higher top area

        /* material info (number of materials, number of veins for that material) */
        val materialInfo = List(
          player.materials.getOrElse(Material.all(i), 0),
          pid2Material2Income(player.id)(Material.all(i)))

        /* offer */
        for (j <- 0 to 1) {
          val trade = if (j == 0) game.getOffer(player.id, Material.all(i)) else game.getDemand(player.id, Material.all(i))
          if (trade != null) {

            if (j == 0) {
              renderer.drawImage(sellIconImage, infoAreaX + 110, y + 5)
            } else {
              renderer.drawImage(buyIconImage, infoAreaX + 110, y + 5)
            }

            {
              val x = infoAreaX + 169
              var numX = x

              trade.amount.toString.reverse.foreach { n =>
                renderer.drawImage(numberImages(-1, n.toString.toInt), numX + offsetX, y + offsetY)
                numX -= numberImageSize.x
              }
            }
            {
              val x = infoAreaX + 212
              var numX = x

              trade.price.toString.reverse.foreach { n =>
                renderer.drawImage(numberImages(-1, n.toString.toInt), numX + offsetX, y + offsetY)
                numX -= numberImageSize.x
              }
            }
            {
              val x = infoAreaX + 279
              var numX = x

              (trade.amount * trade.price).toString.reverse.foreach { n =>
                renderer.drawImage(numberImages(-1, n.toString.toInt), numX + offsetX, y + offsetY)
                numX -= numberImageSize.x
              }
            }
          }
        }

      }

    }

    (infoAreaX, infoAreaY + (infoImageSize.y + infoMargin) * game.players.size)
  }

  def drawSquads() {
    val robotImages = ImageLoader.loadRobots(renderer)
    val numberImages = ImageLoader.loadNumbers(renderer)

    for (s <- game.field.squads) {
      val px = env.trianglePointToPixelPoint(s.current)

      val squadImageWidth = robotImageSize.x + numberImageSize.x * 3 + 1
      val squadImageX = px.x - squadImageWidth / 2
      val squadImageY = if (game.field.veins.toMap.contains(s.current))
        px.y - (veinImageSize.y + veinTopMargin) / 2
      else
        px.y - robotImageSize.y / 2

      renderer.drawImage(robotImages(s.ownerId), squadImageX, squadImageY)
      /* Draw robots */
      var numberX = squadImageX + robotImageSize.x + 1
      var numberY = squadImageY
      "%03d".format(s.robot).foreach { d =>
        renderer.drawImage(numberImages(s.ownerId, d.toString.toInt), numberX, numberY)
        numberX += plusImageSize.x
      }
    }
  }

  def drawBank(x: Int, y: Int) {
    val infoImage = ImageLoader.loadBankInformationBackground(renderer)
    val numberImages = ImageLoader.loadNumbers(renderer)

    renderer.drawImage(infoImage, x, y)
    // TODO: 

    for (i <- 0 to 2) {

      val numY = y + 12 * i + 35

      {
        var numX = x + 212

        game.alienTrade.sellPriceOf(Material.all(i)).toString.reverse.foreach { n =>
          renderer.drawImage(numberImages(-1, n.toString.toInt), numX, numY)
          numX -= numberImageSize.x
        }
      }

      {
        var numX = x + 280

        game.alienTrade.buyPriceOf(Material.all(i)).toString.reverse.foreach { n =>
          renderer.drawImage(numberImages(-1, n.toString.toInt), numX, numY)
          numX -= numberImageSize.x
        }
      }

    }

    //game.bank.buyPriceOf(material)
    //game.bank.sellPriceOf(game, material)
  }

  override def draw() {
    val backImg = ImageLoader.loadBackgrounds(renderer)(env.tileSize)
    renderer.drawImage(backImg, 0, 0)

    if (game == null) return

    val veinSample = ImageLoader.loadVeinSample(renderer)
    renderer.drawImage(veinSample, 0, 425)
    
    drawTile()
    
    drawRound()

    drawVeins()

    drawSquads()

    val (nextX, nextY) = drawPlayers()

    drawBank(nextX, nextY)
  }
}

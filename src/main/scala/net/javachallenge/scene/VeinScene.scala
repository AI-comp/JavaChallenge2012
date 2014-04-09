package net.javachallenge.scene

import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.GameEnvironment
import net.javachallenge.util.misc.IndexStr
import net.javachallenge.entity.Player
import net.javachallenge.entity.Game
import net.javachallenge.printer.Printer
import net.javachallenge.util.misc.TrianglePointStr

abstract class VeinScene(val nextScene: Scene[GameEnvironment])
    extends CommandBaseScene {
  var selectCount = 0

  def veins = game.field.veins.toStream

  override def initialize() {
    describe("Generated veins")
    Printer.veins(game, displayLine)
    describe("Select own veins")
    startSelect(game)
  }

  def startSelect(game: Game) {
    displayLine("%s: Choose your vein by number".format(game.currentPlayer.name))
  }

  override def execute(words: List[String]) = {
    try {
      val TrianglePointStr(location) :: Nil = words
      if (!game.field.veins.contains(location)) {
        throw new IllegalArgumentException("Wrong vein location, please try again.")
      }

      val vein = game.field.veins(location)
      if (vein.ownerId != Player.neutralPlayer.id) {
        throw new IllegalArgumentException("The specified vein has been already taken.")
      }
      display("Your choice: ")
      Printer.vein(location, vein, displayLine)
      val (newSelectCount, newGame) = game.occupy(selectCount, location)
      selectCount = newSelectCount
      if (isFinished) {
        (newGame, nextScene)
      } else {
        startSelect(newGame)
        (newGame, this)
      }
    } catch {
      case e: MatchError => {
        displayLine("Wrong format, please try again.")
        (game, this)
      }
      case e: IllegalArgumentException => {
        displayLine(e.getMessage)
        (game, this)
      }
    }
  }

  def isFinished = {
    val nPlayer = game.playerCount
    (selectCount >= nPlayer * 2)
  }
}
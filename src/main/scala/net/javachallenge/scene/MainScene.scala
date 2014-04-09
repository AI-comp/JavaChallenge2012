package net.javachallenge.scene

import net.javachallenge.entity.Game
import net.javachallenge.printer.Printer
import net.javachallenge.util.internationalization.I18n
import net.javachallenge.entity.Material
import net.javachallenge.util.misc.IntStr
import net.javachallenge.util.misc.IndexStr
import net.javachallenge.util.misc.TrianglePointStr
import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.GameEnvironment
import net.javachallenge.entity.Offer
import net.javachallenge.entity.Demand
import net.javachallenge.entity.InvalidCommandException

abstract class MainScene(val nextScene: Scene[GameEnvironment]) extends CommandBaseScene {
  val commands: Map[String, List[String] => Game] =
    Map("h" -> help _, "st" -> serialize _,
      "p" -> players _, "v" -> veins _, "t" -> trades _,
      "sq" -> squads _,
      "bu" -> buy _, "se" -> sell _,
      "o" -> offer _, "d" -> demand _, "ba" -> bank _,
      "l" -> launch _, "u" -> upgrade _, "r" -> rate _,
      "f" -> finish _)

  override def initialize() {
    describe("The game starts!")
    outputTurnInfo(game)
  }

  def outputTurnInfo(game: Game) {
    displayLine("Round = %d, Player = %s".format(game.round, game.currentPlayer))
    displayLine("Please enter a command name and arguments, or just enter 'help'.")
  }

  def help(args: List[String]) = {
    displayLine("You can use the following commands:")
    displayLine("%45s : %s".format("st[ring]", "Show the serialized game instance"))
    displayLine("%45s : %s".format("p[layers]", "Show the player list"))
    displayLine("%45s : %s".format("v[eins]", "Show the vein list"))
    displayLine("%45s : %s".format("sq[uads]", "Show the squad list"))
    displayLine("%45s : %s".format("t[rades]", "Show the offer/demand list"))
    displayLine("%45s : %s".format("r[ate]", "Show the rate of trade with the bank"))
    displayLine("%45s : %s".format("bu[y] <player_id> <material> <amount>", "Buy materials for the given offer"))
    displayLine("%45s : %s".format("se[ll] <player_id> <material> <amount>", "Sell materials for the given demand"))
    displayLine("%45s : %s".format("o[ffer] <material> <amount> <price>", "Make offer for selling the given materials and price of a material"))
    displayLine("%45s : %s".format("d[emand] <material> <amount> <price>", "Make demand for buying the given materials and price of a material"))
    displayLine("%45s : %s".format("ba[nk] <'buy' | 'sell'> <amount>", "Buy materials for the given offer"))
    displayLine("%45s : %s".format("l[aunch] <robot_amount> <from_coord> <to_coord>", "Send a squad for occupying another vein"))
    displayLine("%45s : %s".format("u[pgrade] <material/robot> <vein_coord>", "Upgrade material or power rank of the given vein"))
    displayLine("%45s : %s".format("f[inish]", "Finish your turn and advance to the next turn"))
    displayLine("You shoud specify location without any spaces such as 10,-5.")
    displayLine("Mouse left click: Show the information of the clicked vein or/and squad.")
    displayLine("Mouse right click: Enter the clicked location in the command textbox.")
    game
  }

  def execute(commandAndArgs: List[String]) = {
    val command = commandAndArgs.head
    val args = commandAndArgs.tail

    val c1 = command.substring(0, 1)
    val c2 = (command + " ").substring(0, 2)
    val cmd = if (commands.contains(c1)) c1 else c2

    val newGame = commands.get(cmd) match {
      case Some(cmdFunc) => {
        try {
          cmdFunc(args)
        } catch {
          case e: InvalidCommandException =>
            displayLine("Error: " + e.getMessage)
            game
          case e: MatchError =>
            displayLine("Input command is wrong format.")
            game
        }
      }
      case _ => {
        displayLine("error: %s".format(I18n.get("unknown_error") + "(" + command + ")"))
        game
      }
    }
    (newGame, if (!newGame.isEnded) this else nextScene)
  }

  /**
   * Displays the serialized game instance.
   *
   * @param args arguments for this command
   */
  def serialize(args: List[String]) = {
    displayLine(game.toString)
    game
  }

  /**
   * Displays all the squads.
   *
   * @param args arguments for this command
   */
  def squads(args: List[String]) = {
    Printer.squads(game, displayLine)
    game
  }

  /**
   * Displays all the players.
   *
   * @param args arguments for this command
   */
  def players(args: List[String]) = {
    Printer.players(game, displayLine)
    game
  }

  /**
   * Displays all veins.
   *
   * @param args arguments for this command
   */
  def veins(args: List[String]) = {
    Printer.veins(game, displayLine)
    game
  }

  /**
   * Displays all the available trades.
   */
  def trades(args: List[String]) = {
    Printer.trades(game, displayLine)
    game
  }

  /**
   * Buys materials from an existing offer.
   */
  def buy(args: List[String]) = buyOrSell(args, true)

  /**
   * Sells materials to an existing demand.
   */
  def sell(args: List[String]) = buyOrSell(args, false)

  private def buyOrSell(args: List[String], isBuy: Boolean) = {
    val (IntStr(publisherId) :: Material(material) :: IntStr(amount) :: Nil) = args
    game.auctionHall.trades.get((publisherId, material)) match {
      case Some(offer: Offer) if isBuy => game.buy(offer, amount)
      case Some(demand: Demand) if !isBuy => game.sell(demand, amount)
      case _ => throw new IllegalArgumentException("There is no specified trade")
    }
  }

  /**
   * Makes an offer to sell material.
   */
  def offer(args: List[String]) = offerOrDemand(args, true)

  /**
   * Makes a demand to buy material.
   */
  def demand(args: List[String]) = offerOrDemand(args, false)

  def offerOrDemand(args: List[String], isOffer: Boolean) = {
    val (Material(material) :: IntStr(amount) :: IntStr(price) :: Nil) = args
    if (isOffer)
      game.offer(material, amount, price)
    else
      game.demand(material, amount, price)
  }

  /**
   * Sends a squad to the specified vein with given robots.
   *
   * @param args arguments for this command
   */
  def launch(args: List[String]) = {
    val (IntStr(robot) :: TrianglePointStr(from) :: TrianglePointStr(to) :: rest) = args
    if (rest.isEmpty) {
      game.sendSquad(robot, from, to)
    } else {
      game.sendSquad(robot, from :: to :: rest.map { case TrianglePointStr(p) => p })
    }
  }

  def bank(args: List[String]) = {
    val (cmd :: Material(material) :: IntStr(amount) :: Nil) = args
    cmd match {
      case "buy" => {
        game.tradeWithAlien(material, amount, true)
      }
      case "sell" => {
        game.tradeWithAlien(material, amount, false)
      }
    }
  }

  /**
   * Invests a material or robot technology for enhancing the rank.
   *
   * @param args arguments for this command
   */
  def upgrade(args: List[String]) = {
    val (kindStr :: TrianglePointStr(location) :: _) = args
    if (!game.field.veins.contains(location)) {
      throw new IllegalArgumentException("Wrong vein location.")
    }
    val vein = game.field.veins(location)
    kindStr match {
      case "material" => {
        game.upgrade(location, vein, true)
      }
      case "robot" => {
        game.upgrade(location, vein, false)
      }
    }
  }

  def rate(args: List[String]) = {
    Printer.rate(game, displayLine)
    game
  }

  /**
   * Advances this turn to activate the next player.
   *
   * @param args arguments for this command
   */
  def finish(args: List[String]) = {
    val newGame = game.advanceTurn()
    if (!newGame.isEnded) {
      outputTurnInfo(newGame)
    }
    newGame
  }
}
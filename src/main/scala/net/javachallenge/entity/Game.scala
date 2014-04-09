package net.javachallenge.entity

import scala.collection.mutable
import scala.collection.immutable
import net.javachallenge.util.internationalization.I18n
import scala.collection.JavaConverters._
import java.util.Random

/**
 * A case class which represents a whole game states.
 *
 * @constructor Create a new game.
 * @param players the player map of the indexes and the players
 * @param auctionHall the {@link newAuctionHall} instance
 * @param maxRounds the maximum round number
 * @param field the field
 * @param playerIndex the index of the current player
 * @param round the current round number
 */
case class Game(val players: Vector[Player], val auctionHall: AuctionHall, val alienTrade: AlienTrade,
  val field: Field, val setting: GameSetting, val currentPlayerId: Int, val round: Int)
    extends net.javachallenge.api.Game {

  require(players != null, "players shoud be not null.")
  require(players.size > 0, "players has more than or equal to one player.")
  require(auctionHall != null, "auctionHall shoud be not null.")
  require(alienTrade != null, "alienTrade shoud be not null.")
  require(field != null, "field shoud be not null.")
  require(setting != null, "setting shoud be not null.")
  require(currentPlayerId >= 0, "currentPlayerId shoud be grater than or equal to 0.")
  require(round >= 0, "round shoud be grater than or equal to 0.")

  override def getNeutralPlayerId = Player.neutralPlayer.id

  override def getMyPlayerId = currentPlayerId

  override def getPlayers = {
    val javaPlayers: Vector[net.javachallenge.api.Player] = players
    new java.util.ArrayList(javaPlayers.asJava)
  }

  override def getSurvivedPlayers = getSurvivingPlayers

  override def getSurvivingPlayers = {
    val javaPlayers: Vector[net.javachallenge.api.Player] = survivedPlayers
    new java.util.ArrayList(javaPlayers.asJava)
  }

  override def getPlayer(id: Int) = {
    require(0 <= id && id < players.size, "id should be grater than or equal to 0 and less than playerCount");
    players(id)
  }

  override def getMyPlayer = currentPlayer

  override def getAlienTrade = alienTrade

  override def getField = field

  override def getOffer(playerId: Int, material: net.javachallenge.api.Material) = {
    require(0 <= playerId && playerId < players.size, "playerId should be grater than or equal to 0 and less than playerCount");
    require(material != null, "material should be not null.")

    val m = Material(material)
    auctionHall.trades.values.filter(_ match {
      case trade: Offer =>
        trade.publisherId == playerId && trade.material == m
      case _ => false
    }).headOption.getOrElse(null)
  }

  override def getOffers(playerId: Int) = {
    require(0 <= playerId && playerId < players.size, "playerId should be grater than or equal to 0 and less than playerCount");

    val trades: Seq[net.javachallenge.api.PlayerTrade] = auctionHall.trades.values.filter(_ match {
      case trade: Offer =>
        trade.publisherId == playerId
      case _ => false
    }).toSeq
    new java.util.ArrayList(trades.asJava)
  }

  override def getOffers(material: net.javachallenge.api.Material) = {
    require(material != null, "material should be not null.")

    val m = Material(material)
    val trades: Seq[net.javachallenge.api.PlayerTrade] = auctionHall.trades.values.filter(_ match {
      case trade: Offer =>
        trade.material == m
      case _ => false
    }).toSeq
    new java.util.ArrayList(trades.asJava)
  }

  override def getDemand(playerId: Int, material: net.javachallenge.api.Material) = {
    require(0 <= playerId && playerId < players.size, "playerId should be grater than or equal to 0 and less than playerCount");
    require(material != null, "material should be not null.")

    val m = Material(material)
    auctionHall.trades.values.filter(_ match {
      case trade: Demand =>
        trade.publisherId == playerId && trade.material == m
      case _ => false
    }).headOption.getOrElse(null)
  }

  override def getDemands(playerId: Int) = {
    require(0 <= playerId && playerId < players.size, "playerId should be grater than or equal to 0 and less than playerCount");

    val trades: Seq[net.javachallenge.api.PlayerTrade] = auctionHall.trades.values.filter(_ match {
      case trade: Demand =>
        trade.publisherId == playerId
      case _ => false
    }).toSeq
    new java.util.ArrayList(trades.asJava)
  }

  override def getDemands(material: net.javachallenge.api.Material) = {
    require(material != null, "material should be not null.")

    val m = Material(material)
    val trades: Seq[net.javachallenge.api.PlayerTrade] = auctionHall.trades.values.filter(_ match {
      case trade: Demand =>
        trade.material == m
      case _ => false
    }).toSeq
    new java.util.ArrayList(trades.asJava)
  }

  override def getPlayerTrades = {
    val javaTrades: List[net.javachallenge.api.PlayerTrade] = auctionHall.trades.values.toList
    new java.util.ArrayList(javaTrades.asJava)
  }

  override def getSetting = setting

  override def getPlayerCount = players.size

  override def getRound = round

  override def getTotalMoneyWhenSellingAllMaterials(playerId: Int) = {
    require(0 <= playerId && playerId < players.size, "playerId should be grater than or equal to 0 and less than playerCount");

    val p = players(playerId)
    p.money + p.materials.map { case (m, a) => alienTrade.sellPriceOf(m) * a }.sum
  }

  override def isSurvivingPlayer(playerId: Int) = field.veins.exists(_._2.ownerId == playerId)

  def survivedPlayers = players.filter(p => field.veins.exists(_._2.ownerId == p.id))
  def currentPlayer = players(currentPlayerId)
  def playerCount = players.size

  def isEnded = {
    if (round > setting.maxRound)
      true
    else
      survivedPlayers.size <= 1
  }

  def advanceSelectVein(nextPlayerId: Int) = {
    this.copy(currentPlayerId = nextPlayerId)
  }

  /**
   * Advances to the next turn.
   * Ends the last turn and starts the next turn.
   */
  def advanceTurn(): Game = {
    if (isEnded)
      return this

    var game = this
    do {
    println(toString())
      // Change to the next player
      val newPlayerId = (game.currentPlayerId + 1) % game.playerCount
      val newRound = if (game.currentPlayerId == game.playerCount - 1) game.round + 1 else game.round
      if (game.round != newRound) {
        // Process starting the next round
      }
      // Process starting the next turn
      game = if (game.isSurvivingPlayer(game.currentPlayerId)) {
        game
          .earnMaterials()
          .earnRobots()
          .updateTimeToLive()
          .updateAlienTrade()
          .advanceSquads()
          .advanceAuctionHall(newPlayerId)
          .copy(round = newRound, currentPlayerId = newPlayerId)
      } else {
        game
          .updateAlienTrade()
          .advanceSquads()
          .advanceAuctionHall(newPlayerId)
          .copy(round = newRound, currentPlayerId = newPlayerId)
      }
    } while (!game.isEnded && !game.isSurvivingPlayer(game.currentPlayerId))
    if (game.isEnded)
      game.copy(currentPlayerId = (game.currentPlayerId - 1 + game.playerCount) % game.playerCount)
    else
      game
  }

  def clearAuctionHall() = {
    var newGame = this
    for (i <- 0 until players.size) {
      newGame = newGame.advanceAuctionHall(i)
    }
    newGame
  }

  private def earnMaterials() = this.copy(players = players.updated(currentPlayerId, currentPlayer.earnMaterials(this)))
  private def updateTimeToLive() = this.copy(players = players.updated(currentPlayerId, currentPlayer.updateTimeToLive(this)))
  private def earnRobots() = this.copy(field = field.setVeins(currentPlayer.earnRobots(this)))
  private def updateAlienTrade() = this.copy(alienTrade = alienTrade.update(this))
  private def advanceSquads() = this.copy(field = field.advanceSquads(this))
  private def advanceAuctionHall(newPlayerId: Int) = {
    val (newAuctionHall, newPlayer) = auctionHall.clear(players(newPlayerId))
    this.copy(auctionHall = newAuctionHall, players = players.updated(newPlayerId, newPlayer))
  }

  def occupy(selectCount: Int, location: TrianglePoint) = {
    require(selectCount >= 0)
    require(location != null)

    (selectCount + 1, this.copy(field = field.occupy(location, currentPlayerId),
      currentPlayerId = math.max(veinSelectPlayerIndex(selectCount + 1), 0)))
  }

  def veinSelectPlayerIndex(selectCount: Int) = {
    require(selectCount >= 0)

    if (selectCount < playerCount) selectCount else playerCount * 2 - selectCount - 1
  }

  def tradeWithAlien(material: Material, amount: Int, isBuy: Boolean) = {
    require(Material != null)

    val newPlayer = if (isBuy) {
      currentPlayer.buyMaterial(material, amount, alienTrade.buyPriceOf(material))
    } else {
      currentPlayer.sellMaterial(material, amount, alienTrade.sellPriceOf(material))
    }
    this.copy(players = players.updated(currentPlayerId, newPlayer))
  }

  /**
   * Returns a new game sending a squad with the given robot from the vein vein to the given vein.
   * @param robot the robot of the new squad
   * @param from the from location of the new squad
   * @param to the to location of the new squad
   * @return the new filed where a new squad added with the specified parameters
   */
  def sendSquad(robot: Int, from: TrianglePoint, to: TrianglePoint): Game = {
    require(from != null)
    require(to != null)

    sendSquad(robot, from.shortestPath(to))
  }

  /**
   * Returns a new game sending a squad with the given robot and the given path.
   * @param robot the robot of the new squad
   * @param path the locations from the owned vein to other vein
   * @return the new filed where a new squad added with the specified parameters
   */
  def sendSquad(robot: Int, path: List[TrianglePoint]): Game = {
    require(path != null)
    require(!path.isEmpty)

    this.copy(field = field.sendSquad(this, robot, path))
  }

  def upgrade(location: TrianglePoint, vein: Vein, isMaterialRank: Boolean) = {
    require(location != null)
    require(vein != null)

    val (rank, required) =
      if (isMaterialRank)
        (vein.materialRank, setting.materialsForUpgradingMaterial)
      else
        (vein.robotRank, setting.materialsForUpgradingRobot)
    val newField = field.upgrade(location, isMaterialRank)
    val newPlayer = Material.all.foldLeft(currentPlayer)((player, material) =>
      player.changeMaterial(material, -required(rank - 1)(material)))
    this.copy(field = newField, players = players.updated(currentPlayerId, newPlayer))
  }

  def buy(trade: Offer, amount: Int) = {
    val (newAuctionHall, publisher, customer) =
      auctionHall.makeTransaction(this, currentPlayerId, trade, amount)
    val newPlayers = players.updated(publisher.id, publisher)
      .updated(customer.id, customer)
    this.copy(auctionHall = newAuctionHall, players = newPlayers)
  }

  def sell(trade: Demand, amount: Int) = {
    val (newAuctionHall, publisher, customer) =
      auctionHall.makeTransaction(this, currentPlayerId, trade, amount)
    val newPlayers = players.updated(publisher.id, publisher)
      .updated(customer.id, customer)
    this.copy(auctionHall = newAuctionHall, players = newPlayers)
  }

  def offer(material: Material, amount: Int, price: Int) = {
    val (newAuctionHall, newPlayer) =
      auctionHall.addOffer(this, currentPlayerId, material, amount, price)
    this.copy(auctionHall = newAuctionHall, players = players.updated(currentPlayerId, newPlayer))
  }

  def demand(material: Material, amount: Int, price: Int) = {
    val (newAuctionHall, newPlayer) =
      auctionHall.addDemand(this, currentPlayerId, material, amount, price)
    this.copy(auctionHall = newAuctionHall, players = players.updated(currentPlayerId, newPlayer))
  }
  
  def print(num: Int) = {
	  var data=""
	  data += num
	  data += ","
	  data
  }
  override def toString() = {
    var data = ""
    data += print(this.getRound())
    data += print(this.getPlayerCount())
   for(player <- this.players) {
      data += player.name+","
      for(i <- 0 until 3) {
    	  data += print(player.getMaterial(Material.all(i)))
      }
      data += print(player.getMoney())
      data += print(player.getTimeToLive())
    }
    
    	
   	val veinList = this.getField().getVeins()
   val veinSize = veinList.size()
   data += print(veinSize);
   
   for(veincnt <- 0 until veinSize) {
     val vein = veinList.get(veincnt)
     data += print(vein.getOwnerId())
     data += print(vein.getLocation().getX())
     data += print(vein.getLocation().getY())
     data += print(vein.getMaterial().ordinal())
     data += print(vein.getNumberOfRobots())
     data += print(vein.getCurrentMaterialProductivity())
     data += print(vein.getCurrentRobotProductivity())
     data += print(vein.getMaterialRank())
     data += print(vein.getRobotRank())
   }
   
   	val squadList = this.getField().getSquads()
   	val squadSize = squadList.size()
//   	data += "squad"
   	data += print(squadSize)
   	for(squadcnt <- 0 until squadSize) {
   	  val squad = squadList.get(squadcnt)
   	  data += print(squad.getOwnerId())
   	  data += print(squad.getRobot())
   	  val path = squad.getPath()
   	  data += print(path.size())
   	  for(i <- 0 until path.size()) {
   	    data += print(path.get(i).getX())
   	    data += print(path.get(i).getY())
   	  }
   	  
   	  data += print(squad.getCurrentLocation().getX())
   	  data += print(squad.getCurrentLocation().getY())
   	  data += print(squad.getDestinationLocation().getX())
   	  data += print(squad.getDestinationLocation().getY())

   	}
   val alien = this.getAlienTrade()
   for(i <- 0 until 3) {
     data += print(alien.getBuyPriceOf(Material.all(i)) )
     data += print(alien.getSellPriceOf(Material.all(i)) )
   }
   for(mat <- 0 until this.playerCount) {
	  val offerList = this.getOffers(mat)
      val ofSize = offerList.size()
      data += print(ofSize)
      for(i <- 0 until ofSize) {
    	val ptrade = offerList.get(i)
        data += print(ptrade.getPlayerId())
        data += print(ptrade.getAmount())
        data += print(ptrade.getPricePerOneMaterial())
        data += print(ptrade.getMaterial().ordinal())
      }
	  val demandList = this.getDemands(mat)
	  val demSize = demandList.size()
	  data += print(demSize)
	  for(i <- 0 until demSize) {
	    val ptrade = demandList.get(i)
	    data += print(ptrade.getPlayerId())
	    data += print(ptrade.getAmount())
	    data += print(ptrade.getPricePerOneMaterial())
	    data += print(ptrade.getMaterial().ordinal())
	  }
	  
   }
   
   	//return のところ
   data
}
  
}

object Game {
  def apply(names: Iterable[String], setting: GameSetting = GameSetting(), field: Field = null) = {
    val nonNullField = if (field == null) Field(setting, new Random()) else field
    new Game(Vector(names.zipWithIndex.map { case (n, i) => Player(i, n, setting) }.toStream: _*),
      AuctionHall(), AlienTrade(setting), nonNullField, setting, 0, 1)
  }
}

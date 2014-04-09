package net.javachallenge.entity

import scala.collection.mutable
import scala.collection.immutable
import scala.collection.immutable.TreeMap
import scala.collection.immutable.TreeSet
import java.util.Random
import scala.collection.JavaConverters._
import net.javachallenge.entity.ApiConversion._
import com.google.common.collect.Lists
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import net.javachallenge.api.TriangleComparator

/**
 * A case class which represents a filed including veins and squads.
 *
 * @constructor Create a field.
 * @param size the size of the field
 * @param veins the vein map of locations and veins
 * @param squads the squad set
 */
case class Field(val veins: TreeMap[TrianglePoint, Vein], val squads: List[Squad], val validCoords: TreeSet[TrianglePoint])
    extends net.javachallenge.api.Field {

  require(veins != null)
  require(squads != null)
  require(validCoords != null)

  override def getVein(location: net.javachallenge.api.TrianglePoint) = {
    require(location != null)

    veins(location)
  }

  override def getVeins = {
    val javaVeins: Iterable[net.javachallenge.api.Vein] = veins.values
    new java.util.ArrayList(javaVeins.toSeq.asJava)
  }

  override def getVeins(playerId: Int) = {
    val javaVeins: Iterable[net.javachallenge.api.Vein] = veins.values
      .filter(_.ownerId == playerId)
    new java.util.ArrayList(javaVeins.toSeq.asJava)
  }

  override def getVeinMap = {
    val javaVeins: Map[net.javachallenge.api.TrianglePoint, net.javachallenge.api.Vein] = veins
      .map(v => v._1 -> v._2)
    val treeMap: java.util.TreeMap[net.javachallenge.api.TrianglePoint, net.javachallenge.api.Vein] = new java.util.TreeMap(new TriangleComparator)
    treeMap.putAll(javaVeins.asJava)
    treeMap
  }

  override def getVeinMap(playerId: Int) = {
    val javaVeins: Map[net.javachallenge.api.TrianglePoint, net.javachallenge.api.Vein] = veins
      .filter(_._2.ownerId == playerId)
      .toMap
    val treeMap: java.util.TreeMap[net.javachallenge.api.TrianglePoint, net.javachallenge.api.Vein] = new java.util.TreeMap(new TriangleComparator)
    treeMap.putAll(javaVeins.asJava)
    treeMap
  }

  override def getSquads = {
    val javaSquads: List[net.javachallenge.api.Squad] = squads
    new ArrayList(javaSquads.asJava)
  }

  override def getSquads(playerId: Int) = {
    val javaSquads: List[net.javachallenge.api.Squad] = squads
      .filter(_.ownerId == playerId)
    new ArrayList(javaSquads.asJava)
  }

  override def getValidCoords = {
    val javaValidCoords: Set[net.javachallenge.api.TrianglePoint] = validCoords
      .map(p => p)
    new java.util.HashSet(javaValidCoords.asJava)
  }

  override def countVeins(playerId: Int) = veinCounts(playerId)

  override def sumRobots(playerId: Int) = totalVeinRobotAndIncomes(playerId)._1 + totalSquadRobots(playerId)

  override def sumCurrentMaterialProductivity(playerId: Int, material: net.javachallenge.api.Material) = {
    require(material != null)

    totalMaterialIncomes(playerId)(material)
  }

  override def sumCurrentRobotProductivity(playerId: Int) = totalVeinRobotAndIncomes(playerId)._1 + totalSquadRobots(playerId)

  override def getVeinsOfSameOwnerOrderedByDistance(origination: net.javachallenge.api.Vein) = {
    require(origination != null)

    val vs = veins.values
      .filter(v => v != origination && v.ownerId == origination.getOwnerId())
      .map((v: net.javachallenge.api.Vein) => (v, origination.getDistance(v)))
      .toList
    new java.util.ArrayList(vs.sortBy(_._2).map(_._1).asJava)
  }

  override def getVeinsOfOtherOwnersOrderedByDistance(origination: net.javachallenge.api.Vein) = {
    require(origination != null)

    val vs = veins.values
      .filter(v => v != origination && v.ownerId != origination.getOwnerId())
      .map((v: net.javachallenge.api.Vein) => (v, origination.getDistance(v)))
      .toList
    new java.util.ArrayList(vs.sortBy(_._2).map(_._1).asJava)
  }

  def totalMaterialIncomes = {
    veins.groupBy(_._2.ownerId)
      .map {
        case (pid, vs) =>
          (pid, vs.groupBy(_._2.material)
            .map { case (m, vs) => (m, vs.map(_._2.materialIncome).sum) }
            .toMap
            .withDefaultValue(0))
      }
      .toMap
      .withDefaultValue(Map.empty.withDefaultValue(0))
  }

  def veinCounts = {
    veins.groupBy(_._2.ownerId)
      .map {
        case (pid, vs) =>
          (pid, vs.size)
      }
      .toMap
      .withDefaultValue(0)
  }

  def totalVeinRobotAndIncomes = {
    veins.groupBy(_._2.ownerId)
      .map {
        case (pid, vs) =>
          (pid, (vs.map(_._2.robot).sum, vs.map(_._2.robotIncome).sum))
      }
      .toMap
      .withDefaultValue((0, 0))
  }

  def totalSquadRobots = {
    squads.groupBy(_.ownerId)
      .map {
        case (pid, ss) =>
          (pid, ss.map(_.robot).sum)
      }
      .toMap
      .withDefaultValue(0)
  }

  def owingVeins(player: Player) = {
    veins.filter(_._2.ownerId == player.id)
  }

  def occupy(location: TrianglePoint, playerId: Int) = {
    if (!veins.contains(location)) {
      throw new InvalidCommandException("The specified vertex has no vein.")
    }
    this.copy(veins = veins + (location -> veins(location).occupy(playerId)))
  }

  /**
   * Returns a new filed where a new squad added with the specified arguments.
   * Add squad in this field and returns new field.
   * @param game the game instance
   * @param robot the robot of the new squad
   * @param path the path from the vein to the other vein
   * @return the new filed where a new squad added
   */
  def sendSquad(game: Game, robot: Int, path: List[TrianglePoint]) = {
    val from = path.head
    val to = path.last
    if (from == to) {
      throw new InvalidCommandException("You can not select the same vein as a departure and a destination.")
    }
    if (!(veins.contains(from) && veins.contains(to))) {
      throw new InvalidCommandException("You can not select a vertext without a vein.")
    }
    if (!(0 < robot && robot <= veins(from).robot)) {
      throw new InvalidCommandException("You don't have enough robots.")
    }
    if (veins(from).ownerId != game.currentPlayerId) {
      throw new InvalidCommandException("You cannot select other player's vein as a departure.")
    }
    if (path.zip(path.tail).exists { case (p1, p2) => !p1.isConnected(p2) }) {
      throw new InvalidCommandException("You should select a connected path from a depature to a destination.")
    }

    this.copy(squads = Squad(game, robot, path) :: squads,
      veins = veins + (from -> veins(from).changeRobot(-robot)))
  }

  def upgrade(location: TrianglePoint, isMaterialRank: Boolean) = {
    val newVein =
      if (isMaterialRank)
        veins(location).upgradeMaterialRank()
      else
        veins(location).upgradeRobotRank()
    this.copy(veins = veins + (location -> newVein))
  }

  /**
   * Returns a new filed where the specified squad removed
   * @param squad the squad to be removed
   * @return the new filed where the specified squad removed
   */
  def removeSquad(squad: Squad) = {
    this.copy(squads = squads - squad)
  }

  /**
   * Returns a new field where the squads advance.
   * @param game the {@link Game} instance
   * @return the new field where the squads advance
   */
  def advanceSquads(game: Game) = {
    val newSquadsAndVeins = Squad.advanceAll(game)
    this.copy(squads = newSquadsAndVeins._1, veins = newSquadsAndVeins._2)
  }

  /**
   * Returns a new field where the new vein map replaced with old one.
   * @param newVeins the new vein map
   * @return the new field where the new vein map replaced with old one
   */
  def setVeins(newVeins: TreeMap[TrianglePoint, Vein]) = {
    this.copy(veins = newVeins)
  }
}

/**
 * A object containing helper methods for creating new fields.
 */
object Field {
  /**
   * Create a new field with the specified size.
   * @param size the size of the field
   * @return the new field with the specified size
   */
  def apply(setting: GameSetting, rand: Random): Field = {
    val validCoords = getValidCoords(setting.mapSize)
    val coords: mutable.Set[TrianglePoint] = mutable.Set()
    while (coords.size < setting.veinCount) {
      val coord = randomCoods(rand, setting.mapSize)
        .filter(p => validCoords.contains(p))
        .filter(p => !coords.contains(p))
        .filter(p => !coords.exists(p2 => p2.shortestPath(p).size <= 3))
        .head
      coords.add(coord)
    }

    val veins = TreeMap((coords.zipWithIndex.map {
      case (p, i) => {
        val robot = 50 + rand.nextInt(50)
        val rProd = 5 + rand.nextInt(5)
        val mProd = 5 + rand.nextInt(5)
        val material = Material.all(i % Material.all.size)
        (p, Vein(p, material, robot, mProd, rProd))
      }
    }).toStream: _*)
    new Field(veins, List(), validCoords)
  }

  /**
   * Create a new field with the specified size and vein map.
   * @param size the size of the field
   * @param veins the vein map of the locations and the veins
   * @return the new field with the specified size
   */
  def apply(setting: GameSetting, veins: TreeMap[TrianglePoint, Vein]): Field = {
    new Field(veins, List(), getValidCoords(setting.mapSize))
  }

  def getValidCoords(size: Int) = {
    val validCoords: mutable.Set[TrianglePoint] = mutable.Set()
    for (y <- 0 until size) {
      for (x <- Range(-size * 2 + 2 + y, size * 2 - y + 1)) {
        validCoords.add(TrianglePoint(x, y))
        validCoords.add(TrianglePoint(x, y + 1))
        validCoords.add(TrianglePoint(x, -y))
        validCoords.add(TrianglePoint(x, -y + 1))
      }
    }
    TreeSet(validCoords.toStream: _*)
  }

  /**
   * Returns random coordinates for a field with the specified size.
   * @param rand the {@link Random} instance
   * @param size the size of the field
   * @return the random coordinates for a field with the specified size
   */
  def randomCood(rand: Random, size: Int) = {
    val x = rand.nextInt(size * 4) - size * 2 + 1
    val y = rand.nextInt(size * 2) - size + 1
    TrianglePoint(x, y)
  }

  /**
   * Returns random coordinates for a field with the specified size.
   * @param rand the {@link Random} instance
   * @param size the size of the field
   * @return the random coordinates for a field with the specified size
   */
  def randomCoods(rand: Random, size: Int): Stream[TrianglePoint] = {
    randomCood(rand, size) #:: randomCoods(rand, size)
  }
}

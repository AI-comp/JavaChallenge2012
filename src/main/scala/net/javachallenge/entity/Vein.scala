package net.javachallenge.entity

import net.javachallenge.util.internationalization.I18n
import scala.math._
import scala.util._

/**
 * A vein in the map.
 * @param owner the owner of the vein
 * @param materialRank the current mining level of the vein
 * @param robotRank the current productivity level of the vein
 */
case class Vein(val location: TrianglePoint, val ownerId: Int, val material: Material, val robot: Int,
  val materialProductivity: Int, val robotProductivity: Int, val materialRank: Int, val robotRank: Int)
    extends net.javachallenge.api.Vein {
  require(robot >= 0, "robot must be greater than or eqaul to zero")
  require(robotProductivity >= 0, "robotProductivity must be greater than or equal to zero")
  require(materialProductivity >= 0, "materialProductivity must be greater than or equal to zero")
  require(1 <= robotRank && robotRank <= 3, "robotRank must be greater than 0 and less than 4")
  require(1 <= materialRank && materialRank <= 3, "materialRank must be greater than 0 and less than 4")

  override def getOwnerId = ownerId
  override def getLocation = location
  override def getMaterial = material
  override def getNumberOfRobots = robot
  override def getCurrentMaterialProductivity = materialIncome
  override def getCurrentRobotProductivity = robotIncome
  override def getInitialMaterialProductivity = materialProductivity
  override def getInitialRobotProductivity = robotProductivity
  override def getMaterialRank = materialRank
  override def getRobotRank = robotRank
  override def getDistance(to: net.javachallenge.api.Vein) = location.getDistance(to.getLocation())
  override def getShortestPath(to: net.javachallenge.api.Vein) = location.getShortestPath(to.getLocation())

  def materialIncome = materialProductivity * (1 << (materialRank - 1))

  def robotIncome = robotProductivity * (1 << (robotRank - 1))

  def owner(game: Game) = game.players(ownerId)

  def earnRobot() = this.copy(robot = robot + robotIncome)

  def changeRobot(addedRobot: Int) = this.copy(robot = robot + addedRobot)

  def conquer(squad: Squad) = {
    require(ownerId != squad.ownerId)
    this.copy(ownerId = squad.ownerId, robot = squad.robot - robot, materialRank = max(1, materialRank - 1), robotRank = max(1, robotRank - 1))
  }

  def occupy(playerId: Int) = {
    if (ownerId != Player.neutralPlayer.id) {
      throw new InvalidCommandException("The specified vein has been already taken.")
    }
    this.copy(ownerId = playerId)
  }

  /**
   * Upgrades material rank.
   *
   * @param new material rank
   */
  def upgradeMaterialRank() = {
    if (materialRank == 3) {
      throw new InvalidCommandException("The material rank is already maximum.")
    }
    this.copy(materialRank = materialRank + 1)
  }

  /**
   * Upgrades robot rank.
   *
   * @param new robot rank
   */
  def upgradeRobotRank() = {
    if (robotRank == 3) {
      throw new InvalidCommandException("The robot rank is already maximum.")
    }
    this.copy(robotRank = robotRank + 1)
  }

  /**
   * Returns vein information in String
   *
   * @return vein information in String
   */
  override def toString: String = {
    "owner=" + ownerId +
      " material=" + material +
      " robot=" + robot +
      " materialRank=" + materialRank +
      " robotRank=" + robotRank +
      " materialProductivity=" + materialProductivity +
      " robotProductivity=" + robotProductivity
  }
}

object Vein {
  def apply(location: TrianglePoint, material: Material, robot: Int, materialProductivity: Int, robotProductivity: Int) = {
    new Vein(location, Player.neutralPlayer.id, material, robot, materialProductivity, robotProductivity, 1, 1)
  }
}
package net.javachallenge.entity

import scala.collection.mutable
import scala.collection.immutable
import scala.collection.immutable.TreeMap
import scala.collection.JavaConverters._

/**
 * A case class which represents a squad.
 * @constructor Create a new squad with the specified arguments.
 * @param ownerId the owner id of the squad
 * @param robot the robot amount
 * @param path the path from the current position to the goal
 * @param onRoadTurn the number of the consumed turns to walk a side of a tile
 */
case class Squad(val ownerId: Int, val robot: Int, val onRoadRemainingTurn: Int, val path: List[TrianglePoint])
    extends net.javachallenge.api.Squad {
  require(robot > 0, "robot must be greater than zero")
  require(onRoadRemainingTurn > 0, "onRoadRemainingTurn must be greater than zero")

  override def getOwnerId() = ownerId

  override def getRobot() = robot

  override def getPath() = {
    val ps: List[net.javachallenge.api.TrianglePoint] = path
    new java.util.ArrayList(ps.asJava)
  }

  override def getCurrentLocation() = path.head

  override def getDestinationLocation() = path.last

  /**
   * The current location on the path.
   *
   * @return the current location
   */
  def current = path.head

  /**
   * Returns the value which indicates whether this squad is on a vertex.
   *
   * @return the value which indicates whether this squad is on a vertex
   */
  def onVertex(game: Game) = onRoadRemainingTurn == game.setting.moveTurn

  def owner(game: Game) = game.players(ownerId)

  /**
   * Returns the value which indicates whether this squad is conflicted enemy squads
   * on a vertex.
   *
   * @return the value which indicates whether this squad is conflicted enemy squads
   * on a vertex
   */
  private def conflictedOnVertex(game: Game, that: Squad) = {
    onRoadRemainingTurn + that.onRoadRemainingTurn == game.setting.moveTurn * 2 &&
      current == that.current
  }

  /**
   * Returns the value which indicates whether this squad is conflicted enemy squads
   * on a road.
   *
   * @return the value which indicates whether this squad is conflicted enemy squads
   * on a road
   */
  private def conflictedOnRoad(game: Game, that: Squad) = {
    onRoadRemainingTurn + that.onRoadRemainingTurn == game.setting.moveTurn &&
      current.isConnected(that.current) &&
      current == that.path.tail.head && path.tail.head == that.current
  }
}

object Squad {

  def apply(game: Game, robot: Int, path: List[TrianglePoint]) = {
    new Squad(game.currentPlayerId, robot, game.setting.moveTurn, path)
  }

  /**
   * Advances the all squads of the current player.
   *
   * @param game the game instance containing whole states
   */
  def advanceAll(game: Game) = {
    val newVeins = mutable.Map(game.field.veins.toStream: _*)
    val friends = game.field.squads.filter(_.ownerId == game.currentPlayerId)
    val enemies = game.field.squads.filter(_.ownerId != game.currentPlayerId)
    val (newFriends, newEnemies) = battle(game, move(game, friends), enemies, newVeins)
    (newFriends ++ newEnemies, TreeMap(newVeins.toStream: _*))
  }

  /**
   * Moves the all squads of the current player.
   *
   * @param ownSquads the squads of the current player
   * @param newSquads new squads of the current player
   */
  private def move(game: Game, squads: List[Squad], newSquads: List[Squad] = List()): List[Squad] = {
    squads match {
      case squad :: rest => {
        val newSquad = if (squad.ownerId == game.currentPlayerId) {
          var onRoadRemainingTurn = squad.onRoadRemainingTurn - 1
          var path = squad.path
          if (onRoadRemainingTurn == 0) {
            path = path.tail
            onRoadRemainingTurn = game.setting.moveTurn
          }
          squad.copy(path = path, onRoadRemainingTurn = onRoadRemainingTurn)
        } else {
          squad
        }
        move(game, rest, newSquad :: newSquads)
      }
      case Nil => {
        newSquads
      }
    }
  }

  /**
   * Battles the all squads of the current player with enemy squads.
   *
   * @param game the game instance containing whole states
   * @param ownSquads the squads of the current player
   * @param newSquads new squads of the current player
   */
  private def battle(game: Game, friends: List[Squad], enemies: List[Squad], newVeins: mutable.Map[TrianglePoint, Vein], newFriends: List[Squad] = List()): (List[Squad], List[Squad]) = {
    friends match {
      case squad :: rest => {
        if (squad.path.size == 1) {
          newVeins += squad.current -> reach(game, squad, newVeins)
          battle(game, rest, enemies, newVeins, newFriends)
        } else {
          val (newrobot, updatedEnemies) =
            if (squad.onVertex(game))
              conflict(game, squad.robot, enemies, squad.conflictedOnVertex(game, _))
            else
              conflict(game, squad.robot, enemies, squad.conflictedOnRoad(game, _))
          val updatedFriends = if (newrobot > 0) {
            (squad.copy(robot = newrobot) :: newFriends)
          } else {
            newFriends
          }
          battle(game, rest, updatedEnemies, newVeins, updatedFriends)
        }
      }
      case Nil => {
        (newFriends, enemies)
      }
    }
  }

  /**
   * Processes that the given squads conflicts the enemy squads.
   *
   * @param squad the squad conflicting the enemy squads
   * @param enemySquads the squads of the other players
   * @param newSquads new squads of the current player
   */
  private def conflict(game: Game, robot: Int, enemies: List[Squad], conflicted: Squad => Boolean, newEnemies: List[Squad] = List()): (Int, List[Squad]) = {
    if (robot <= 0) {
      (robot, enemies ++ newEnemies)
    } else {
      enemies match {
        case enemy :: rest => {
          if (conflicted(enemy)) {
            var enemyrobot = enemy.robot
            val damage = math.min(robot, enemyrobot)
            val newrobot = robot - damage
            enemyrobot -= damage
            val updatedEnemies = if (enemyrobot > 0) {
              enemy.copy(robot = enemyrobot) :: newEnemies
            } else {
              newEnemies
            }
            conflict(game, newrobot, rest, conflicted, updatedEnemies)
          } else {
            conflict(game, robot, rest, conflicted, enemy :: newEnemies)
          }
        }
        case Nil => {
          (robot, newEnemies)
        }
      }
    }
  }

  /**
   * Processes that the given squad reaches the veins.
   *
   * @param game the game instance containing whole states
   * @param squad the squad reaching the vein
   */
  private def reach(game: Game, squad: Squad, newVeins: mutable.Map[TrianglePoint, Vein]) = {
    val vein = newVeins(squad.current)
    if (vein.ownerId == game.currentPlayerId) {
      reachOwnVein(squad, vein)
    } else {
      reachEnemyVein(squad, vein)
    }
  }

  /**
   * Processes that the given squad reaches the specified own vein.
   *
   * @param squad the squad reaching the vein
   * @param vein the own vein where the squad reaches
   */
  private def reachOwnVein(squad: Squad, vein: Vein) = {
    vein.changeRobot(squad.robot)
  }

  /**
   * Processes that the given squad reaches the specified enemy vein.
   *
   * @param squad the squad reaching the vein
   * @param vein the enemy vein where the squad reaches
   */
  private def reachEnemyVein(squad: Squad, vein: Vein) = {
    if (vein.robot >= squad.robot) {
      vein.changeRobot(-squad.robot)
    } else {
      vein.conquer(squad)
    }
  }
}

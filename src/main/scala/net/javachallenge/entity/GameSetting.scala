package net.javachallenge.entity

import net.javachallenge.api.GameSettingBuilder
import scala.collection.JavaConverters._

/**
 * @constructor
 * @param initialMaterials the quantity of material owned by players when starting the game.
 */
@SerialVersionUID(0l)
case class GameSetting(val maxRound: Int = 200, val initialMoney: Int = 0,
  val initialMaterials: Map[Material, Int] = Map(Gas -> 0, Stone -> 0, Metal -> 0),
  val materialsForUpgradingMaterial: Vector[Map[Material, Int]] = Vector(Map(Gas -> 200, Stone -> 100, Metal -> 0),
    Map(Gas -> 100, Stone -> 300, Metal -> 0)),
  val materialsForUpgradingRobot: Vector[Map[Material, Int]] = Vector(Map(Gas -> 200, Stone -> 0, Metal -> 200),
    Map(Gas -> 0, Stone -> 300, Metal -> 500)),
  val mapSize: Int = 10, val veinCount: Int = 40, val bankMargin: Int = 4, val moveTurn: Int = 1)
    extends net.javachallenge.api.GameSetting {

  require(maxRound > 0, "maxRound must be greater than zero")
  require(initialMoney >= 0, "initialMoney must be greater than or equal to zero")
  require(bankMargin > 0, "bankMargin must be greater than zero")
  require(moveTurn > 0, "moveTurn must be greater than zero")
  require(mapSize > 0, "mapSize must be greater than zero")
  require(veinCount > 0, "veinCount must be greater than zero")

  override def getMaxRound() = maxRound
  override def getInitialMoney() = initialMoney
  override def getInitialMaterial(material: net.javachallenge.api.Material) = initialMaterials(Material(material))
  override def getMapSize() = mapSize
  override def getVeinCount() = veinCount
  override def getAlienTradeMargin() = bankMargin
  override def getMaterialsForUpgradingMaterialRankFrom1To2(m: net.javachallenge.api.Material) = materialsForUpgradingMaterial(0)(m)
  override def getMaterialsForUpgradingMaterialRankFrom2To3(m: net.javachallenge.api.Material) = materialsForUpgradingMaterial(1)(m)
  override def getMaterialsForUpgradingRobotRankFrom1To2(m: net.javachallenge.api.Material) = materialsForUpgradingRobot(0)(m)
  override def getMaterialsForUpgradingRobotRankFrom2To3(m: net.javachallenge.api.Material) = materialsForUpgradingRobot(1)(m)

  def toJavaMap(map: Map[Material, Int]) = map.map(t => {
    val i: java.lang.Integer = t._2;
    t._1 -> i
  }).asJava
}

object GameSetting {
  def build(b: GameSettingBuilder) =
    GameSetting(bankMargin = b.getAlienTradeMargin(), initialMoney = b.getInitialMoney(),
      mapSize = b.getMapSize(), maxRound = b.getMaxRound(), veinCount = b.getVeinCount())
  val defaultInstance = GameSetting()
}
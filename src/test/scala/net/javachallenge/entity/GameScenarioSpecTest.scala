package net.javachallenge.entity

import java.io.StringReader
import java.util.Scanner
import net.javachallenge.GameEnvironment
import net.javachallenge.scene.console._
import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner.{ JUnitSuiteRunner, JUnit }
import scala.collection.mutable
import scala.collection.immutable
import scala.collection.mutable.MutableList
import scala.collection.mutable.Queue
import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.scene.CommandBaseScene
import net.javachallenge.scene.VeinScene
import net.javachallenge.scene.MainScene
import scala.collection.immutable.TreeMap

@RunWith(classOf[JUnitSuiteRunner])
class GameScenarioSpecTest extends Specification with JUnit {

  "Game" should {

    val names = List("A", "B", "C")
    val p11 = TrianglePoint(-5, 3)
    val p12 = TrianglePoint(6, 0)
    val p21 = TrianglePoint(-3, 3)
    val p22 = TrianglePoint(0, -1)
    val p31 = TrianglePoint(4, 0)
    val p32 = TrianglePoint(2, 0)
    val p4 = TrianglePoint(7, 3)
    val p5 = TrianglePoint(5, -2)
    val p6 = TrianglePoint(6, 2)

    val veins = TreeMap(p11 -> Vein(p11, Gas, 10, 10, 1),
      p12 -> Vein(p12, Stone, 10, 10, 1),
      p21 -> Vein(p21, Stone, 10, 10, 1),
      p22 -> Vein(p22, Gas, 10, 10, 1),
      p31 -> Vein(p31, Metal, 10, 10, 1),
      p32 -> Vein(p32, Metal, 10, 10, 1),
      p4 -> Vein(p4, Metal, 10, 10, 1),
      p5 -> Vein(p5, Metal, 10, 10, 1),
      p6 -> Vein(p6, Stone, 10, 10, 1))

    val settings = GameSetting(veinCount = 10, moveTurn = 1, initialMoney = 100)
    val env: GameEnvironment = GameEnvironment(game = Game(names, settings, Field(settings, veins)))
    val man = env.getSceneManager().setFps(1000);
    def game = env.game

    val mainScene = new MainScene(null) with TestScene
    val veinScene = new VeinScene(mainScene) with TestScene
    man.initialize(env, veinScene)

    def run(scene: Scene[GameEnvironment], command: String) = {
      TestScene.push(command.split(" ").toList)
      man.runOneStep(env, scene)
    }

    def runMain(command: String) = {
      run(mainScene, command)
    }

    run(veinScene, p11.cmdStr)
    run(veinScene, p21.cmdStr)
    run(veinScene, p31.cmdStr)
    run(veinScene, p32.cmdStr)
    run(veinScene, p22.cmdStr)
    run(veinScene, p12.cmdStr)

    runMain("finish")
    runMain("finish")
    runMain("finish")

    "advance turns for increasing materials" in {
      val ps = game.players
      ps(0).materials must_== mutable.Map(Metal -> 0, Gas -> 10, Stone -> 10)
      game.field.veins(p11).robot must_== 11
      game.field.veins(p12).robot must_== 11
      ps(1).materials must_== mutable.Map(Metal -> 0, Gas -> 10, Stone -> 10)
      game.field.veins(p21).robot must_== 11
      game.field.veins(p22).robot must_== 11
      ps(2).materials must_== mutable.Map(Metal -> 20, Gas -> 0, Stone -> 0)
      game.field.veins(p31).robot must_== 11
      game.field.veins(p32).robot must_== 11
    }

    "advance turns changing current player" in {
      game.currentPlayer must_== game.players(0)
      runMain("finish")
      game.currentPlayer must_== game.players(1)
      runMain("finish")
      game.currentPlayer must_== game.players(2)
      runMain("finish")
      game.currentPlayer must_== game.players(0)
    }

    "get initial money" in {
      game.players(0).money must_== game.setting.initialMoney
    }

    "trade materials" in {
      runMain("demand gas 10 1")
      runMain("finish")
      game.players(0).materials must_== mutable.Map(Metal -> 0, Gas -> 20, Stone -> 20)
      game.players(0).money must_== settings.initialMoney - 10
      runMain("sell 0 gas 10")
      runMain("finish")
      runMain("offer metal 20 1")
      runMain("finish")
      game.players(2).materials must_== mutable.Map(Metal -> 20, Gas -> 0, Stone -> 0)
      game.players(2).money must_== settings.initialMoney
      runMain("buy 2 metal 10")
      runMain("finish")
      runMain("buy 2 metal 10")
      runMain("finish")
      val ps = game.players
      ps(0).materials must_== mutable.Map(Metal -> 10, Gas -> 40, Stone -> 30)
      ps(0).money must_== settings.initialMoney - 20
      ps(1).materials must_== mutable.Map(Metal -> 10, Gas -> 20, Stone -> 30)
      ps(1).money must_== settings.initialMoney
      ps(2).materials must_== mutable.Map(Metal -> 20, Gas -> 0, Stone -> 0)
      ps(2).money must_== settings.initialMoney + 20
    }

    "launch squads to a own vein" in {
      runMain("finish")
      runMain("finish")

      game.field.squads.size must_== 0
      runMain("launch 10 " + p31.cmdStr + " " + p32.cmdStr)
      game.field.squads.size must_== 1
      game.field.veins(p31).robot must_== 1
      game.field.veins(p31).ownerId must_== 2
      game.field.veins(p32).robot must_== 11
      game.field.veins(p32).ownerId must_== 2

      for (i <- 0 until 3) runMain("finish")

      game.field.squads.size must_== 1
      game.field.veins(p31).robot must_== 2
      game.field.veins(p31).ownerId must_== 2
      game.field.veins(p32).robot must_== 12
      game.field.veins(p32).ownerId must_== 2

      for (i <- 0 until 3) runMain("finish")

      game.field.squads.size must_== 0
      game.field.veins(p31).robot must_== 3
      game.field.veins(p31).ownerId must_== 2
      game.field.veins(p32).robot must_== 23
      game.field.veins(p32).ownerId must_== 2
    }

    "launch squads to a neutral vein" in {
      runMain("launch 10 " + p12.cmdStr + " " + p6.cmdStr)
      for (i <- 0 until 3) runMain("finish")

      runMain("launch 1 " + p12.cmdStr + " " + p6.cmdStr)
      for (i <- 0 until 3) runMain("finish")

      for (i <- 0 until 2) {
        game.field.squads.size must_== 2
        game.field.veins(p6).robot must_== 10
        for (i <- 0 until 3) runMain("finish")
      }

      game.field.squads.size must_== 1
      game.field.veins(p6).robot must_== 0
      game.field.veins(p6).ownerId must_== -1

      for (i <- 0 until 3) runMain("finish")

      game.field.squads.size must_== 0
      game.field.veins(p6).robot must_== 1
      game.field.veins(p6).ownerId must_== 0
    }

    "launch squads to not neutral veins" in {
      runMain("launch 10 " + p11.cmdStr + " " + p21.cmdStr)
      runMain("finish")
      runMain("launch 10 " + p21.cmdStr + " " + p4.cmdStr)
      runMain("launch 1 " + p21.cmdStr + " " + p11.cmdStr)

      game.field.squads.size must_== 3
      runMain("finish")
      game.field.squads.size must_== 2
      runMain("finish")
      game.field.veins(p11).robot must_== 2
      game.field.veins(p21).robot must_== 1

      for (i <- 0 until 3) runMain("finish")
      game.field.veins(p11).ownerId must_== 0
      game.field.veins(p11).robot must_== 3
      game.field.veins(p21).ownerId must_== 0
      game.field.veins(p21).robot must_== 8
    }
  }
}
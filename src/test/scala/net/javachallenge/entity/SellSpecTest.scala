package net.javachallenge.entity

import net.javachallenge.GameEnvironment
import net.javachallenge.scene.console._
import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner.{ JUnitSuiteRunner, JUnit }
import scala.collection.mutable
import scala.collection.immutable
import scala.collection.mutable.Queue
import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.scene.CommandBaseScene
import net.javachallenge.scene.VeinScene
import net.javachallenge.scene.MainScene
import scala.collection.immutable.TreeMap

@RunWith(classOf[JUnitSuiteRunner])
class SellSpecTest extends Specification with JUnit {
  "GameScenario" should {
    val names = List("hoge", "foo", "bar")
    val p11 = TrianglePoint(-5, 3)
    val p12 = TrianglePoint(6, 0)
    val p21 = TrianglePoint(-3, 3)
    val p22 = TrianglePoint(0, -1)
    val p31 = TrianglePoint(-7, 0)
    val p32 = TrianglePoint(4, -2)
    val p4 = TrianglePoint(7, 2)
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

    val settings = GameSetting(veinCount = 10, moveTurn = 1)
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

    "sell material when no demand" in {
      game.currentPlayer must_== game.players(0)
      runMain("sell 1 10")
      runMain("finish")
      game.currentPlayer must_== game.players(1)
    }

  }
}
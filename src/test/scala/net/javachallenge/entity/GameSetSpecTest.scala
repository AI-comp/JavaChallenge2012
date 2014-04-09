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
class GameSetSpecTest extends Specification with JUnit {
  "GameScenario" should {

    val names = List("hoge", "piyo")
    val p11 = TrianglePoint(0, 0)
    val p12 = TrianglePoint(1, 0)
    val p21 = TrianglePoint(-1, 0)
    val p22 = TrianglePoint(2, 0)

    val veins = TreeMap(
      p11 -> Vein(p11, Gas, 1000, 1, 10),
      p12 -> Vein(p12, Stone, 1000, 1, 10),
      p21 -> Vein(p21, Stone, 10, 1, 10),
      p22 -> Vein(p22, Gas, 10, 1, 10))

    val settings = GameSetting(veinCount = 4, moveTurn = 1)
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
    run(veinScene, p22.cmdStr)
    run(veinScene, p12.cmdStr)

    "game set when conquested" in {
      runMain("launch 1000 " + p11.cmdStr + " " + p21.cmdStr)
      runMain("launch 1000 " + p12.cmdStr + " " + p22.cmdStr)
      val nextScene = runMain("finish")

      game.field.veins(p21).ownerId must_== 0
      game.field.veins(p22).ownerId must_== 0
      game.currentPlayerId must_== 0

      nextScene must_!= mainScene
    }

    "game set when time over" in {
      for (n <- (0 until game.setting.maxRound * 2 - 1)) {
        runMain("finish")
      }

      game.currentPlayerId must_== 1

      runMain("finish") must_!= mainScene
    }
  }
}
package net.javachallenge.runner

import org.specs._
import org.specs.matcher._
import org.specs.runner.{ JUnitSuiteRunner, JUnit }
import org.junit.runner.RunWith
import net.javachallenge.scene.MainScene
import net.javachallenge.scene.VeinScene
import net.javachallenge.scene.console.ConsoleScene
import net.javachallenge.scene.MainRunnerScene
import net.javachallenge.scene.InitialRunnerScene
import net.javachallenge.GameEnvironment
import net.javachallenge.entity.Game
import net.javachallenge.RunnerInitializer
import net.javachallenge.entity.Field
import net.javachallenge.scene.ResultScene
import net.javachallenge.scene.ResultScene
import net.javachallenge.scene.EmptyScene

@RunWith(classOf[JUnitSuiteRunner])
class ReplaySpecTest extends Specification with JUnit {
  val env = GameEnvironment()
  "Replay runner" should {
    "read a replay file" in {
      val fileName = "replay/2012_11_16_16_32__tokoharuAI_Sabateur_JoeJack_near_player_Wand_Player_Myu.rep"
      val (irs, mrs, names, settings, random) = RunnerInitializer.initializeReplay(fileName)
      env.game = Game(names, settings, Field(settings, random))
      val man = env.getSceneManager.setFps(9999)
      val endScene = new EmptyScene(null) with ResultScene with ConsoleScene
      val mainScene = new MainScene(endScene) with ConsoleScene with MainRunnerScene
      val veinScene = new VeinScene(mainScene) with ConsoleScene with InitialRunnerScene
      mainScene.runners = Vector(mrs: _*)
      veinScene.runners = Vector(irs: _*)

      man.initialize(env, veinScene)
      while (man.runOneStep(env, veinScene) == veinScene) {}
      while (man.runOneStep(env, mainScene) == mainScene) {}
      mainScene.game.field.countVeins(0) must_== 10
      mainScene.game.field.countVeins(1) must_== 30
      mainScene.game.field.countVeins(2) must_== 0
      mainScene.game.field.countVeins(3) must_== 0
      mainScene.game.field.countVeins(4) must_== 0
      mainScene.game.field.countVeins(5) must_== 0
      mainScene.game.field.sumRobots(0) must_== 5643
      mainScene.game.field.sumRobots(1) must_== 15466
      mainScene.game.field.sumRobots(2) must_== 0
      mainScene.game.field.sumRobots(3) must_== 0
      mainScene.game.field.sumRobots(4) must_== 0
      mainScene.game.field.sumRobots(5) must_== 0
      mainScene.game.getTotalMoneyWhenSellingAllMaterials(0) must_== 383037
      mainScene.game.getTotalMoneyWhenSellingAllMaterials(1) must_== 207339
      mainScene.game.getTotalMoneyWhenSellingAllMaterials(2) must_== 74113
      mainScene.game.getTotalMoneyWhenSellingAllMaterials(3) must_== 19087
      mainScene.game.getTotalMoneyWhenSellingAllMaterials(4) must_== 59035
      mainScene.game.getTotalMoneyWhenSellingAllMaterials(5) must_== 14118
    }
  }
}
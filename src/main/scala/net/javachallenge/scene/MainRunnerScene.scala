package net.javachallenge.scene

import jp.ac.waseda.cs.washi.gameaiarena.runner.AbstractRunner
import net.javachallenge.api.Game
import net.javachallenge.api.ComputerPlayer
import net.javachallenge.entity.TrianglePoint
import scala.util.Random
import scala.collection.immutable
import scala.collection.mutable
import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.GameEnvironment

trait MainRunnerScene extends CommandBaseScene {
  var runners: IndexedSeq[AbstractRunner[Game, Array[String], ComputerPlayer]] = null
  val queue: mutable.Queue[List[String]] = mutable.Queue()

  override def execute() = {
    var newScene: Scene[GameEnvironment] = this
    val cmdStrs = runners(game.currentPlayerId).run(game) ++ Array("finish")
    for (cmdStr <- cmdStrs) {
      val command = cmdStr.split(" ").filter(_.length > 0).toList
      displayLine("> " + command.mkString(" "))
      val (nextGame, nextScene) = if (command.length > 0) execute(command) else (game, this)
      game = nextGame
      newScene = nextScene
    }
    (game, newScene)
  }

  override def nextCommand = {
    throw new Exception()
  }
}
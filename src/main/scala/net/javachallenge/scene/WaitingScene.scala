package net.javachallenge.scene

import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.GameEnvironment
import net.javachallenge.entity.Game

abstract class WaitingScene(val nextScene: Scene[GameEnvironment]) extends CommandBaseScene {
  override def execute() = {
    if (getInputer().isPush(0))
      (game, nextScene)
    else
      (game, this)
  }

  override def nextCommand: Option[List[String]] = throw new Exception()

  override def execute(words: List[String]): (Game, Scene[GameEnvironment]) = throw new Exception()
}
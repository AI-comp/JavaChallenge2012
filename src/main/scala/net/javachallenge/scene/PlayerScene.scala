package net.javachallenge.scene

import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.entity.Field
import net.javachallenge.entity.Game
import net.javachallenge.GameEnvironment
import net.javachallenge.entity.GameSetting
import java.util.Random

abstract class PlayerScene(nextScene: Scene[GameEnvironment], setting: GameSetting = GameSetting())
    extends CommandBaseScene {
  override def initialize() {
    describe("Enter player names")
    displayLine("Please enter player names with space delimiters.")
  }

  override def execute(names: List[String]) = {
    if (names.size <= 1) {
      displayLine("Please enter two or more names.")
      (game, this)
    } else {
      displayLine(names.size + " players have joined the game. (" + names.mkString(", ") + ")")
      (Game(names, setting, Field(setting, new Random())), nextScene)
    }
  }
}
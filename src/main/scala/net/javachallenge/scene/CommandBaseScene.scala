package net.javachallenge.scene

import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.GameEnvironment
import net.javachallenge.entity.Game
import net.javachallenge.Main
import net.javachallenge.util.settings.Defaults
import com.google.common.io.Files
import java.io.File
import java.nio.charset.Charset
import net.javachallenge.util.misc.DateUtils

abstract class CommandBaseScene extends AbstractScene {
  override def execute() = {
    nextCommand match {
      case Some(command) =>
        displayLine("> " + command.mkString(" "))
        if (command.length > 0) execute(command) else (game, this)
      case None =>
        (game, this)
    }
  }

  def nextCommand: Option[List[String]]

  def execute(words: List[String]): (Game, Scene[GameEnvironment])
}

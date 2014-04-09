package net.javachallenge.runner

import net.javachallenge.api.ComputerPlayer
import net.javachallenge.api.command.Command
import jp.ac.waseda.cs.washi.gameaiarena.runner.AbstractRunner
import java.util.Collections
import net.javachallenge.api.Game

class MainRunner(com: ComputerPlayer) extends AbstractRunner[Game, Array[String], ComputerPlayer] {
  private var _game: Game = null
  private var _commands: Array[Command] = null

  def getComputerPlayer() = com

  def runPreProcessing(game: Game) {
    _game = game
    _commands = null
    com.saveTemporalCommands(null)
  }

  def runProcessing() {
    val commands = com.selectActions(_game)
    _commands = if (commands != null) commands.toArray(Array()) else null
  }

  def runPostProcessing() = {
    val nullableCommands = if (_commands != null) _commands else com.getTemporalCommands()
    val commands = if (nullableCommands != null) nullableCommands else Array()
    commands.map(cmd => cmd.toString).take(100).toArray
  }
}
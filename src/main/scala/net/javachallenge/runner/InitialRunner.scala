package net.javachallenge.runner

import net.javachallenge.api.ComputerPlayer
import net.javachallenge.api.command.Command
import jp.ac.waseda.cs.washi.gameaiarena.runner.AbstractRunner
import java.util.Collections
import net.javachallenge.api.TrianglePoint
import net.javachallenge.api.Game

class InitialRunner(com: ComputerPlayer) extends AbstractRunner[Game, TrianglePoint, ComputerPlayer] {
  private var _game: Game = null
  private var _location: TrianglePoint = null

  def getComputerPlayer() = com

  def runPreProcessing(game: Game) {
    _game = game
    _location = null
    com.saveTemporalVeinLocation(null)
  }

  def runProcessing() {
    _location = com.selectVein(_game)
  }

  def runPostProcessing() = {
    if (_location != null) _location else com.getTemporalVeinLocation()
  }
}
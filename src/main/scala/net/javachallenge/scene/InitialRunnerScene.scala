package net.javachallenge.scene

import jp.ac.waseda.cs.washi.gameaiarena.runner.AbstractRunner
import net.javachallenge.api.Game
import net.javachallenge.api.ComputerPlayer
import net.javachallenge.entity.TrianglePoint
import scala.util.Random
import net.javachallenge.entity.Player
import net.javachallenge.entity.ApiConversion._

trait InitialRunnerScene extends CommandBaseScene {
  var runners: IndexedSeq[AbstractRunner[Game, net.javachallenge.api.TrianglePoint, ComputerPlayer]] = null

  override def nextCommand = {
    val location: TrianglePoint = runners(game.currentPlayerId).run(game)
    val isValid = (p: TrianglePoint) => game.field.veins.contains(p) &&
      game.field.veins(p).ownerId == Player.neutralPlayer.id
    val validLocation = if (isValid(location))
      location
    else
      game.field.validCoords.filter(isValid).head
    Some(List(validLocation.cmdStr))
  }
}
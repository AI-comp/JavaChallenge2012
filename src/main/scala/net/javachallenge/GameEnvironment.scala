package net.javachallenge;

import scala.math._
import jp.ac.waseda.cs.washi.gameaiarena.gui.Environment
import jp.ac.waseda.cs.washi.gameaiarena.gui.GamePanel
import net.javachallenge.entity.Game
import jp.ac.waseda.cs.washi.gameaiarena.api.Point2
import net.javachallenge.entity.TrianglePoint

/**
 * ゲーム全体の情報を統括するクラスです。
 */
class GameEnvironment(panel: GamePanel, var game: Game, val tileSize: Int) extends Environment(panel) {
  private var _trianglePointToPixelPoint: Map[TrianglePoint, Point2] = Map.empty

  val offsetX = 20
  val offsetY = 8

  def toHexPoint(x: Int, y: Int) = (abs(y) * tileSize / 2 + (x + 9) * tileSize, (y + 9) * tileSize / 4 * 3)

  def trianglePointToPixelPoint = {
    if (_trianglePointToPixelPoint.size == 0 && game != null) {
      val (cpx, cpy) = toHexPoint(0, 0)
      _trianglePointToPixelPoint = game.field.validCoords.map(
        p => {
          val oddx = abs(p.x) % 2
          val oddy = abs(p.y) % 2
          val px = (p.x >> 1) * tileSize + tileSize / 2 * oddx
          val py = (p.y >> 1) * tileSize / 2 * 3 + (oddy * 2 - 1) * tileSize / 4 * (oddx + 1) + tileSize / 2
          p -> new Point2(px + cpx + offsetX, py + cpy + offsetY)
        }).toMap
    }
    _trianglePointToPixelPoint
  }
}

object GameEnvironment {
  def apply(panel: GamePanel = null, game: Game = null, tileSize: Int = 48) = {
    new GameEnvironment(panel, game, tileSize)
  }
}

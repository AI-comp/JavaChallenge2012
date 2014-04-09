package net.javachallenge.entity

import net.javachallenge.entity.ApiConversion._
import scala.collection.JavaConverters._
import jp.ac.waseda.cs.washi.gameaiarena.api.Point2

/**
 * A case class which represents a coordinate in triangle coordinate with the {@link Point2} instance.
 */
@SerialVersionUID(0l)
case class TrianglePoint(_x: Int, _y: Int) extends Point2(_x, _y) with net.javachallenge.api.TrianglePoint {
  override def getX() = x

  override def getY() = y

  override def isUpwardTriangle = isUpward

  override def isDownwardTriangle = isDownward

  override def isConnected(that: net.javachallenge.api.TrianglePoint): Boolean = isConnected(that)

  override def getDistance(to: net.javachallenge.api.TrianglePoint) = getShortestPath(to).size - 1

  override def getShortestPath(to: net.javachallenge.api.TrianglePoint) = {
    val ps: List[net.javachallenge.api.TrianglePoint] = shortestPath(to)
    new java.util.ArrayList(ps.asJava)
  }

  override def toStringForCommand = cmdStr

  /**
   * Returns a boolean whether this location is on an upward triangle.
   * @return the a boolean whether this location is on an upward triangle
   */
  def isUpward = ((x + y) & 1) == 0

  /**
   * Returns a boolean whether this location is on a downward triangle.
   * @return the a boolean whether this location is on a downward triangle
   */
  def isDownward = ((x + y) & 1) != 0

  /**
   * Returns a boolean whether this location connects with the specified location.
   * @param that the location to be checked with this location
   * @return the boolean whether this location connects with the specified location
   */
  def isConnected(that: TrianglePoint): Boolean = {
    val dx = x - that.x
    val dy = y - that.y
    if (dy == 0 && (dx == -1 || dx == 1)) {
      true
    } else if (isUpward) {
      dx == 0 && dy == -1
    } else {
      dx == 0 && dy == 1
    }
  }

  /**
   * Returns the list of the shortest path from this location to the specified location.
   * @param to the destination location
   * @return the list of the shortest path from this location to the specified location
   */
  def shortestPath(to: TrianglePoint): List[TrianglePoint] = {
    if (x < to.x || (x == to.x && y <= to.y)) {
      val dy = y - to.y
      val absdy = math.abs(dy)
      val signedDy = if (absdy != 0) dy / absdy else 0
      shortestPath(signedDy, to :: Nil)
    } else {
      to.shortestPath(this).reverse
    }
  }

  private def shortestPath(signedDy: Int, path: List[TrianglePoint]): List[TrianglePoint] = {
    val to = path.head
    if (to.y == y) {
      if (to.x == x) {
        path
      } else if (to.x < x) {
        shortestPath(signedDy, TrianglePoint(to.x + 1, to.y) :: path)
      } else {
        shortestPath(signedDy, TrianglePoint(to.x - 1, to.y) :: path)
      }
    } else if (to.isConnected(TrianglePoint(to.x, to.y + signedDy))) {
      shortestPath(signedDy, TrianglePoint(to.x, to.y + signedDy) :: path)
    } else {
      if (to.x < x) {
        shortestPath(signedDy, TrianglePoint(to.x + 1, to.y) :: path)
      } else {
        shortestPath(signedDy, TrianglePoint(to.x - 1, to.y) :: path)
      }
    }
  }

  def ==(that: TrianglePoint) = {
    equals(that)
  }

  def cmdStr = x + "," + y
}

object TrianglePoint {
  implicit def trianglePointOrdering: Ordering[TrianglePoint] = Ordering.fromLessThan((p1, p2) => {
    if (p1.y == p2.y) {
      p1.x < p2.x
    } else {
      p1.y < p2.y
    }
  })
}
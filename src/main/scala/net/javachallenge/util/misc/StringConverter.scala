package net.javachallenge.util.misc

import scala.util.control.Exception._
import net.javachallenge.entity.Material
import jp.ac.waseda.cs.washi.gameaiarena.api.Point2
import net.javachallenge.entity.TrianglePoint

object IndexStr {
  def apply(i: Int) = (i + 1).toString
  def unapply(s: String) = catching(classOf[NumberFormatException]).opt(s.toInt - 1)
}

object IntStr {
  def apply(i: Int) = i.toString
  def unapply(s: String) = catching(classOf[NumberFormatException]).opt(s.toInt)
}

object TrianglePointStr {
  def apply(p: TrianglePoint) = p.toString
  def unapply(s: String) = {
    catching(classOf[Exception]).opt(Point2.parse(s)) match {
      case Some(p) => Some(TrianglePoint(p.x, p.y))
      case _ => None
    }
  }
}
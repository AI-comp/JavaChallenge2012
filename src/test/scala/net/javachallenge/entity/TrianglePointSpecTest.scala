package net.javachallenge.entity

import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner.{ JUnitSuiteRunner, JUnit }
import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

@RunWith(classOf[JUnitSuiteRunner])
class TrianglePointSpecTest extends Specification with JUnit {

  "TrianglePoint" should {
    "judge whether two points are connected" in {
      TrianglePoint(0, 0).isConnected(TrianglePoint(0, 1)) must_== true
      TrianglePoint(0, 0).isConnected(TrianglePoint(1, 0)) must_== true
      TrianglePoint(0, 0).isConnected(TrianglePoint(-1, 0)) must_== true
      TrianglePoint(0, 0).isConnected(TrianglePoint(0, -1)) must_== false

      TrianglePoint(0, 1).isConnected(TrianglePoint(0, 0)) must_== true
      TrianglePoint(0, 1).isConnected(TrianglePoint(1, 1)) must_== true
      TrianglePoint(0, 1).isConnected(TrianglePoint(-1, 1)) must_== true
      TrianglePoint(0, 1).isConnected(TrianglePoint(0, 2)) must_== false
    }

    "caluculate shortest path between two points" in {
      TrianglePoint(0, 0).shortestPath(TrianglePoint(0, 1)) must_== TrianglePoint(0, 0) :: TrianglePoint(0, 1) :: Nil
      TrianglePoint(0, 0).shortestPath(TrianglePoint(0, -1)) must_== TrianglePoint(0, 0) :: TrianglePoint(-1, 0) :: TrianglePoint(-1, -1) :: TrianglePoint(0, -1) :: Nil
    }

    "caluculate shortest path between same point" in {
      TrianglePoint(0, 0).shortestPath(TrianglePoint(0, 0)) must_== TrianglePoint(0, 0) :: Nil
      TrianglePoint(0, 0).getDistance(TrianglePoint(0, 0)) must_== 0
    }
  }

}
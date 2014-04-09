package net.javachallenge.api;

import java.util.Comparator;

/**
 * A {@link Comparator} for {@link TrianglePoint}.
 */
public class TriangleComparator implements Comparator<TrianglePoint> {
  public int compare(TrianglePoint p1, TrianglePoint p2) {
    if (p1.getY() == p2.getY()) {
      return p1.getX() - p2.getX();
    } else {
      return p1.getY() - p2.getY();
    }
  }
}

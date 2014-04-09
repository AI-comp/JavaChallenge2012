package net.javachallenge.api;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The {@link TrianglePoint} class represents the coordinates of the game map.
 * 
 * Note that this interface is immutable, thus, its object stores the information of just this turn
 * and will not be updated.
 * 
 */
public interface TrianglePoint extends Serializable {
  /**
   * Returns the horizontal coordinate of the point.
   * 
   * @return the horizontal coordinate of the point
   */
  int getX();

  /**
   * Returns the vertical coordinate of the point.
   * 
   * @return the vertical coordinate of the point.
   */
  int getY();

  /**
   * Returns true if the triangle is upward, false otherwise
   * 
   * @return true if the triangle is upward, false otherwise
   */
  boolean isUpwardTriangle();

  /**
   * Returns true if the triangle is downward, false otherwise
   * 
   * @return true if the triangle is downward, false otherwise
   */
  boolean isDownwardTriangle();

  /**
   * Returns true if this and that are connected, else otherwise
   * 
   * @return true if this and that are connected, else otherwise
   */
  boolean isConnected(TrianglePoint that);

  /**
   * Returns the distance to the point to
   * 
   * @param to the target point
   * @return the distance to the point to
   */
  int getDistance(TrianglePoint to);

  /**
   * Returns the shortest path from this position to the specified position as a copied mutable
   * list.
   * 
   * @param to the target point
   * @return the shortest path from this position to the specified position as a copied mutable
   *         list.
   */
  ArrayList<TrianglePoint> getShortestPath(TrianglePoint to);

  /**
   * Returns a string representation of the point to display in commands.
   */
  String toStringForCommand();
}

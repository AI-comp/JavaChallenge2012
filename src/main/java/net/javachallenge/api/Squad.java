package net.javachallenge.api;

import java.util.ArrayList;

/**
 * The {@link Squad} interface represents a squad of robots in the game.
 * 
 * Note that this interface is immutable, thus, its object stores the information of just this turn
 * and will not be updated.
 */
public interface Squad {
  /**
   * Returns the id of the owner of the squad.
   * 
   * @return the id of the owner of the squad
   */
  int getOwnerId();

  /**
   * Returns the number of robots in the squad.
   * 
   * @return the number of robots in the squad
   */
  int getRobot();

  /**
   * Returns the path of the squad from the current location to the destination as a copied mutable
   * list.
   * 
   * @return the path of the squad from the current location to the destination as a copied mutable
   *         list
   */
  ArrayList<TrianglePoint> getPath();

  /**
   * Returns the current location of the squad.
   * 
   * @return the current location of the squad
   */
  TrianglePoint getCurrentLocation();

  /**
   * Returns the destination of the squad.
   * 
   * @return the destination of the squad
   */
  TrianglePoint getDestinationLocation();
}

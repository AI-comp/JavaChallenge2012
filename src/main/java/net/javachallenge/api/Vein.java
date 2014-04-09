package net.javachallenge.api;

import java.util.ArrayList;

/**
 * The {@link Vein} class represents a vein of the field in the game.
 * 
 * Note that this interface is immutable, thus, its object stores the information of just this turn
 * and will not be updated.
 * 
 */
public interface Vein {
  /**
   * Returns the id of the owner of the vein.
   * 
   * Note that a neutral vein's owner is equal to Game.getNeutralPlayerId().
   * 
   * @return the id of the owner of the vein
   */
  int getOwnerId();

  /**
   * Returns the location of the vein.
   * 
   * @return the location of the vein
   */
  TrianglePoint getLocation();

  /**
   * Returns the material produced by the vein.
   * 
   * @return the material produced by the vein
   */
  Material getMaterial();

  /**
   * Returns the number of robots in the vein.
   * 
   * @return Returns the number of robots in the vein
   */
  int getNumberOfRobots();

  /**
   * Returns the current material productivity.
   * 
   * @return the current material productivity
   */
  int getCurrentMaterialProductivity();

  /**
   * Returns the current robot productivity (reproduction rate).
   * 
   * @return the current robot productivity (reproduction rate)
   */
  int getCurrentRobotProductivity();

  /**
   * Returns the initial material productivity.
   * 
   * @return the initial material productivity
   */
  int getInitialMaterialProductivity();

  /**
   * Returns the initial robot productivity (reproduction rate).
   * 
   * @return the initial robot productivity (reproduction rate)
   */
  int getInitialRobotProductivity();

  /**
   * Returns the material rank.
   * 
   * @return the material rank
   */
  int getMaterialRank();

  /**
   * Returns the robot rank.
   * 
   * @return the robot rank.
   */
  int getRobotRank();

  /**
   * Returns the distance to the vein to.
   * 
   * @param to the target vein for the distance
   * @return the distance between this and to
   */
  int getDistance(Vein to);

  /**
   * Returns the shortest path from this vein to the specified vein as a copied mutable list.
   * 
   * @param to the target vein
   * @return the shortest path from this vein to the specified vein as a copied mutable list.
   */
  ArrayList<TrianglePoint> getShortestPath(Vein to);
}

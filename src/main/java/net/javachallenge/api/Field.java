package net.javachallenge.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class which represents the field which contains the veins and the squads.
 * 
 * Note that this interface is immutable, thus, its object stores the information of just this turn
 * and will not be updated.
 */
public interface Field {
  /**
   * Returns all veins as a copied mutable list.
   * 
   * @return all veins as a copied mutable list
   */
  ArrayList<Vein> getVeins();

  /**
   * Returns all veins which the specified player owns as a copied mutable list.
   * 
   * @param playerId the player to filter veins
   * @return all veins which the specified player owns as a copied mutable list
   */
  ArrayList<Vein> getVeins(int playerId);

  /**
   * Returns the veins of the same owner for the specified vein ordered by the distance in ascending
   * order as a copied mutable list.
   * 
   * @param origination the origination to calculate distances
   * @return the veins of the same owner ordered by the distance in ascending order as a copied
   *         mutable list
   */
  ArrayList<Vein> getVeinsOfSameOwnerOrderedByDistance(Vein origination);

  /**
   * Returns the veins of the other owners for the specified vein ordered by the distance in
   * ascending order as a copied mutable list.
   * 
   * @param origination the origination to calculate distances
   * @return the veins of the other owners ordered by the distance in ascending order as a copied
   *         mutable list
   */
  ArrayList<Vein> getVeinsOfOtherOwnersOrderedByDistance(Vein origination);

  /**
   * Returns the {@link Map} instance of the locations (key) and the veins (value) as a copied
   * mutable map.
   * 
   * @return the {@link Map} instance with the locations (key) and the veins (value) as a copied
   *         mutable map
   */
  TreeMap<TrianglePoint, Vein> getVeinMap();

  /**
   * Returns the filtered {@link Map} instance of the locations (key) and the veins (value) by the
   * specified id of the owner player as a copied mutable map.
   * 
   * @return the filtered {@link Map} instance of the locations (key) and the veins (value) as a
   *         copied mutable map
   */
  TreeMap<TrianglePoint, Vein> getVeinMap(int playerId);

  /**
   * Returns the vein with the specified location.
   * 
   * @param location the location to locate the vein
   * @return the vein with the specified location
   */
  Vein getVein(TrianglePoint location);

  /**
   * Returns the all squads as a copied mutable list.
   * 
   * @return the all squads as a copied mutable list
   */
  ArrayList<Squad> getSquads();

  /**
   * Returns the filtered squads by the specified id of the owner player as a copied mutable list.
   * 
   * @return the filtered squads by the specified id of the owner player as a copied mutable list
   */
  ArrayList<Squad> getSquads(int playerId);

  /**
   * Returns the valid {@link TrianglePoint} instances as a copied mutable set.
   * 
   * @return the valid {@link TrianglePoint} instances as a copied mutable set
   */
  HashSet<TrianglePoint> getValidCoords();

  /**
   * Returns the number of veins which the specified player owns.
   * 
   * @param plyaerId the player id to filter veins
   * @return the number of veins which the specified player owns
   */
  int countVeins(int plyaerId);

  /**
   * Sums and returns the number of the robots of the specified player on the vertexes and the
   * sides.
   * 
   * @param plyaerId the player to sum the number of the veins
   * @return the number of robots including robots on the vertexes and the sides
   */
  int sumRobots(int plyaerId);

  /**
   * Sums and returns the productivity of the specified player and the specified material.
   * 
   * @param plyaerId the player to sum the material productivity
   * @param material the material to sum the material productivity
   * @return the productivity of the specified player and the specified material
   */
  int sumCurrentMaterialProductivity(int plyaerId, Material material);

  /**
   * Sums and returns the robot productivity of the specified player.
   * 
   * @param plyaerId the player to sum the robot productivity
   * @return the robot productivity of the specified player
   */
  int sumCurrentRobotProductivity(int plyaerId);
}

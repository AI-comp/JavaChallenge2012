package net.javachallenge.api.command;

import java.util.List;

import net.javachallenge.api.Material;
import net.javachallenge.api.PlayerTrade;
import net.javachallenge.api.TrianglePoint;
import net.javachallenge.api.Vein;

/**
 * A factory class to construct commands.
 */
public class Commands {
  private Commands() {}

  /**
   * Constructs a command to buy the given material from aliens.
   * 
   * @param material the material to buy
   * @param amount the amount of material to buy
   * @return the command to buy the material
   */
  public static Command buyFromAlienTrade(Material material, int amount) {
    return new BuyFromAlienTradeCommand(material, amount);
  }

  /**
   * Constructs a command to sell the given material to aliens.
   * 
   * @param material the material to sell to the aliens
   * @param amount the amount of material to sell
   * @return the command to sell the material
   */
  public static Command sellToAlienTrade(Material material, int amount) {
    return new SellToAlienTradeCommand(material, amount);
  }

  /**
   * Constructs a command to buy the given material from another player.
   * 
   * @param trade the offer to buy from
   * @param amount the amount of material to buy
   * @return the command to buy from the given offer
   */
  public static Command buyFromPlayerTrade(PlayerTrade trade, int amount) {
    return new BuyFromOfferCommand(trade, amount);
  }

  /**
   * Constructs a command to make an offer to other players.
   * 
   * @param trade the demand to respond to
   * @param amount the amount of material to sell. Should be less or equal to the amount of the
   *        demand.
   * @return the command to sell in response to the given demand
   */
  public static Command sellToPlayerTrade(PlayerTrade trade, int amount) {
    return new SellToDemandCommand(trade, amount);
  }

  /**
   * Constructs a command to create an offer to sell material to other players.
   * 
   * @param material the material to sell
   * @param amount the amount of material to sell
   * @param pricePerSingleItem the price of a unit of the material to sell
   * @return the command to make the offer
   */
  public static Command offer(Material material, int amount, int pricePerSingleItem) {
    return new OfferCommand(material, amount, pricePerSingleItem);
  }

  /**
   * Constructs a command to create a demand to buy material from other players.
   * 
   * @param material the material to buy
   * @param amount the amount of material to buy
   * @param pricePerSingleItem the price of a unit of the material to buy
   * @return the command to make the demand
   */
  public static Command demand(Material material, int amount, int pricePerSingleItem) {
    return new DemandCommand(material, amount, pricePerSingleItem);
  }

  /**
   * Constructs a command to send robot to occupy a vein.
   * 
   * @param robotsNumber the number of robots to send to the vein. There should be enough robots at
   *        the vein from where to send the robots.
   * @param from the coordinates of the vein to send the robots from. The location shall point to an
   *        existing vein owned by the caller.
   * @param to the coordinates of the vein to send the robots to. The location shall point to a vein
   *        owned by the caller.
   * @return the command to send the robots
   */
  public static Command launch(int robotsNumber, TrianglePoint from, TrianglePoint to) {
    return new LaunchCommand(robotsNumber, from, to);
  }

  /**
   * Constructs a command with the number of robots to send, and the coordinates of the vein from
   * and to where the robots should go to.
   * 
   * @param robotsNumber the number of robots to send to the vein. There should be enough robots at
   *        the vein from where to send the robots. vein owned by the caller.
   * @param path a list of points containing the path to take. The path should at least contain the
   *        departure point and the destination.
   * @return the command to send the robots
   */
  public static Command launchWithPath(int robotsNumber, List<TrianglePoint> path) {
    return new LaunchWithPathCommand(robotsNumber, path);
  }

  /**
   * Constructs a command to upgrade the productivity of the vein at the given location.
   * 
   * @param location the location of the vein to upgrade. The location shall point to a vein owned
   *        by the caller.
   * @return the command to upgrade material rank at the given location
   */
  public static Command upgradeMaterial(TrianglePoint location) {
    return new UpgradeMaterialCommand(location);
  }

  /**
   * Constructs a command to upgrade the reproduction rate of the robots at the given vein.
   * 
   * @param location the location of the vein to upgrade. The location shall point to a vein owned
   *        by the caller.
   * @return the command to upgrade material rank at the given location
   */
  public static Command upgradeRobot(TrianglePoint location) {
    return new UpgradeRobotCommand(location);
  }

  /**
   * Constructs a command to upgrade the productivity of the vein at the given location.
   * 
   * @param vein the vein to upgrade. The vein shall point to a vein owned by the caller.
   * @return the command to upgrade the vein at the given location
   */
  public static Command upgradeMaterial(Vein vein) {
    return new UpgradeMaterialCommand(vein.getLocation());
  }

  /**
   * Constructs a command to upgrade the reproduction rate of the robots at the given vein.
   * 
   * @param vein the vein to upgrade. The vein shall point to a vein owned by the caller.
   * @return the command to upgrade robot rank of the vein at the given location
   */
  public static Command upgradeRobot(Vein vein) {
    return new UpgradeRobotCommand(vein.getLocation());
  }
}

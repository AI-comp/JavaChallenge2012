package net.javachallenge.api.command;

import net.javachallenge.api.TrianglePoint;

import com.google.common.base.Preconditions;

/**
 * The {@link UpgradeRobotCommand} class represents a command to upgrade the reproduction rate of
 * the robots at the given vein.
 */
class UpgradeRobotCommand implements Command {

  private TrianglePoint location;

  /**
   * Constructs an {@link UpgradeRobotCommand} to upgrade the robots reproduction rate of the vein
   * at the given location.
   * 
   * @param location the location of the vein in which robots should be upgraded. The location shall
   *        point to a vein owned by the caller.
   */
  UpgradeRobotCommand(TrianglePoint location) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(location);
    this.location = location;
  }

  @Override
  public String toString() {
    return String.format("upgrade robot %s", location.toStringForCommand());
  }
}

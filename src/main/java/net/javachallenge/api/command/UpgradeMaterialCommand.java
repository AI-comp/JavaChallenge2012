package net.javachallenge.api.command;

import net.javachallenge.api.TrianglePoint;

import com.google.common.base.Preconditions;

/**
 * The {@link UpgradeMaterialCommand} class represents a command to upgrade the productivity of the
 * vein at the given location.
 */
class UpgradeMaterialCommand implements Command {

  private TrianglePoint location;

  /**
   * Constructs an {@link UpgradeMaterialCommand} for the vein at the given location.
   * 
   * @param location the location of the vein to upgrade. The location shall point to a vein owned
   *        by the caller.
   */
  UpgradeMaterialCommand(TrianglePoint location) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(location);
    this.location = location;
  }

  @Override
  public String toString() {
    return String.format("upgrade material %s", location.toStringForCommand());
  }
}

package net.javachallenge.api.command;

import net.javachallenge.api.TrianglePoint;

import com.google.common.base.Preconditions;

/**
 * The {@link LaunchCommand} class represents a command to send robot to occupy a vein.
 */
class LaunchCommand implements Command {

  private int robotsNumber;
  private TrianglePoint from;
  private TrianglePoint to;

  /**
   * Constructs a {@link LaunchCommand} with the number of robots to send, and the coordinates of
   * the vein from and to where the robots should go to.
   * 
   * @param robotsNumber the number of robots to send to the vein. There should be enough robots at
   *        the vein from where to send the robots.
   * @param from the coordinates of the vein to send the robots from. The location shall point to a
   *        vein owned by the caller.
   * @param to the coordinates of the vein to send the robots to. The location shall point to a vein
   *        owned by the caller.
   */
  LaunchCommand(int robotsNumber, TrianglePoint from, TrianglePoint to) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(from);
    Preconditions.checkNotNull(to);
    this.robotsNumber = robotsNumber;
    this.from = from;
    this.to = to;
  }

  @Override
  public String toString() {
    return String.format("launch %d %s %s", robotsNumber, from.toStringForCommand(),
        to.toStringForCommand());
  }
}

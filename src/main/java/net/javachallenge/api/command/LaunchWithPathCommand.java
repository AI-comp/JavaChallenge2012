package net.javachallenge.api.command;

import java.util.List;

import net.javachallenge.api.TrianglePoint;

import com.google.common.base.Preconditions;

/**
 * The {@link LaunchWithPathCommand} class represents a command to send robot to occupy a vein.
 */
class LaunchWithPathCommand implements Command {

  private int robotsNumber;
  private List<TrianglePoint> path;

  /**
   * Constructs a {@link LaunchWithPathCommand} with the number of robots to send, and the
   * coordinates of the vein from and to where the robots should go to.
   * 
   * @param robotsNumber the number of robots to send to the vein. There should be enough robots at
   *        the vein from where to send the robots. vein owned by the caller.
   * @param path a list of points containing the path to take. The path should at least contain the
   *        departure point and the destination.
   */

  LaunchWithPathCommand(int robotsNumber, List<TrianglePoint> path) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(path);
    for (TrianglePoint p : path) {
      Preconditions.checkNotNull(p);
    }
    this.robotsNumber = robotsNumber;
    this.path = path;
  }

  @Override
  public String toString() {
    String s = "launch " + String.valueOf(this.robotsNumber);
    for (TrianglePoint p : this.path) {
      s += " " + p.toStringForCommand();
    }
    return s;
  }
}

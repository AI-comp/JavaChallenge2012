package net.javachallenge.api;

import java.util.List;

import net.javachallenge.api.command.Command;

/**
 * An abstract class which represents an AI player. Please extend this class for developing your AI
 * player.
 */
public abstract class ComputerPlayer {
  private List<Command> commands;
  private TrianglePoint location;

  /**
   * Saves the temporal commands for controlling your player. Note that the temporal commands are
   * accepted when your AI program does not decide the commands within the limiting time (1000 ms).
   * 
   * @param commands the temporal commands to save
   */
  public final void saveTemporalCommands(List<Command> commands) {
    this.commands = commands;
  }

  /**
   * Returns the saved temporal commands.
   * 
   * @return the saved temporal commands
   */
  public final Command[] getTemporalCommands() {
    Command[] empty = new Command[0];
    return commands != null ? commands.toArray(empty) : empty;
  }

  /**
   * Saves the temporal vein location to occupy first for selecting your vein. Note that the
   * temporal vein location is accepted when your AI program does not decide the vein location
   * within the limiting time (10000[ms]).
   * 
   * @param location the temporal vein location to save
   */
  public final void saveTemporalVeinLocation(TrianglePoint location) {
    this.location = location;
  }

  /**
   * Returns the saved temporal vein location to occupy first.
   * 
   * @return the saved temporal vein location to occupy first
   */
  public final TrianglePoint getTemporalVeinLocation() {
    return location != null ? location : new net.javachallenge.entity.TrianglePoint(
        Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Returns the selected vein location to occupy first. This method should terminate within
   * 10000[ms]. The saved temporal vein location is used when this method exceed the limiting time.
   * Note that the vein location are selected irresponsibly when an invalid vein location is
   * selected.
   * 
   * @param game the {@link Game} instance of the current turn
   * @return the selected vein location to occupy first
   */
  public abstract TrianglePoint selectVein(Game game);

  /**
   * Returns the decided command list to control your player. This method should terminate within
   * 10000[ms]. The saved temporal command list is used when this method exceed the limiting time.
   * Note that the command to do nothing is accepted when an invalid or empty command is decided.
   * Invalid commands are ignored, i.e. skipped to execute next commands in the command list.
   * 
   * @param game the {@link Game} instance of the current turn
   * @return the decided command list to control your player
   */
  public abstract List<Command> selectActions(Game game);

  /**
   * Return the team name.
   * 
   * @return the team name
   */
  public abstract String getName();
}

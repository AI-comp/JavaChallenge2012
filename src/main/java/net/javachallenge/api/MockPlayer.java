package net.javachallenge.api;

import java.util.List;

import net.javachallenge.api.command.Command;

/**
 * The {@link MockPlayer} class represents a dummy player controlled by the computer.
 */
public class MockPlayer extends ComputerPlayer {

  private String name;

  /**
   * Constructs a MockPlayer with a dummy name.
   */
  public MockPlayer() {
    this("MockPlayer");
  }

  /**
   * Constructs a MockPlayer with the given name.
   * 
   * @param name the name to give to the MockPlayer
   */
  public MockPlayer(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public TrianglePoint selectVein(Game game) {
    return null;
  }

  @Override
  public List<Command> selectActions(Game game) {
    return null;
  }
}

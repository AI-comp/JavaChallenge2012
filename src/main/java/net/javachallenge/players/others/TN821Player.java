package net.javachallenge.players.others;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.javachallenge.api.ComputerPlayer;
import net.javachallenge.api.Game;
import net.javachallenge.api.Make;
import net.javachallenge.api.TrianglePoint;
import net.javachallenge.api.Vein;
import net.javachallenge.api.command.Command;
import net.javachallenge.api.command.Commands;

public class TN821Player extends ComputerPlayer {
  @Override
  public String getName() {
    return "TN821";
  }

  @Override
  public TrianglePoint selectVein(Game game) {
    return Make.point(0, 0);
  }

  @Override
  public List<Command> selectActions(Game game) {
    return do1(game);
  }

  private List<Command> do1(Game game) {

    List<Command> commands = new ArrayList<Command>();

    Random rnd = new Random(100);
    TriTriDistance TTD = new TriTriDistance(game);
    int myId = game.getMyPlayer().getId();
    Map<TrianglePoint, Vein> veinMap = game.getField().getVeinMap();
    for (TrianglePoint myTp : game.getField().getVeinMap(myId).keySet()) {
      TrianglePoint targetTp = null;
      int minDist = Integer.MAX_VALUE;
      Vein myVein = veinMap.get(myTp);
      for (TrianglePoint tp : game.getField().getVeinMap().keySet()) {
        if (veinMap.get(tp).getOwnerId() == myId) continue;
        if (veinMap.get(tp).getOwnerId() != -1 && rnd.nextInt() < 10) continue;
        if (minDist > TTD.getDistance(myTp, tp)) {
          targetTp = tp;
          minDist = TTD.getDistance(myTp, tp);
        }
      }
      if (targetTp != null) {
        int RobotNum = myVein.getNumberOfRobots();
        int x = 20;
        while (RobotNum > x) {
          commands.add(Commands.launch(x, myTp, targetTp));
          RobotNum -= x;
        }
      } else {
        commands.add(Commands.upgradeMaterial(myTp));
        commands.add(Commands.upgradeRobot(myTp));
      }
    }

    return commands;
  }



  public static int getDistance(TrianglePoint t1, TrianglePoint t2) {
    return Math.abs(t1.getX() - t2.getX()) + Math.abs(t1.getY() - t2.getY());
  }


  class TriTriDistance {
    private Game game;
    private Map<TrianglePoint, Map<TrianglePoint, Integer>> triTriDistanceMap;

    public TriTriDistance(Game game) {
      this.game = game;
      this.triTriDistanceMap = getTriTriMap();
    }

    /**
     * Map<trianglePoint1, trianglePoint2, int>> trianglePoint1=departure trianglePoint2=destination
     * int=distance
     */
    private Map<TrianglePoint, Map<TrianglePoint, Integer>> getTriTriMap() {
      // Store the locations of all veins.
      // Vein locations are fixed after starting the game.
      Map<TrianglePoint, Vein> veinMap = game.getField().getVeinMap();
      Set<TrianglePoint> triPoints = veinMap.keySet();

      Map<TrianglePoint, Map<TrianglePoint, Integer>> map =
          new HashMap<TrianglePoint, Map<TrianglePoint, Integer>>();
      for (TrianglePoint t1 : triPoints) {
        HashMap<TrianglePoint, Integer> subMap = new HashMap<TrianglePoint, Integer>();
        for (TrianglePoint t2 : triPoints) {
          int distance = TN821Player.getDistance(t1, t2);
          subMap.put(t2, distance);
        }
        map.put(t1, subMap);
      }

      return map;

    }

    public int getDistance(TrianglePoint source, TrianglePoint target) {
      return triTriDistanceMap.get(source).get(target);
    }

  }

}

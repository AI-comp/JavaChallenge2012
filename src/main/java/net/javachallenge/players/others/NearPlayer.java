package net.javachallenge.players.others;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.javachallenge.api.ComputerPlayer;
import net.javachallenge.api.Game;
import net.javachallenge.api.Material;
import net.javachallenge.api.Squad;
import net.javachallenge.api.TrianglePoint;
import net.javachallenge.api.Vein;
import net.javachallenge.api.command.Command;
import net.javachallenge.api.command.Commands;

public class NearPlayer extends ComputerPlayer {
  static final double LAUNCH_RATE = 2.5;

  static final int REQUIRED_MINIMUM_ROBOTS_TO_INVADE = 100;

  static final int REQUIRED_MAXIMUM_SQUADS_TO_INVADE = 4;

  static final int NEAR_THRESHOLD = 12;

  class VeinWithPoint {
    Vein vein;
    TrianglePoint point;
    int distance;

    VeinWithPoint(Vein v, TrianglePoint p, TrianglePoint t) {
      vein = v;
      point = p;
      distance = p.getShortestPath(t).size();
    }
  }

  static Random random = new Random(1000000009);

  @Override
  public String getName() {
    return "near-player";
  }

  @Override
  public TrianglePoint selectVein(Game game) {
    TrianglePoint bestPoint = null;
    int bestRobot = -1;

    Map<TrianglePoint, Vein> map = filterVein(game, -1);
    Material firstMaterial = null;
    if (filterMyVein(game).size() == 0) {
      firstMaterial = Material.Stone;
    } else {
      Vein selectedVein = filterMyVein(game).values().iterator().next();
      firstMaterial = selectedVein.getMaterial();
    }

    for (Entry<TrianglePoint, Vein> entry : map.entrySet()) {
      TrianglePoint pt = entry.getKey();
      Vein vein = entry.getValue();
      boolean wantMaterial =
          (vein.getMaterial() == Material.Gas || vein.getMaterial() == Material.Metal);
      if (firstMaterial == vein.getMaterial()) {
        wantMaterial = false;
      }
      if (wantMaterial) {
        int score = evaluateInitialVeinScore(game, vein, pt);
        if (bestRobot < score) {
          bestRobot = score;
          bestPoint = pt;
        }
      }
    }
    if (bestPoint != null) {
      return bestPoint;
    }
    return map.entrySet().iterator().next().getKey();
  }

  /**
   * Evaluate and return initial value of specified vein.
   * 
   * @param game
   * @param vein
   * @param pt
   * @return initial evaluation value of specified vein
   */
  private int evaluateInitialVeinScore(Game game, Vein vein, TrianglePoint pt) {
    Map<TrianglePoint, Vein> map = filterVein(game, -1);
    Map<TrianglePoint, Vein> enemyMap = filterEnemyVein(game);
    int score = evaluateBaseProductivity(vein);
    for (Entry<TrianglePoint, Vein> entryTo : map.entrySet()) {
      TrianglePoint topt = entryTo.getKey();
      if (pt.equals(topt)) {
        continue;
      }
      int d = topt.getShortestPath(pt).size();
      score += (Math.max(0, NEAR_THRESHOLD - d));
    }
    for (Entry<TrianglePoint, Vein> entryTo : enemyMap.entrySet()) {
      TrianglePoint topt = entryTo.getKey();
      if (pt.equals(topt)) {
        continue;
      }
      int d = topt.getShortestPath(pt).size();
      score -= (Math.max(0, NEAR_THRESHOLD - d)) * 100;
    }
    return score;
  }


  @Override
  public List<Command> selectActions(Game game) {
    List<Command> commands = new ArrayList<Command>();

    commands.addAll(generateLaunchCommands(game));

    commands.addAll(generateUpgradeCommands(game));

    commands.addAll(generateTradeCommands(game));

    return commands;
  }

  /**
   * Generate and return trade commands.
   * 
   * @param game
   * @return trade commands
   */
  private List<Command> generateTradeCommands(Game game) {
    List<Command> ret = new ArrayList<Command>();
    return ret;
  }

  /**
   * Return evaluation value of the vein based on initial productivity.
   * 
   * @param vein
   * @return evaluation value of the vein based on initial productivity
   */
  private int evaluateBaseProductivity(Vein vein) {
    return vein.getInitialRobotProductivity() + vein.getInitialMaterialProductivity();
  }

  /**
   * Generate and return upgrade commands.
   * 
   * @param game
   * @return upgrade commands
   */
  private List<Command> generateUpgradeCommands(Game game) {
    List<Command> ret = new ArrayList<Command>();

    Map<TrianglePoint, Vein> myVeins = filterVein(game, game.getMyPlayer().getId());
    for (Entry<TrianglePoint, Vein> myEntry : myVeins.entrySet()) {
      TrianglePoint myPoint = myEntry.getKey();
      ret.add(Commands.upgradeRobot(myPoint));
    }
    for (Entry<TrianglePoint, Vein> myEntry : myVeins.entrySet()) {
      TrianglePoint myPoint = myEntry.getKey();
      ret.add(Commands.upgradeMaterial(myPoint));
    }
    return ret;
  }

  /**
   * Generate and return launch commands.
   * 
   * @param game
   * @return upgrade commands
   */
  private List<Command> generateLaunchCommands(Game game) {
    List<Command> ret = new ArrayList<Command>();
    Map<TrianglePoint, Vein> myVeins = filterVein(game, game.getMyPlayer().getId());
    Set<TrianglePoint> sentSet = new HashSet<TrianglePoint>();
    Set<TrianglePoint> sentToSet = new HashSet<TrianglePoint>();

    ret.addAll(launchSingleToSingle(game, myVeins, sentSet, sentToSet));

    ret.addAll(launchMultipleToSingle(game, myVeins, sentSet, sentToSet));

    return ret;
  }

  /**
   * Return list of unused veins that owned by me
   * 
   * @param myVeins
   * @param sentSet
   * @return list of unused veins that owned by me
   */
  private Map<TrianglePoint, Vein> unusedMyVeins(Map<TrianglePoint, Vein> myVeins,
      Set<TrianglePoint> sentSet) {
    Map<TrianglePoint, Vein> unusedMyVeins = new HashMap<TrianglePoint, Vein>();
    for (Entry<TrianglePoint, Vein> myEntry : myVeins.entrySet()) {
      TrianglePoint myPoint = myEntry.getKey();
      if (sentSet.contains(myPoint)) {
        continue;
      }
      if (myEntry.getValue().getNumberOfRobots() <= REQUIRED_MINIMUM_ROBOTS_TO_INVADE) {
        continue;
      }
      unusedMyVeins.put(myPoint, myEntry.getValue());
    }
    return unusedMyVeins;
  }

  /**
   * Return commands that launch robots multiple vein to enemy single vein.
   * 
   * @param game
   * @param myVeins
   * @param sentSet
   * @param sentToSet
   * @return commands that launch robots multiple vein to enemy single vein.
   */
  private List<Command> launchMultipleToSingle(Game game, Map<TrianglePoint, Vein> myVeins,
      Set<TrianglePoint> sentSet, Set<TrianglePoint> sentToSet) {
    List<Command> multipleLaunchCommands = new ArrayList<Command>();
    for (Entry<TrianglePoint, Vein> enemyEntry : filterEnemyVein(game).entrySet()) {
      Vein enemyVein = enemyEntry.getValue();
      TrianglePoint enemyPoint = enemyEntry.getKey();

      Map<TrianglePoint, Vein> unusedMyVeins = unusedMyVeins(myVeins, sentSet);

      List<VeinWithPoint> nearPoints = nearVeins(game, enemyPoint, unusedMyVeins);
      int cnt = 0;
      int totalMyRobot = 0;
      for (VeinWithPoint vwp : nearPoints) {
        totalMyRobot += vwp.vein.getNumberOfRobots() / 2;
        cnt++;
        int expectedEnemyRobot =
            enemyVein.getNumberOfRobots() + enemyVein.getCurrentRobotProductivity() * vwp.distance;
        if (totalMyRobot >= expectedEnemyRobot * LAUNCH_RATE / 2) {
          break;
        }
      }

      if (cnt <= Math.min(nearPoints.size() - 1, REQUIRED_MAXIMUM_SQUADS_TO_INVADE)
          && nearPoints.get(0).distance <= NEAR_THRESHOLD) {
        for (int d = 0; d < cnt; d++) {
          VeinWithPoint vwp = nearPoints.get(d);
          multipleLaunchCommands.add(Commands.launch(vwp.vein.getNumberOfRobots() / 2, vwp.point,
              enemyPoint));
          sentSet.add(vwp.point);
        }
      }
    }
    return multipleLaunchCommands;
  }

  /**
   * Return enemy squads going to specified points sorted by distance.
   * 
   * @param game
   * @param vwp
   * @return enemy squads going to specified points sorted by distance
   */
  private List<Squad> enemySquadsSortedByDistance(Game game, VeinWithPoint vwp) {
    List<Squad> squads = new ArrayList<Squad>();
    for (Squad squad : game.getField().getSquads()) {
      if (squad.getOwnerId() == game.getMyPlayer().getId()) {
        continue;
      }
      if (squad.getDestinationLocation().equals(vwp.point)) {
        squads.add(squad);
      }
    }
    Collections.sort(squads, new Comparator<Squad>() {
      @Override
      public int compare(Squad o1, Squad o2) {
        int z1 = o1.getCurrentLocation().getShortestPath(o1.getDestinationLocation()).size();
        int z2 = o2.getCurrentLocation().getShortestPath(o2.getDestinationLocation()).size();
        return z1 - z2;
      }
    });
    return squads;
  }

  /**
   * Return expected number of enemy robots.
   * 
   * @param game
   * @param squads
   * @param length
   * @return expected number of enemy robots
   */
  private int expectedEnemyRobot(Game game, List<Squad> squads, int length) {
    int squadIndex = squads.size() - 1;
    int expectedEnemyRobot = 0;
    while (squadIndex >= 0) {
      Squad squad = squads.get(squadIndex);
      if (squad.getOwnerId() != game.getMyPlayer().getId()) {
        if (squad.getPath().size() > length) {
          expectedEnemyRobot += squad.getRobot();
        } else {
          expectedEnemyRobot = Math.max(expectedEnemyRobot, squad.getRobot());
        }
      }
      squadIndex--;
    }
    return expectedEnemyRobot;
  }

  /**
   * Return commands that launch robots single vein to another single vein.
   * 
   * @param game
   * @param myVeins
   * @param sentSet
   * @param sentToSet
   * @return commands that launch robots single vein to another single vein
   */
  private List<Command> launchSingleToSingle(Game game, Map<TrianglePoint, Vein> myVeins,
      Set<TrianglePoint> sentSet, Set<TrianglePoint> sentToSet) {
    List<Command> singleLaunchCommands = new ArrayList<Command>();

    for (Entry<TrianglePoint, Vein> myEntry : myVeins.entrySet()) {
      TrianglePoint fromPoint = myEntry.getKey();
      Vein myVein = myEntry.getValue();

      Map<TrianglePoint, Vein> emptyVeins = filterVein(game, -1);
      List<VeinWithPoint> nearVeins = nearVeins(game, fromPoint, emptyVeins);
      for (VeinWithPoint vwp : nearVeins) {
        if (sentToSet.contains(vwp.point)) {
          continue;
        }
        int length = vwp.point.getShortestPath(fromPoint).size();
        List<Squad> squads = enemySquadsSortedByDistance(game, vwp);
        int expectedEnemyRobot =
            expectedEnemyRobot(game, squads, length) + vwp.vein.getNumberOfRobots();
        if (length <= NEAR_THRESHOLD
            && myVein.getNumberOfRobots() >= expectedEnemyRobot * LAUNCH_RATE) {
          singleLaunchCommands.add(Commands.launch((int) (expectedEnemyRobot * LAUNCH_RATE / 2),
              fromPoint, vwp.point));
          sentSet.add(fromPoint);
          sentToSet.add(vwp.point);
          break;
        }
      }
    }
    return singleLaunchCommands;
  }

  /**
   * Return veins owned by me.
   * 
   * @param game
   * @return my veins
   */
  private Map<TrianglePoint, Vein> filterMyVein(Game game) {
    return filterVein(game, game.getMyPlayer().getId());
  }

  /**
   * Return veins owned by enemies.
   * 
   * @param game
   * @return enemy veins
   */
  private Map<TrianglePoint, Vein> filterEnemyVein(Game game) {
    return filterNotVein(game, game.getMyPlayer().getId(), -1);
  }

  private Map<TrianglePoint, Vein> filterVein(Game game, int tid) {
    return game.getField().getVeinMap(tid);
  }

  /**
   * Returns the vein owned by tid nor tid2.
   * 
   * @param game
   * @param tid
   * @param tid2
   * @return
   */
  private Map<TrianglePoint, Vein> filterNotVein(Game game, int tid, int tid2) {
    Map<TrianglePoint, Vein> map = game.getField().getVeinMap();
    Map<TrianglePoint, Vein> returnMap = new HashMap<TrianglePoint, Vein>();
    for (Entry<TrianglePoint, Vein> entry : map.entrySet()) {
      TrianglePoint pt = entry.getKey();
      Vein vein = entry.getValue();
      if (vein.getOwnerId() != tid && vein.getOwnerId() != tid2) {
        returnMap.put(pt, vein);
      }
    }
    return returnMap;
  }

  /**
   * Return the map of vein and point that exist near the specified point.
   * 
   * @param game
   * @param p
   * @param targetVeins
   * @return the map of vein and point that exist near the specified point
   */
  private List<VeinWithPoint> nearVeins(Game game, TrianglePoint p,
      Map<TrianglePoint, Vein> targetVeins) {
    List<VeinWithPoint> nearPoints = new ArrayList<VeinWithPoint>();
    for (Entry<TrianglePoint, Vein> myEntry : targetVeins.entrySet()) {
      TrianglePoint fromPoint = myEntry.getKey();
      Vein myVein = myEntry.getValue();
      nearPoints.add(new VeinWithPoint(myVein, fromPoint, p));
    }

    Collections.sort(nearPoints, new Comparator<VeinWithPoint>() {
      public int compare(VeinWithPoint o1, VeinWithPoint o2) {
        if (o1.distance == o2.distance) {
          return o2.vein.getNumberOfRobots() - o1.vein.getNumberOfRobots();
        }
        return o1.distance - o2.distance;
      }
    });
    return nearPoints;
  }
}

package net.javachallenge.players.others;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.javachallenge.api.ComputerPlayer;
import net.javachallenge.api.Field;
import net.javachallenge.api.Game;
import net.javachallenge.api.Make;
import net.javachallenge.api.Material;
import net.javachallenge.api.Player;
import net.javachallenge.api.Squad;
import net.javachallenge.api.TrianglePoint;
import net.javachallenge.api.Vein;
import net.javachallenge.api.command.Command;
import net.javachallenge.api.command.Commands;

public class Tokoharu extends ComputerPlayer {
  @Override
  public String getName() {
    return "tokoharuAI";
  }

  @Override
  public TrianglePoint selectVein(Game game) {
    Field f = game.getField();
    Player me = game.getMyPlayer();
    Map<TrianglePoint, Vein> veinMap = f.getVeinMap();

    ArrayList<Vein> veins = new ArrayList<Vein>();

    for (int i = -20; i < 20; i++)
      for (int j = -20; j <= 20; j++) {
        TrianglePoint tp = Make.point(i, j);
        if (veinMap.containsKey(tp)) veins.add(veinMap.get(tp));
      }

    TrianglePoint besttp = Make.point(0, 0);
    double bestscore = 0;
    for (int i = 0; i < veins.size(); i++) {

      Vein myVein = veins.get(i);

      TrianglePoint startLocation = myVein.getLocation();
      if (myVein.getOwnerId() != -1) continue;
      if (myVein.getOwnerId() == me.getId()) continue;

      double div = 1;

      double score = 0;
      for (int j = 0; j < veins.size(); j++)
        if (i != j) {
          Vein targetVein = veins.get(j);
          TrianglePoint toLocation = targetVein.getLocation();

          int ownerId = targetVein.getOwnerId();
          int shortestPathLength = startLocation.getShortestPath(toLocation).size();

          double baseScore = (double) 1 / shortestPathLength;
          baseScore *= baseScore;

          if (ownerId != -1)
            score += baseScore;
          else if (ownerId == me.getId())
            div = shortestPathLength;
          else
            score += baseScore * 5;

        }

      double hoge =
          (double) myVein.getInitialMaterialProductivity() + myVein.getInitialRobotProductivity();
      score *= hoge / div;
      if (score > bestscore) {
        bestscore = score;
        besttp = startLocation;
      }

    }
    // System.err.println(bestscore);

    return besttp;
  }

  /*
   * private ArrayList<Vein> generateList(Map<TrianglePoint, Vein> veinsMy) { ArrayList<Vein> ret =
   * new ArrayList<Vein>(); for(Entry<TrianglePoint, Vein> pointAndVein : veinsMy.entrySet() ) {
   * 
   * // TrianglePoint p = pointAndVein.getKey(); Vein vein = pointAndVein.getValue(); ret.add(vein);
   * } return ret;
   * 
   * }
   */

  private void sortingDistanceToVeins(int[] nums, int iNo, List<Vein> veinsNoOwner,
      List<Vein> veinsMy) {
    TrianglePoint fromLocation = veinsNoOwner.get(iNo).getLocation();

    for (int iMy = 0; iMy < veinsMy.size(); iMy++)
      for (int jMy = 0; jMy + 1 < veinsMy.size(); jMy++) {
        TrianglePoint ALocation = veinsMy.get(nums[jMy]).getLocation();
        TrianglePoint BLocation = veinsMy.get(nums[jMy + 1]).getLocation();

        int a = fromLocation.getShortestPath(ALocation).size();
        int b = fromLocation.getShortestPath(BLocation).size();

        if (a > b) {
          int tmp = nums[jMy];
          nums[jMy] = nums[jMy + 1];
          nums[jMy + 1] = tmp;
        }
      }

    // System.out.println();

  }

  private List<Command> genNoOneGoThere(int myId, List<Vein> veinsNoOwner, List<Vein> veinsMy,
      int[] nokoriRobot, List<Squad> squads) {

    List<Command> ret = new ArrayList<Command>();

    while (true) {

      int minTime = 1000;
      int bestiNo = 1000;
      for (int iNo = 0; iNo < veinsNoOwner.size(); iNo++) {
        int require = veinsNoOwner.get(iNo).getNumberOfRobots();

        int myTotalSquad = 0;
        for (int iSq = 0; iSq < squads.size(); iSq++) {
          Squad thisSquad = squads.get(iSq);
          if (thisSquad.getOwnerId() == myId) {
            if (thisSquad.getDestinationLocation().equals(veinsNoOwner.get(iNo).getLocation())) {
              myTotalSquad += thisSquad.getRobot();
            }
          }
        }
        require = Math.max(-1, require - myTotalSquad);

        int nums[] = new int[veinsMy.size()];
        for (int iMy = 0; iMy < veinsMy.size(); iMy++)
          nums[iMy] = iMy;
        sortingDistanceToVeins(nums, iNo, veinsNoOwner, veinsMy);

        int now = 10000;
        if (require > 0) for (int iMy = 0; iMy < veinsMy.size(); iMy++)
          if (nokoriRobot[nums[iMy]] > 0) {
            require -= nokoriRobot[nums[iMy]];
            if (require < 0) {
              // now = iMy;
          now =
              veinsNoOwner.get(iNo).getLocation()
                  .getShortestPath(veinsMy.get(nums[iMy]).getLocation()).size();

          if (now > 7) now = 10000;
          break;
        }
      }

        if (now < minTime) {
          minTime = now;
          bestiNo = iNo;
        }
      }

      boolean update = false;
      if (minTime < 300) {
        update = true;

        int nums[] = new int[veinsMy.size()];
        for (int i = 0; i < veinsMy.size(); i++)
          nums[i] = i;
        sortingDistanceToVeins(nums, bestiNo, veinsNoOwner, veinsMy);

        int require = veinsNoOwner.get(bestiNo).getNumberOfRobots();
        // System.out.println("GO "+ bestiNo + " "+require);
        for (int iMy = 0; iMy < veinsMy.size(); iMy++) {
          int iter = nums[iMy];

          int used = nokoriRobot[iter];
          require -= used;
          nokoriRobot[nums[iMy]] = 0;
          if (require < 0) used += require + 1;
          nokoriRobot[nums[iMy]] -= used;
          ret.add(Commands.launch(used, veinsMy.get(nums[iMy]).getLocation(),
              veinsNoOwner.get(bestiNo).getLocation()));
          if (require < 0) break;

          // System.out.println(iter+" "+bestiNo);

        }
        // System.out.println("END");
      }
      if (!update) break;
    }

    return ret;

  }

  private List<Command> genAssistCommand(int[] veinsRobot, List<Vein> veins, int myID,
      List<Squad> squads) {
    int maxDist = 7;
    ArrayList<Command> ret = new ArrayList<Command>();
    int veinCnt = veins.size();
    int[] nokoriRobot = veinsRobot;

    for (int i = 0; i < squads.size(); i++) {
      Squad thisSquad = squads.get(i);
      if (thisSquad.getOwnerId() == myID) {
        for (int j = 0; j < veinCnt; j++)
          if (thisSquad.getDestinationLocation().equals(veins.get(j).getLocation())) {
            nokoriRobot[j] += thisSquad.getRobot();
          }
      }
    }

    for (int iFrom = 0; iFrom < veinCnt; iFrom++)
      if (nokoriRobot[iFrom] > 0) {
        for (int iTo = 0; iTo < veinCnt; iTo++)
          if (iFrom != iTo) {
            Vein veinFrom = veins.get(iFrom);
            Vein veinTo = veins.get(iTo);
            int robotFrom = nokoriRobot[iFrom];
            int robotTo = nokoriRobot[iTo];
            int sa = (robotFrom - robotTo) / 2;
            if (sa > 10) {
              int dist = veinFrom.getLocation().getShortestPath(veinTo.getLocation()).size();
              if (maxDist > dist) {
                ret.add(Commands.launch(sa, veinFrom.getLocation(), veinTo.getLocation()));
                nokoriRobot[iFrom] -= sa;
                nokoriRobot[iTo] += sa;

              }
            }
          }
      }
    return ret;
  }

  private List<Command> genIkamusume(Game game, List<Vein> veinsMy, List<Vein> veinsEnemy,
      int enemyId, int[] nokoriRobot, int myId, List<Squad> squads) {
    List<Command> ret = new ArrayList<Command>();
    int cnt = 0;

    while (true) {
      cnt++;
      if (cnt > 3) break;
      boolean update = false;
      for (int iEn = 0; iEn < veinsEnemy.size(); iEn++) {
        // System.out.println(iEn+" "+veinsEnemy.size());
        Vein thisVein = veinsEnemy.get(iEn);

        int suff = minimumSufficientRobots(thisVein, veinsMy, false);
        int real = thisVein.getNumberOfRobots();
        if (suff + 5 > real) {
          update = true;
          int require = suff + 5;
          int nums[] = new int[veinsMy.size()];
          for (int i = 0; i < veinsMy.size(); i++)
            nums[i] = i;
          sortingDistanceToVeins(nums, iEn, veinsEnemy, veinsMy);

          for (int iMy = 0; iMy < veinsMy.size(); iMy++)
            if (nokoriRobot[nums[iMy]] > 0) {
              int iter = nums[iMy];

              int used = nokoriRobot[iter];
              require -= used;
              nokoriRobot[nums[iMy]] = 0;
              if (require < 0) used += require + 1;
              nokoriRobot[nums[iMy]] -= used;
              ret.add(Commands.launch(used, veinsMy.get(nums[iMy]).getLocation(),
                  veinsEnemy.get(iEn).getLocation()));
              if (require < 0) break;
            }
        }
      }
      if (!update) break;
    }
    return ret;

  }

  private int minimumSufficientRobots(Vein myVein, List<Vein> aPlayerVein, boolean noceil) {
    int maxDist = 7;
    if (noceil) maxDist = 30;

    TrianglePoint myVeinTp = myVein.getLocation();

    int[] distRobotSequence = new int[maxDist];
    for (int i = 0; i < maxDist; i++)
      distRobotSequence[i] = 0;

    for (int i = 0; i < aPlayerVein.size(); i++) {
      Vein aVein = aPlayerVein.get(i);
      int robots = aVein.getNumberOfRobots();
      int dist = myVeinTp.getShortestPath(aVein.getLocation()).size();

      if (dist < maxDist) {
        distRobotSequence[dist] += robots;
      }
    }

    int[] distRobotSum = new int[distRobotSequence.length];
    distRobotSum[0] = 0;
    for (int i = 1; i < maxDist; i++)
      distRobotSum[i] = distRobotSum[i - 1] + distRobotSequence[i];

    int retval = 0;
    int myRobotProduct = myVein.getCurrentRobotProductivity();
    for (int i = 1; i < maxDist; i++) {
      boolean ok = true;
      int nowCntRobot = distRobotSequence[i];
      for (int j = i - 1; j >= 0; j--) {
        nowCntRobot -= myRobotProduct;
        if (nowCntRobot < distRobotSequence[j]) ok = false;
      }
      if (ok) retval = Math.max(retval, nowCntRobot);
    }
    return retval;

  }

  private int valueDamage(TrianglePoint tp, List<Squad> squads) {
    int ret = 0;
    for (int i = 0; i < squads.size(); i++) {
      Squad someSquad = squads.get(i);
      TrianglePoint dest = someSquad.getDestinationLocation();
      if (tp == dest) ret += someSquad.getRobot();
    }
    return ret;
  }

  public List<Command> updateCommand(List<Vein> veinsMy, Player myPlayer) {
    List<Command> ret = new ArrayList<Command>();
    int cnt = 0;

    int myGas = myPlayer.getMaterial(Material.Gas);
    int myStone = myPlayer.getMaterial(Material.Stone);
    int myMetal = myPlayer.getMaterial(Material.Metal);
    while (true) {
      if (cnt > 5) break;
      cnt++;
      boolean update = false;

      if (cnt > 1 || myStone <= myMetal) if (myGas >= 100 && myStone >= 200) {
        update = true;
        myGas -= 100;
        myStone -= 200;

        int bestVein = 0;
        int bestScore = -50;
        for (int i = 0; i < veinsMy.size(); i++) {
          Vein targetVein = veinsMy.get(i);
          int matLevel = targetVein.getMaterialRank();
          int matNum = targetVein.getCurrentMaterialProductivity();

          if (matLevel != 1) continue;

          int score = matNum;

          if (bestScore < score) {
            bestScore = score;
            bestVein = i;
          }

        }
        if (bestScore > 0) ret.add(Commands.upgradeMaterial(veinsMy.get(bestVein).getLocation()));
      }

      else if (cnt > 1 || myStone >= myMetal) if (myGas >= 100 && myMetal >= 200) {
        update = true;
        myGas -= 100;
        myMetal -= 200;

        int bestVein = 0;
        int bestScore = -50;
        for (int i = 0; i < veinsMy.size(); i++) {
          Vein targetVein = veinsMy.get(i);
          int robLevel = targetVein.getRobotRank();
          int robNum = targetVein.getCurrentRobotProductivity();

          if (robLevel != 1) continue;

          int score = robNum;

          if (bestScore < score) {
            bestScore = score;
            bestVein = i;
          }

        }
        if (bestScore > 0) ret.add(Commands.upgradeRobot(veinsMy.get(bestVein).getLocation()));
      }

      else if (cnt > 1 || myStone <= myMetal) if (myGas >= 200 && myStone >= 400) {

        update = true;
        myGas -= 200;
        myStone -= 400;

        int bestVein = 0;
        int bestScore = -50;
        for (int i = 0; i < veinsMy.size(); i++) {
          Vein targetVein = veinsMy.get(i);
          int matLevel = targetVein.getMaterialRank();
          int matNum = targetVein.getCurrentMaterialProductivity();

          if (matLevel != 2) continue;

          int score = matNum;

          if (bestScore < score) {
            bestScore = score;
            bestVein = i;
          }

        }
        if (bestScore > 0) ret.add(Commands.upgradeMaterial(veinsMy.get(bestVein).getLocation()));
      }

      else if (cnt > 1 || myStone >= myMetal) if (myGas >= 200 && myMetal >= 400) {
        update = true;
        int bestVein = 0;
        int bestScore = -50;
        for (int i = 0; i < veinsMy.size(); i++) {
          Vein targetVein = veinsMy.get(i);
          int robLevel = targetVein.getRobotRank();
          int robNum = targetVein.getCurrentRobotProductivity();

          if (robLevel != 2) continue;

          int score = robNum;

          if (bestScore < score) {
            bestScore = score;
            bestVein = i;
          }

        }
        if (bestScore > 0) ret.add(Commands.upgradeRobot(veinsMy.get(bestVein).getLocation()));
      }

      if (!update) break;
    }

    return ret;
  }

  @Override
  public List<Command> selectActions(Game game) {
    List<Command> commands = new ArrayList<Command>();

    List<Squad> squads = game.getField().getSquads();

    int myId = game.getMyPlayer().getId();

    List<Vein> veinsMy = game.getField().getVeins(myId);
    List<Vein> veinsNoOwner = game.getField().getVeins(-1);

    int[] nokoriRobot = new int[veinsMy.size()];
    for (int i = 0; i < veinsMy.size(); i++) {
      Vein thisVein = veinsMy.get(i);

      int maximumSufficient = 0;
      for (int id = 0; id < game.getPlayerCount(); id++)
        if (id != myId)
          maximumSufficient =
              Math.max(maximumSufficient,
                  minimumSufficientRobots(thisVein, game.getField().getVeins(id), false));

      nokoriRobot[i] = thisVein.getNumberOfRobots() - maximumSufficient;
      nokoriRobot[i] = nokoriRobot[i] - valueDamage(thisVein.getLocation(), squads);
    }

    List<Command> now = genNoOneGoThere(myId, veinsNoOwner, veinsMy, nokoriRobot, squads);
    for (int i = 0; i < now.size(); i++)
      commands.add(now.get(i));

    List<Command> assistance = genAssistCommand(nokoriRobot, veinsMy, myId, squads);
    for (int i = 0; i < assistance.size(); i++)
      commands.add(assistance.get(i));

    List<Command> update = updateCommand(veinsMy, game.getMyPlayer());
    for (int i = 0; i < update.size(); i++)
      commands.add(update.get(i));

    for (int id = 0; id < game.getPlayerCount(); id++)
      if (id != myId) {
        List<Command> ika =
            genIkamusume(game, veinsMy, game.getField().getVeins(id), id, nokoriRobot, myId, squads);
        for (int j = 0; j < ika.size(); j++)
          commands.add(ika.get(j));
      }

    this.saveTemporalCommands(commands);

    commands.add(Commands.buyFromAlienTrade(Material.Stone, 1));

    return commands;
  }
}

package net.javachallenge.players.others;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.javachallenge.api.*;
import net.javachallenge.api.command.*;

public class Wand extends ComputerPlayer {

  static final int NEUTRAL_OWNER_ID = -1;

  @Override
  public String getName() {
    return "Wand Player";
  }

  @Override
  public TrianglePoint selectVein(Game game) {
    // Store your temporal selection. When your program exceeds the time
    // limit (10 second),
    // this selection are accepted.
    this.saveTemporalVeinLocation(Make.point(0, 0));

    Field field = game.getField();
    List<Vein> allVeins = field.getVeins();
    Vein selectVein = allVeins.get(0);

    Map<Vein, Integer> veinScores = new HashMap<Vein, Integer>();

    for (Vein vein : allVeins) {
      if (vein.getOwnerId() != NEUTRAL_OWNER_ID) {
        veinScores.put(vein, Integer.MIN_VALUE);
        continue;
      }
      Integer score =
          (vein.getCurrentRobotProductivity() + vein.getCurrentMaterialProductivity()) * 25
              + vein.getNumberOfRobots();
      veinScores.put(vein, score);
    }

    Integer maxScore = Integer.MIN_VALUE;
    for (Vein vein : allVeins) {
      Integer veinScore = veinScores.get(vein);
      if (veinScore > maxScore) {
        selectVein = vein;
        maxScore = veinScore;
      }
    }

    return selectVein.getLocation();

  }

  @Override
  public List<Command> selectActions(Game game) {
    List<Command> commands = new ArrayList<Command>();

    Field field = game.getField();
    Player myPlayer = game.getMyPlayer();
    // AlienTrade alienTrade = game.getAlienTrade();

    List<Vein> veinList = field.getVeins();
    List<Vein> myVeinList = field.getVeins(game.getMyPlayer().getId());
    List<Vein> copiedVeinList = new ArrayList<Vein>();
    List<Vein> copiedMyVeinList = new ArrayList<Vein>();

    for (Vein vein : veinList) {
      copiedVeinList.add(vein);
    }

    for (Vein vein : myVeinList) {
      copiedMyVeinList.add(vein);
    }

    // Launch
    for (Vein myVein : myVeinList) {
      Collections.sort(copiedVeinList, new VeinComparator(VeinComparator.DISTANCE, myVein));
      Vein to = copiedVeinList.get(0);

      if (myVein.getNumberOfRobots() > 100) {
        commands.add(Commands.launch(myVein.getNumberOfRobots() - 50, myVein.getLocation(),
            to.getLocation()));
      }

    }

    // Upgrade
    // Robot upgrade
    Collections.sort(copiedMyVeinList, new VeinComparator(VeinComparator.ROBOT_PRODUCTIVITY));
    for (Vein myVein : copiedMyVeinList) {
      if (myVein.getRobotRank() >= 3) {
        continue;
      }
      boolean flag = true;
      for (Material material : Material.values()) {
        int materialAmount = myPlayer.getMaterial(material);
        if ((myVein.getRobotRank() == 1 && game.getSetting()
            .getMaterialsForUpgradingRobotRankFrom1To2(material) > materialAmount)
            || (myVein.getRobotRank() == 2 && game.getSetting()
                .getMaterialsForUpgradingRobotRankFrom2To3(material) > materialAmount)) {
          flag = false;
        }
      }
      if (flag) {
        commands.add(Commands.upgradeRobot(myVein));
        break;
      }
    }

    // Material Upgrade
    Collections.sort(copiedMyVeinList, new VeinComparator(VeinComparator.MATERIAL_PRODUCTIVITY));
    for (Vein myVein : copiedMyVeinList) {
      if (myVein.getMaterialRank() >= 3) {
        continue;
      }
      boolean flag = true;
      for (Material material : Material.values()) {
        int materialAmount = myPlayer.getMaterial(material);
        if ((myVein.getRobotRank() == 1 && game.getSetting()
            .getMaterialsForUpgradingMaterialRankFrom1To2(material) > materialAmount)
            || (myVein.getRobotRank() == 2 && game.getSetting()
                .getMaterialsForUpgradingMaterialRankFrom2To3(material) > materialAmount)) {
          flag = false;
        }
      }
      if (flag) {
        commands.add(Commands.upgradeMaterial(myVein));
      }
    }

    // Trade
    for (Material material : Material.values()) {
      int amount = game.getMyPlayer().getMaterial(material);
      if (amount > 1000) {
        commands.add(Commands.sellToAlienTrade(material, amount - 500));
      }
      if (amount < 200) {
        commands.add(Commands.buyFromAlienTrade(material,
            Math.min(200, myPlayer.getMoney() / game.getAlienTrade().getBuyPriceOf(material))));
      }
    }

    return commands;
  }

  class VeinComparator implements Comparator<Vein> {

    public static final int DISTANCE = 0;
    public static final int MATERIAL_PRODUCTIVITY = 1;
    public static final int ROBOT_PRODUCTIVITY = 2;
    public static final int AIM_SCORE = 3;

    Vein originVein;
    int type;

    VeinComparator(int type, Vein originVein) {
      this.type = type;
      this.originVein = originVein;
    }

    VeinComparator(int type) {
      this.type = type;
    }

    @Override
    public int compare(Vein o1, Vein o2) {
      switch (type) {
        case DISTANCE:
          return compareDistance(o1, o2);
        case MATERIAL_PRODUCTIVITY:
          return compareMaterialProductivity(o1, o2);
        case ROBOT_PRODUCTIVITY:
          return compareRobotProductivity(o1, o2);
        case AIM_SCORE:
          return compareAimScore(o1, o2);
        default:
          return 0;
      }
    }

    public int compareDistance(Vein o1, Vein o2) {
      if (o1.equals(originVein)) return 1;
      if (o2.equals(originVein)) return -1;

      if (o1.getOwnerId() == originVein.getOwnerId()) return 1;
      if (o2.getOwnerId() == originVein.getOwnerId()) return -1;
      return originVein.getDistance(o1) - originVein.getDistance(o2);
    }

    public int compareAimScore(Vein o1, Vein o2) {
      return 0;
    }

    public int compareMaterialProductivity(Vein o1, Vein o2) {
      return (o1.getCurrentMaterialProductivity() - o2.getCurrentMaterialProductivity()) * -1;
    }

    public int compareRobotProductivity(Vein o1, Vein o2) {
      return (o1.getCurrentMaterialProductivity() - o2.getCurrentMaterialProductivity()) * -1;
    }

  }
}

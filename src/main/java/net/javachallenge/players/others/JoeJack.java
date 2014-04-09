package net.javachallenge.players.others;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.javachallenge.api.ComputerPlayer;
import net.javachallenge.api.Game;
import net.javachallenge.api.GameSetting;
import net.javachallenge.api.Material;
import net.javachallenge.api.PlayerTrade;
import net.javachallenge.api.Squad;
import net.javachallenge.api.TradeType;
import net.javachallenge.api.TrianglePoint;
import net.javachallenge.api.Vein;
import net.javachallenge.api.command.Command;
import net.javachallenge.api.command.Commands;

public class JoeJack extends ComputerPlayer {
  private Map<TrianglePoint, Vein> allVeins;
  private Map<TrianglePoint, Integer> sentRobots;
  private Map<TrianglePoint, Integer> launchedRobots;
  private Map<Material, Integer> usedMaterials;
  private int usedMoney;
  private Game game;
  private final int MaxRank = 3;

  private void initialize(Game game) {
    this.game = game;
    allVeins = game.getField().getVeinMap();
    sentRobots = new HashMap<TrianglePoint, Integer>();
    launchedRobots = new HashMap<TrianglePoint, Integer>();
    usedMaterials = new HashMap<Material, Integer>();
    usedMoney = 0;
    for (Squad squad : game.getField().getSquads()) {
      TrianglePoint destination = squad.getDestinationLocation();
      increment(sentRobots, destination, squad.getRobot()
          * (squad.getOwnerId() == game.getMyPlayer().getId() ? 1 : -1));
    }
  }

  @Override
  public String getName() {
    return "JoeJack";
  }

  private Vein firstSelectedVein;

  @Override
  public TrianglePoint selectVein(Game game) {
    initialize(game);
    List<TrianglePoint> all = new ArrayList<TrianglePoint>();
    all.addAll(allVeins.keySet());
    Collections.sort(all, veinValueComparator);
    TrianglePoint best = null;
    for (TrianglePoint point : all) {
      if (allVeins.get(point).getOwnerId() == game.getNeutralPlayerId()
          && (firstSelectedVein == null || allVeins.get(point).getShortestPath(firstSelectedVein)
              .size() < game.getSetting().getMapSize())) {
        best = point;
        break;
      }
    }
    if (firstSelectedVein == null && best != null) {
      firstSelectedVein = allVeins.get(best);
    }
    return best;
  }

  private void launch(List<Command> commands, int robotsToLaunch, TrianglePoint from,
      TrianglePoint to) {
    addCommand(commands, Commands.launch(robotsToLaunch, from, to));
    increment(sentRobots, to, robotsToLaunch);
    increment(launchedRobots, from, robotsToLaunch);
  }

  @Override
  public List<Command> selectActions(Game game) {
    // initialize
    initialize(game);
    List<Command> commands = new ArrayList<Command>();
    ArrayList<TrianglePoint> myVeinPoints = new ArrayList<TrianglePoint>();
    ArrayList<TrianglePoint> otherVeinPoints = new ArrayList<TrianglePoint>();
    for (TrianglePoint point : allVeins.keySet()) {
      if (allVeins.get(point).getOwnerId() == game.getMyPlayer().getId()) {
        myVeinPoints.add(point);
      } else {
        otherVeinPoints.add(point);
      }
    }

    // launch
    Collections.sort(myVeinPoints, frontVeinComparator);
    Collections.reverse(myVeinPoints);
    for (TrianglePoint myPoint : myVeinPoints) {
      TargetVeinComparator targetVeinComparator = new TargetVeinComparator();
      targetVeinComparator.setSource(myPoint);
      Collections.sort(otherVeinPoints, targetVeinComparator);
      for (TrianglePoint otherPoint : otherVeinPoints.subList(0,
        otherVeinPoints.size() / 4)) {
        int robotsToLaunch =
            getRequiredRobots(myPoint, otherPoint) + 1 - getValue(sentRobots, otherPoint);
        if (robotsToLaunch > 0
            && robotsToLaunch <= getRemainingRobots(myPoint) - getValue(launchedRobots, myPoint)) {
          launch(commands, robotsToLaunch, myPoint, otherPoint);
        }
      }
      final int NumberOfRobotsToHold = 100;
      for (TrianglePoint otherPoint : otherVeinPoints) {
        int robotsToLaunch = getRemainingRobots(myPoint) - NumberOfRobotsToHold;
        if (robotsToLaunch <= 0) {
          break;
        } else {
          launch(commands, robotsToLaunch, myPoint, otherPoint);
        }
      }
    }

    // upgrade
    Collections.sort(myVeinPoints, veinValueComparator);
    for (TrianglePoint point : myVeinPoints) {
      GameSetting setting = game.getSetting();

      int currentMaterialRank = allVeins.get(point).getMaterialRank();
      Map<Material, Integer> requiredMaterialsForMaterial = new HashMap<Material, Integer>();
      for (Material material : Material.values()) {
        int requiredAmount;
        if (currentMaterialRank == 1) {
          requiredAmount = setting.getMaterialsForUpgradingMaterialRankFrom1To2(material);
        } else {
          requiredAmount = setting.getMaterialsForUpgradingMaterialRankFrom2To3(material);
        }
        requiredMaterialsForMaterial.put(material, requiredAmount);
      }
      upgrade(requiredMaterialsForMaterial, Commands.upgradeMaterial(point), point,
          currentMaterialRank, commands);

      int currentRobotRank = allVeins.get(point).getRobotRank();
      Map<Material, Integer> requiredMaterialsForRobot = new HashMap<Material, Integer>();
      for (Material material : Material.values()) {
        int requiredAmount;
        if (currentRobotRank == 1) {
          requiredAmount = setting.getMaterialsForUpgradingRobotRankFrom1To2(material);
        } else {
          requiredAmount = setting.getMaterialsForUpgradingRobotRankFrom2To3(material);
        }
        requiredMaterialsForRobot.put(material, requiredAmount);
      }
      upgrade(requiredMaterialsForRobot, Commands.upgradeRobot(point), point, currentRobotRank,
          commands);
    }

    // trade : prepare
    final int MaxTradeAmount = 100;
    final int MaterialAmountToHold = 300;
    List<Material> materialsToBuy = new ArrayList<Material>();
    List<Material> materialsToSell = new ArrayList<Material>();
    for (Material material : Material.values()) {
      if (getRemainingMaterials(material) < MaterialAmountToHold) {
        materialsToBuy.add(material);
      } else if (getRemainingMaterials(material) > MaterialAmountToHold) {
        materialsToSell.add(material);
      }
    }

    // trade : accept their trades
    for (PlayerTrade trade : game.getPlayerTrades()) {
      Material material = trade.getMaterial();
      if (trade.getTradeType() == TradeType.Offer
          && trade.getPricePerOneMaterial() < getDefaultPrice(material)
          && materialsToBuy.contains(material)) {
        addCommand(commands, Commands.buyFromPlayerTrade(trade, Math.min(trade.getAmount(),
            Math.min(getMaximumAmountFromRemainingMoney(trade.getPricePerOneMaterial()),
                MaxTradeAmount))));
      } else if (trade.getTradeType() == TradeType.Demand
          && trade.getPricePerOneMaterial() > getDefaultPrice(material)
          && materialsToSell.contains(material)) {
        addCommand(
            commands,
            Commands.sellToPlayerTrade(
                trade,
                Math.min(trade.getAmount(),
                    Math.min(getRemainingMaterials(material), MaxTradeAmount))));
      }
    }

    // trade : send our trade requests
    for (Material material : materialsToBuy) {
      int amount = Math.min(getRemainingMoney() / getDefaultPrice(material), MaxTradeAmount);
      if (amount > 0) {
        addCommand(commands, Commands.demand(material, amount, getDefaultPrice(material)));
      }
    }
    for (Material material : materialsToSell) {
      int amount = Math.min(getRemainingMaterials(material), MaxTradeAmount);
      if (amount > 0) {
        addCommand(commands, Commands.offer(material, amount, getDefaultPrice(material)));
      }
    }

    return commands;
  }

  private int getDefaultPrice(Material material) {
    return game.getAlienTrade().getBuyPriceOf(material) / 2;
  }

  private int getMaximumAmountFromRemainingMoney(int price) {
    return getRemainingMoney() / price;
  }

  private void upgrade(Map<Material, Integer> requiredMaterials, Command upgradeCommand,
      TrianglePoint point, int currentRank, List<Command> commands) {
    if (currentRank < MaxRank) {
      boolean hasEnoughMaterials = true;
      for (Material material : requiredMaterials.keySet()) {
        if (getRemainingMaterials(material) < requiredMaterials.get(material)) {
          hasEnoughMaterials = false;
          break;
        }
      }
      if (hasEnoughMaterials) {
        addCommand(commands, upgradeCommand);
        for (Material material : requiredMaterials.keySet()) {
          increment(usedMaterials, material, requiredMaterials.get(material));
        }
      }
    }
  }

  private void addCommand(List<Command> commands, Command command) {
    commands.add(command);
    this.saveTemporalCommands(commands);
  }

  private <K> void increment(Map<K, Integer> map, K key, Integer value) {
    map.put(key, (map.containsKey(key) ? map.get(key) : 0) + value);
  }

  private <K> Integer getValue(Map<K, Integer> map, K key) {
    if (map.containsKey(key)) {
      return map.get(key);
    } else {
      return 0;
    }
  }

  private int evaluateVein(TrianglePoint from, TrianglePoint to) {
    if (getRequiredRobots(from, to) <= 0) {
      return Integer.MIN_VALUE / 10;
    }
    int distance = from.getShortestPath(to).size();
    if (distance > game.getSetting().getMapSize()) {
      return Integer.MIN_VALUE / 10*2;
    }
    int evaluation = 0;
    evaluation -= getRequiredRobots(from, to);
    evaluation += allVeins.get(from).getNumberOfRobots();
    evaluation -=
        distance
            * (allVeins.get(to).getOwnerId() == game.getNeutralPlayerId() ? 5 : allVeins.get(to)
                .getCurrentRobotProductivity());
    return evaluation;
  }

  private int getRequiredRobots(TrianglePoint from, TrianglePoint to) {
    Vein targetVein = allVeins.get(to);
    int productivity =
        (targetVein.getOwnerId() == game.getNeutralPlayerId() ? 0 : targetVein
            .getCurrentRobotProductivity());
    int robots = targetVein.getNumberOfRobots() + from.getShortestPath(to).size() * productivity;
    return robots;
  }

  private int getRemainingMaterials(Material material) {
    return game.getMyPlayer().getMaterial(material) - getValue(usedMaterials, material);
  }

  private int getRemainingMoney() {
    return game.getMyPlayer().getMoney() - usedMoney;
  }

  private int getRemainingRobots(TrianglePoint vein) {
    return allVeins.get(vein).getNumberOfRobots() - getValue(launchedRobots, vein);
  }

  public Comparator<TrianglePoint> veinValueComparator = new Comparator<TrianglePoint>() {
    private int getValue(Vein vein) {
      return vein.getCurrentMaterialProductivity() + vein.getCurrentRobotProductivity();
    }

    public int compare(TrianglePoint left, TrianglePoint right) {
      return getValue(allVeins.get(right)) - getValue(allVeins.get(left));
    }
  };

  public class TargetVeinComparator implements Comparator<TrianglePoint> {
    private TrianglePoint source;

    public void setSource(TrianglePoint source) {
      this.source = source;
    }

    public int compare(TrianglePoint left, TrianglePoint right) {
      return evaluateVein(source, right) - evaluateVein(source, left);
    }
  };

  public Comparator<TrianglePoint> frontVeinComparator = new Comparator<TrianglePoint>() {
    private int getValue(TrianglePoint from) {
      int value = 0;
      int nearFriendlies = 0;
      for (TrianglePoint to : allVeins.keySet()) {
        if (allVeins.get(to).getOwnerId() != game.getMyPlayer().getId()) {
          value -= from.getShortestPath(to).size();
        } else {
          value -= game.getSetting().getMapSize() * 2;
          nearFriendlies++;
        }
      }
      if (nearFriendlies < 2) {
        return Integer.MIN_VALUE / 10;
      } else {
        return value;
      }
    }

    public int compare(TrianglePoint left, TrianglePoint right) {
      return getValue(right) - getValue(left);
    }
  };
}

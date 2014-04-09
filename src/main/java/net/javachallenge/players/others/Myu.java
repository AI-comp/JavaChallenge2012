package net.javachallenge.players.others;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.javachallenge.api.ComputerPlayer;
import net.javachallenge.api.Field;
import net.javachallenge.api.Game;
import net.javachallenge.api.Material;
import net.javachallenge.api.Player;
import net.javachallenge.api.TrianglePoint;
import net.javachallenge.api.Vein;
import net.javachallenge.api.command.Command;
import net.javachallenge.api.command.Commands;

public class Myu extends ComputerPlayer {
  private List<TrianglePoint> veinPoints;
  private List<TrianglePoint> myVeinPoints = new ArrayList<TrianglePoint>();
  private static boolean isFirst = true;

  @Override
  public String getName() {
    return "Myu";
  }

  @Override
  public TrianglePoint selectVein(Game game) {
    Field field = game.getField();
    Map<TrianglePoint, Vein> veins = field.getVeinMap();
    Object[] tp = veins.keySet().toArray();
    Point edge = getEdgeVeins(tp);

    veinPoints = categolizeVeins(tp, edge);
    return selectPoint(veins);
  }

  private Point getEdgeVeins(Object[] tp) {
    int maxX = 0;
    int minX = 100;
    int maxY = 0;
    int minY = 0;

    for (int i = tp.length - 1; i >= 0; i--) {
      TrianglePoint t = (TrianglePoint) tp[i];
      int tmpX = t.getX();
      int tmpY = t.getY();

      if (maxX < tmpX) {
        maxX = tmpX;
      } else if (minX > tmpX) {
        minX = tmpX;
      }

      if (maxY < tmpY) {
        maxY = tmpY;
      } else if (minY > tmpY) {
        minY = tmpY;
      }
    }
    return new Point(maxX + minX, maxY + minY);
  }

  private List<TrianglePoint> categolizeVeins(Object[] tp, Point edge) {
    int maxNum = 0;
    int zone = 0;
    List<TrianglePoint> seVeins = new ArrayList<TrianglePoint>();
    List<TrianglePoint> swVeins = new ArrayList<TrianglePoint>();
    List<TrianglePoint> neVeins = new ArrayList<TrianglePoint>();
    List<TrianglePoint> nwVeins = new ArrayList<TrianglePoint>();

    for (int i = tp.length - 1; i >= 0; i--) {
      TrianglePoint t = (TrianglePoint) tp[i];
      if (t.getX() >= edge.x / 2) {
        if (t.getY() >= edge.y / 2) {
          seVeins.add(t);
          if (seVeins.size() > maxNum) {
            maxNum = seVeins.size();
            zone = 0;
          }
        } else {
          neVeins.add(t);
          if (neVeins.size() > maxNum) {
            maxNum = neVeins.size();
            zone = 3;
          }
        }
      } else {
        if (t.getY() >= edge.x / 2) {
          swVeins.add(t);
          if (swVeins.size() > maxNum) {
            maxNum = swVeins.size();
            zone = 2;
          }
        } else {
          nwVeins.add(t);
          if (nwVeins.size() > maxNum) {
            maxNum = nwVeins.size();
            zone = 1;
          }
        }
      }
    }

    List<TrianglePoint> points = new ArrayList<TrianglePoint>();
    switch (zone) {
      case 0:
        points.addAll(seVeins);
        points.addAll(swVeins);
        points.addAll(neVeins);
        points.addAll(nwVeins);
        return points;
      case 1:
        points.addAll(nwVeins);
        points.addAll(neVeins);
        points.addAll(swVeins);
        points.addAll(seVeins);
        return points;
      case 2:
        points.addAll(swVeins);
        points.addAll(seVeins);
        points.addAll(nwVeins);
        points.addAll(neVeins);
        return points;
      case 3:
        points.addAll(neVeins);
        points.addAll(nwVeins);
        points.addAll(seVeins);
        points.addAll(swVeins);
        return points;
    }
    return seVeins;
  }

  private TrianglePoint selectPoint(Map<TrianglePoint, Vein> veins) {
    int maxRP = 0;
    Vein vein;
    TrianglePoint point = veinPoints.get(0);
    for (TrianglePoint t : veinPoints) {
      vein = veins.get(t);
      int rp = vein.getInitialRobotProductivity();
      if (isFirst) {
        if (maxRP <= rp && vein.getOwnerId() == -1 && vein.getMaterial() == Material.Gas) {
          maxRP = rp;
          point = t;
        }
      } else {
        if (maxRP <= rp && vein.getOwnerId() == -1 && vein.getMaterial() == Material.Metal) {
          maxRP = rp;
          point = t;
        }
      }
    }

    isFirst = false;
    myVeinPoints.add(point);
    return point;
  }

  @Override
  public List<Command> selectActions(Game game) {
    Player myself = game.getMyPlayer();
    Field field = game.getField();
    Map<TrianglePoint, Vein> veinlist = field.getVeinMap();
    int robots =
        (int) ((veinlist.get(myVeinPoints.get(0)).getNumberOfRobots() + veinlist.get(
            myVeinPoints.get(1)).getNumberOfRobots()) * 0.8);

    List<Command> commands = new ArrayList<Command>();
    boolean ugrbt = true;
    boolean ugmtl = true;
    for (TrianglePoint tp : myVeinPoints) {
      Vein v = veinlist.get(tp);
      int rank = v.getRobotRank();

      if (ugmtl
          && myself.getMaterial(Material.Stone) > game.getSetting()
              .getMaterialsForUpgradingMaterialRankFrom2To3(Material.Stone)
          && myself.getMaterial(Material.Gas) > game.getSetting()
              .getMaterialsForUpgradingRobotRankFrom2To3(Material.Gas)) {
        if (rank < 3) {
          commands.add(Commands.upgradeMaterial(v.getLocation()));
          ugmtl = false;
        }
      }

      if (ugmtl
          && myself.getMaterial(Material.Stone) > game.getSetting()
              .getMaterialsForUpgradingMaterialRankFrom1To2(Material.Stone)
          && myself.getMaterial(Material.Gas) > game.getSetting()
              .getMaterialsForUpgradingRobotRankFrom1To2(Material.Gas)) {
        if (rank < 2) {
          commands.add(Commands.upgradeMaterial(v.getLocation()));
          ugmtl = false;
        }
      }

      if (ugrbt
          && myself.getMaterial(Material.Metal) > game.getSetting()
              .getMaterialsForUpgradingRobotRankFrom2To3(Material.Metal)
          && myself.getMaterial(Material.Gas) > game.getSetting()
              .getMaterialsForUpgradingRobotRankFrom2To3(Material.Gas)) {
        if (rank < 3) {
          commands.add(Commands.upgradeRobot(v.getLocation()));
          ugrbt = false;
        }
      }

      if (ugrbt
          && myself.getMaterial(Material.Metal) > game.getSetting()
              .getMaterialsForUpgradingRobotRankFrom1To2(Material.Metal)
          && myself.getMaterial(Material.Gas) > game.getSetting()
              .getMaterialsForUpgradingRobotRankFrom1To2(Material.Gas)) {
        if (rank < 2) {
          commands.add(Commands.upgradeRobot(v.getLocation()));
          ugrbt = false;
        }
      }

    }

    Iterator<TrianglePoint> tp = veinPoints.iterator();
    while (tp.hasNext()) {
      TrianglePoint location = tp.next();
      if (veinlist.get(location).getOwnerId() == game.getNeutralPlayerId()) {
        if (veinlist.get(location).getNumberOfRobots() < robots * 0.8) {
          for (TrianglePoint p : myVeinPoints) {
            Vein v = veinlist.get(p);
            commands.add(Commands.launch((int) (v.getNumberOfRobots() * 0.9), v.getLocation(),
                location));
          }
          myVeinPoints.add(location);
          tp.remove();
          break;
        }
      }
    }

    this.saveTemporalCommands(commands);
    return commands;
  }
}

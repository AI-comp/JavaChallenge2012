package net.javachallenge.players.others;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.javachallenge.api.ComputerPlayer;
import net.javachallenge.api.Game;
import net.javachallenge.api.Material;
import net.javachallenge.api.TrianglePoint;
import net.javachallenge.api.Vein;
import net.javachallenge.api.command.Command;
import net.javachallenge.api.command.Commands;

public class Sabateur extends ComputerPlayer {

  @Override
  public String getName() {
    return "Sabateur";
  }

  @Override
  public TrianglePoint selectVein(Game game) {
    Map<TrianglePoint, Vein> allVeins = game.getField().getVeinMap();
    TrianglePoint bestPoint = null;

    for (TrianglePoint point : allVeins.keySet()) {
      if (bestPoint == null) {
        bestPoint = point;
      } else if (allVeins.get(point).getInitialRobotProductivity() * 5
          + allVeins.get(point).getNumberOfRobots() > allVeins.get(bestPoint)
          .getInitialRobotProductivity() * 5 + allVeins.get(bestPoint).getNumberOfRobots()) {
        bestPoint = point;
      }
    }

    return bestPoint;
  }

  @Override
  public List<Command> selectActions(Game game) {
    List<Command> commands = new ArrayList<Command>();

    Map<TrianglePoint, Vein> allVeins = game.getField().getVeinMap();
    ArrayList<TrianglePoint> myVeinPoints = new ArrayList<TrianglePoint>();
    ArrayList<TrianglePoint> enemyVeinPoints = new ArrayList<TrianglePoint>();
    ArrayList<TrianglePoint> otherVeinPoints = new ArrayList<TrianglePoint>();

    for (TrianglePoint point : game.getField().getVeinMap().keySet()) {
      if (allVeins.get(point).getOwnerId() == game.getMyPlayer().getId()) {
        myVeinPoints.add(point);
      } else if (allVeins.get(point).getOwnerId() != -1) {
        enemyVeinPoints.add(point);
      } else {
        otherVeinPoints.add(point);
      }
    }

    this.saveTemporalCommands(commands);

    for (TrianglePoint point : myVeinPoints) {
      int tmp = 0;
      for (TrianglePoint opoint : otherVeinPoints) {
        if (Math.abs(point.getX() - opoint.getX()) < 4 + game.getRound() / 20
            && Math.abs(point.getY() - opoint.getY()) < 4 + game.getRound() / 20) {
          if (game.getField().getVein(point).getNumberOfRobots() - tmp > game.getField()
              .getVein(opoint).getNumberOfRobots()
              + game.getRound() * 2) {
            tmp += game.getField().getVein(opoint).getNumberOfRobots() + 1;
            commands.add(Commands.launch(game.getField().getVein(opoint).getNumberOfRobots() + 1,
                point, opoint));
          }
        }
      }
    }

    if (game.getRound() > 20) {
      for (TrianglePoint point : myVeinPoints) {
        int tmp = 0;
        for (TrianglePoint epoint : enemyVeinPoints) {
          if (Math.abs(point.getX() - epoint.getX()) < 4 + game.getRound() / 20
              && Math.abs(point.getY() - epoint.getY()) < 4 + game.getRound() / 20) {
            if (game.getField().getVein(point).getNumberOfRobots() - tmp > game.getField()
                .getVein(epoint).getNumberOfRobots()
                * 2 - game.getRound() * 3) {
              if (game.getField().getVein(point).getNumberOfRobots() - tmp > game.getField()
                  .getVein(epoint).getNumberOfRobots()) {
                tmp += game.getField().getVein(epoint).getNumberOfRobots() + 1;
                commands.add(Commands.launch(
                    game.getField().getVein(epoint).getNumberOfRobots() + 1, point, epoint));

              }
            }
          }
        }
      }
    }

    this.saveTemporalCommands(commands);

    for (TrianglePoint point : myVeinPoints) {
      if (game.getField().getVein(point).getRobotRank() == 1) {
        if (game.getMyPlayer().getMaterial(Material.Gas) > 200
            && game.getMyPlayer().getMaterial(Material.Stone) > 200) {
          commands.add(Commands.upgradeRobot(point));
          break;
        }
      } else {
        if (game.getMyPlayer().getMaterial(Material.Stone) > 300
            && game.getMyPlayer().getMaterial(Material.Metal) > 500) {
          commands.add(Commands.upgradeRobot(point));
          break;
        }
      }
    }

    return commands;
  }

}

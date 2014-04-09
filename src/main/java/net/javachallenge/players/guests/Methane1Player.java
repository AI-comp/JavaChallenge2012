package net.javachallenge.players.guests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

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

public class Methane1Player extends ComputerPlayer {
	private Player self;
	private List<Command> commands;
	private Game game;
	int turn;

	int stone, gas, metal;

	class OwnVein {
		Vein vein;
		int robots;
		TrianglePoint location;
		int robotRank;
		int materialRank;
		int commingSquads;
		boolean emerge = false;

		OwnVein(Vein v) {
			vein = v;
			robots = v.getNumberOfRobots();
			location = v.getLocation();
			robotRank = v.getRobotRank();
			materialRank = v.getMaterialRank();
			commingSquads = 0;
			for (Squad s : game.getField().getSquads()) {
				if (s.getDestinationLocation().equals(location)) {
					commingSquads += s.getRobot();
					if (s.getCurrentLocation().getDistance(location) == 1) {
						if (s.getRobot() > robots
								+ v.getCurrentRobotProductivity()) {
							emerge = true;
						}
					}
				}
			}
		}

		boolean tryUpgradeRobot() {
			if (robotRank == 1 && metal >= 200 && gas >= 200) {
				commands.add(Commands.upgradeRobot(vein));
				metal -= 200;
				gas -= 200;
				robotRank++;
				return true;
			} else if (robotRank == 2 && metal >= 500 && stone >= 300) {
				commands.add(Commands.upgradeRobot(vein));
				metal -= 500;
				stone -= 300;
				robotRank++;
				return true;
			}
			return false;
		}

		boolean tryUpgradeMaterial() {
			if (materialRank == 1 && gas >= 200 && stone >= 100) {
				commands.add(Commands.upgradeMaterial(vein));
				gas -= 200;
				stone -= 100;
				return true;
			} else if (materialRank == 2 && gas >= 100 && stone >= 300) {
				commands.add(Commands.upgradeMaterial(vein));
				gas -= 100;
				stone -= 300;
				return true;
			}
			return false;
		}

		void attack(Vein target, int squadSize, boolean force) {
			if (squadSize > robots) {
				System.err.println("Warn: too big squad size.");
				squadSize = robots;
			}
			List<TrianglePoint> route = makeRoute(location,
					target.getLocation());
			// List<TrianglePoint> route =
			// location.getShortestPath(target.getLocation());
			if (route == null) {
				if (force) {
					commands.add(Commands.launch(squadSize, location,
							target.getLocation()));
					saveTemporalCommands(commands);
					return;
				} else
					return;
			}

			robots -= squadSize;
			commands.add(Commands.launch(squadSize, location,
					target.getLocation()));
			// commands.add(Commands.launchWithPath(squadSize, route));
			saveTemporalCommands(commands);
			System.err.println("Move from " + location + " to "
					+ target.getLocation() + " with " + squadSize + " robots.");
			// System.err.println("Route:" + route);
		}

		public boolean equals(Object other) {
			OwnVein ov = (OwnVein) other;
			return ov.location == this.location;
		}

		public int hashCode() {
			return location.hashCode();
		}

		ArrayList<OwnVein> nearFriends() {
			ArrayList<OwnVein> veins = new ArrayList<>();
			for (Vein v : game.getField().getVeinsOfSameOwnerOrderedByDistance(
					vein)) {
				veins.add(myVeinsByLocation.get(v.getLocation()));
			}
			return veins;
		}

		List<Vein> nearEnemies() {
			return game.getField().getVeinsOfOtherOwnersOrderedByDistance(vein);
		}
	}

	TreeMap<TrianglePoint, OwnVein> myVeinsByLocation = new TreeMap<>();
	ArrayList<OwnVein> myVeins = new ArrayList<>();

	@Override
	public String getName() {
		return "methane1";
	}

	Vein firstVein = null;

	@Override
	public TrianglePoint selectVein(Game game) {
		int gas_ratio = 2;
		int metal_ratio = 3;
		TrianglePoint center = Make.point(1, 0);

		if (firstVein != null) {
			switch (firstVein.getMaterial()) {
			case Metal:
				metal_ratio = 1;
				break;
			case Gas:
				gas_ratio = 1;
				break;
			default:
				break;
			}
		}
		int bestScore = Integer.MIN_VALUE;
		Vein bestVein = null;

		for (Vein v : game.getField().getVeins()) {
			if (v.getOwnerId() != -1) {
				continue;
			}
			int score = 0;

			score += center.getDistance(v.getLocation());
			score += v.getNumberOfRobots() * 8;
			score += v.getInitialRobotProductivity() * 40;
			switch (v.getMaterial()) {
			case Gas:
				score += gas_ratio * v.getInitialMaterialProductivity();
				break;
			case Metal:
				score += metal_ratio * v.getInitialMaterialProductivity();
				break;
			default:
				break;
			}
			for (Vein ov : game.getField().getVeins()) {
				if (ov.getOwnerId() == -1)
					continue;
				int d = ov.getDistance(v);
				if (d < 16) {
					score -= 16 - d;
					if (d < 10) {
						score -= 10 - d;
					}
					if (d < 6) {
						score -= 6 - d;
					}
				}
				score -= ov.getDistance(v);

			}
			if (score > bestScore) {
				bestVein = v;
				bestScore = score;
			}
		}
		return bestVein.getLocation();
	}

	@Override
	public List<Command> selectActions(Game game) {
		this.game = game;
		turn = game.getRound();
		self = game.getMyPlayer();
		gas = self.getMaterial(Material.Gas);
		stone = self.getMaterial(Material.Stone);
		metal = self.getMaterial(Material.Metal);

		myVeins.clear();
		myVeinsByLocation.clear();
		for (Vein v : game.getField().getVeins(self.getId())) {
			OwnVein ov = new OwnVein(v);
			myVeins.add(ov);
			myVeinsByLocation.put(v.getLocation(), ov);
		}
		commands = new ArrayList<>();

		upgradeVeins();
		saveTemporalCommands(commands);

		doAttack();
		return commands;
	}

	private static class LargerRobotProducer implements Comparator<OwnVein> {
		@Override
		public int compare(OwnVein o1, OwnVein o2) {
			return o2.vein.getInitialRobotProductivity()
					- o1.vein.getInitialRobotProductivity();
		}
	}

	private static class MoreMaterialProducer implements Comparator<OwnVein> {
		@Override
		public int compare(OwnVein o1, OwnVein o2) {
			int v1 = o1.vein.getInitialMaterialProductivity();
			int v2 = o2.vein.getInitialMaterialProductivity();
			// TODO: 資源の種類に対して重み付け
			// TODO: vein の安全さに対して重み付け
			return v2 - v1;
		}
	}

	void doTrade() {
		if (stone > 1200) {
			commands.add(Commands
					.sellToAlienTrade(Material.Stone, stone - 1200));
		}
		if (gas > 1200) {
			commands.add(Commands.sellToAlienTrade(Material.Gas, gas - 1200));
		}
		if (metal > 1600) {
			commands.add(Commands
					.sellToAlienTrade(Material.Metal, metal - 1600));
		}
		if (metal < 400) {
			commands.add(Commands.buyFromAlienTrade(Material.Metal, 400));
		}
		if (gas < 300) {
			commands.add(Commands.buyFromAlienTrade(Material.Gas, 300));
		}
		if (metal < 400) {
			commands.add(Commands.buyFromAlienTrade(Material.Metal, 400));
		}
		if (gas < 300) {
			commands.add(Commands.buyFromAlienTrade(Material.Gas, 300));
		}
		if (stone < 300) {
			commands.add(Commands.buyFromAlienTrade(Material.Stone, 300));
		}
	}

	/** 所有している vein の rank up を行う. */
	private void upgradeVeins() {
		Collections.sort(myVeins, new LargerRobotProducer());
		for (OwnVein vein : myVeins) {
			vein.tryUpgradeRobot();
		}
		Collections.sort(myVeins, new MoreMaterialProducer());
		for (OwnVein vein : myVeins) {
			vein.tryUpgradeMaterial();
		}
		doTrade();
	}

	private static class MoreRobots implements Comparator<OwnVein> {
		@Override
		public int compare(OwnVein v1, OwnVein v2) {
			return v2.robots - v1.robots;
		}
	}

	private static int getRemainTurn(Squad s) {
		TrianglePoint loc = s.getCurrentLocation();
		TrianglePoint dest = s.getDestinationLocation();
		List<TrianglePoint> path = s.getPath();

		for (int i = 0; i < path.size(); ++i) {
			if (path.get(i).equals(dest)) {
				return path.size() - i;
			}
		}
		System.err.println("Warn: can't calculate remain turn");
		return loc.getDistance(s.getDestinationLocation());
	}

	private static class NearDestination implements Comparator<Squad> {
		@Override
		public int compare(Squad s1, Squad s2) {
			return getRemainTurn(s1) - getRemainTurn(s2);
		}
	}

	private boolean _makeRouteSub(TrianglePoint from, TrianglePoint to,
			List<TrianglePoint> route, int t) {
		int x = from.getX();
		int y = from.getY();

		if (from.equals(to))
			return true;

		if (t > 0) {
			for (Squad s : game.getField().getSquads()) {
				int now;
				TrianglePoint cur = s.getCurrentLocation();
				List<TrianglePoint> sp = s.getPath();
				for (now = 0; now < sp.size(); ++now) {
					if (sp.get(now).equals(cur)) {
						break;
					}
				}
				// System.err.println("now="+now+" path="+sp.size() + " t="+t);
				if (now + t - 1 < sp.size()) {
					if (sp.get(now + t - 1).equals(from)) {
						System.err.println("Boom: " + from);
						return false;
					}
				}
				if (now + t < sp.size()) {
					if (sp.get(now + t).equals(from)) {
						System.err.println("Boom: " + from);
						return false;
					}
				}
			}
		}

		int dist = from.getDistance(to);

		TrianglePoint next = Make.point(x - 1, y);
		if (next.getDistance(to) < dist) {
			route.add(next);
			if (_makeRouteSub(next, to, route, t + 1)) {
				return true;
			}
			route.remove(t + 1);
		}

		next = Make.point(x + 1, y);
		if (next.getDistance(to) < dist) {
			route.add(next);
			if (_makeRouteSub(next, to, route, t + 1)) {
				return true;
			}
			route.remove(t + 1);
		}

		if ((x + y) % 2 == 0) {
			// if (from.isUpwardTriangle()) {
			next = Make.point(x, y + 1);
		} else {
			next = Make.point(x, y - 1);
		}
		if (next.getDistance(to) < dist) {
			route.add(next);
			if (_makeRouteSub(next, to, route, t + 1)) {
				return true;
			}
			route.remove(t + 1);
		}
		return false;
	}

	/** 攻撃するときのルート計算 */
	List<TrianglePoint> makeRoute(TrianglePoint from, TrianglePoint to) {
		List<TrianglePoint> route = new ArrayList<>();
		route.add(from);
		if (_makeRouteSub(from, to, route, 0)) {
			return route;
		}
		return null;
	}

	/** 攻撃を行う */
	private void doAttack() {
		Collections.sort(myVeins, new MoreRobots());
		Field field = game.getField();
		List<Squad> squads = field.getSquads();

		TreeSet<TrianglePoint> targetted = new TreeSet<>();

		int defence = 0;
		int veins = 0;
		for (Vein v : field.getVeinsOfOtherOwnersOrderedByDistance(myVeins
				.get(0).vein)) {
			if (v.getOwnerId() == game.getNeutralPlayerId())
				continue;
			veins++;
			defence += v.getNumberOfRobots();
		}
		if (game.getRound() > 100) {
			defence *= 200 - game.getRound();
		} else {
			defence *= game.getRound();
		}
		defence /= 100 * veins;
		defence += 5;

		// defence = (int)(defence * (1.0 + ((double) game.getRound() / 200.0 -
		// 0.5)) / veins);

		System.err.println("Round: " + game.getRound() + " / Defence: "
				+ defence);

		for (OwnVein vein : myVeins) {
			int mindist = Integer.MAX_VALUE;
			int def2 = defence;
			List<Vein> nearEnemies = vein.nearEnemies();
			for (Vein ev : nearEnemies) {
				if (ev.getOwnerId() == game.getNeutralPlayerId())
					continue;
				int d = ev.getDistance(vein.vein);
				if (d > 8)
					break;
				int x = ev.getNumberOfRobots() - d
						* vein.vein.getCurrentRobotProductivity();
				if (x > 0)
					def2 += x;
				def2 += vein.commingSquads;
			}

			targetloop: for (Vein enemyVein : nearEnemies) {
				TrianglePoint loc = enemyVein.getLocation();
				int enemyRobots = enemyVein.getNumberOfRobots();
				if (targetted.contains(loc)) {
					continue;
				}

				int dist = loc.getDistance(vein.location);
				if (dist < mindist)
					mindist = dist;
				if (dist > 16) {
					break;
				}

				ArrayList<Squad> targetSquads = new ArrayList<>();
				for (Squad s : squads) {
					if (s.getDestinationLocation().equals(loc)) {
						if (s.getCurrentLocation().getDistance(loc) >= dist) {
							continue targetloop;
						}
						if (s.getOwnerId() == self.getId()) {
							enemyRobots -= s.getRobot();
							if (enemyRobots < 0)
								enemyRobots = 0;
						}
						targetSquads.add(s);
					}
				}

				Collections.sort(targetSquads, new NearDestination());
				int enemyId = enemyVein.getOwnerId();
				int t = 0;
				for (Squad s : targetSquads) {
					int r = getRemainTurn(s);
					if (r >= dist) {
						continue targetloop;
					}
					if (enemyId != game.getNeutralPlayerId()) {
						enemyRobots += (r - t)
								* enemyVein.getCurrentRobotProductivity();
					}
					t = r;
					if (enemyRobots < s.getRobot()) {
						enemyId = s.getOwnerId();
						enemyRobots = s.getRobot() - enemyRobots;
					} else {
						enemyRobots -= s.getRobot();
					}
				}
				if (enemyId != game.getNeutralPlayerId()) {
					enemyRobots += (dist - t)
							* enemyVein.getCurrentRobotProductivity();
				}
				enemyRobots += (dist / 2);

				// ターゲットの周辺の敵基地の強さを勘案する. 取ってすぐ取り返されるなら最初から取らない.
				if (turn > 4 || dist > 6
						|| enemyVein.getOwnerId() == game.getNeutralPlayerId()) {
					for (Vein v : game.getField()
							.getVeinsOfOtherOwnersOrderedByDistance(enemyVein)) {
						int r = v.getDistance(enemyVein);
						if (r > 6)
							break;
						if (v.getOwnerId() != self.getId()
								&& v.getOwnerId() != game.getNeutralPlayerId()) {
							int x = v.getNumberOfRobots()
									- enemyVein.getCurrentRobotProductivity()
									* r;
							if (x > 0)
								enemyRobots += x / 2;
						}
					}
					for (Vein v : game.getField()
							.getVeinsOfSameOwnerOrderedByDistance(enemyVein)) {
						int r = v.getDistance(enemyVein);
						if (r > 6)
							break;
						int x = v.getNumberOfRobots()
								- enemyVein.getCurrentRobotProductivity() * r;
						if (x > 0)
							enemyRobots += x / 2;
					}
				}

				if (vein.emerge && enemyRobots < vein.robots) {
					targetted.add(loc);
					vein.attack(enemyVein, vein.robots, false);
					break;
				}
				if (enemyRobots + def2 < vein.robots) {
					targetted.add(loc);
					int squadSize = vein.robots - def2;
					vein.attack(enemyVein, squadSize, false);
					break;
				}
			}

			int distBonus = mindist * vein.vein.getCurrentRobotProductivity()
					/ 2;
			if (distBonus >= defence)
				distBonus = defence - 1;
			if (mindist > 10
					&& (vein.robots - vein.commingSquads) > (defence - distBonus)) {
				// move to mine.
				for (OwnVein nearVein : vein.nearFriends()) {
					if (nearVein.nearEnemies().get(0)
							.getDistance(nearVein.vein) < mindist) {
						int squadSize = vein.robots - vein.commingSquads
								- defence + distBonus;
						System.err.println("Move to friend: from="
								+ vein.location + " to=" + nearVein.location
								+ " robots=" + vein.robots + " mindist="
								+ mindist + " defence=" + defence
								+ " distBonus=" + distBonus + "squad="
								+ squadSize);
						vein.attack(nearVein.vein, vein.robots - defence
								+ distBonus, true);
						break;
					}
				}
			}

			// if (vein.emerge && vein.robots > 0) {
			// System.err.println("WARNING! WARNING! WARNING!");
			// vein.attack(vein.nearFriends().get(0).vein, vein.robots, false);
			// }
		}
	}
}

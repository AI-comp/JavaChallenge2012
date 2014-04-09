package net.javachallenge.players.guests;

import static java.lang.Math.abs;
import static java.lang.Math.exp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.javachallenge.api.ComputerPlayer;
import net.javachallenge.api.Field;
import net.javachallenge.api.Game;
import net.javachallenge.api.Material;
import net.javachallenge.api.Player;
import net.javachallenge.api.Squad;
import net.javachallenge.api.TrianglePoint;
import net.javachallenge.api.Vein;
import net.javachallenge.api.command.Command;
import net.javachallenge.api.command.Commands;

public class Oyososan extends ComputerPlayer {

	public static class Constant {
		public static final int[] WEIGHTS = { 50, 10, 646, 20, 480, 200, 5, 4,
				5, 4, 8, 200, 5, 3, 4, 3, 50, 4, 1000, 1600, 12, 7, 1000, 5,
				14, 10, };
	}

	private static final int LAUNCH_ROBOT_BEHIND_NUMBER_RATIO = Constant.WEIGHTS[0];
	private static final int LAUNCH_ROBOT_BEHIND_DISTANCE_THRESHOLD = Constant.WEIGHTS[1];
	private static final int LAUNCH_ROBOT_BEHIND_NUMBER_THRESHOLD = Constant.WEIGHTS[2];
	private static final int LAUNCH_ROBOT_BEHIND_BEGIN_ROUND = Constant.WEIGHTS[3];
	private static final int LAUNCH_ROBOT_ENEMY_ROUND_WEIGHT = Constant.WEIGHTS[4];
	private static final int LAUNCH_ROBOT_ENEMY_NUMBER_WEIGHT = Constant.WEIGHTS[5];
	private static final int LAUNCH_ROBOT_ENEMY_Y_ROUND_WEIGHT = Constant.WEIGHTS[6];
	private static final int LAUNCH_ROBOT_ENEMY_Y_OFFSET = Constant.WEIGHTS[7];
	private static final int LAUNCH_ROBOT_ENEMY_X_ROUND_WEIGHT = Constant.WEIGHTS[8];
	private static final int LAUNCH_ROBOT_ENEMY_X_OFFSET = Constant.WEIGHTS[9];
	private static final int LAUNCH_ROBOT_ENEMY_BEGIN_ROUND = Constant.WEIGHTS[10];
	private static final int LAUNCH_ROBOT_OTHER_ROUND_WEIGHT = Constant.WEIGHTS[11];
	private static final int LAUNCH_ROBOT_OTHER_Y_ROUND_WEIGHT = Constant.WEIGHTS[12];
	private static final int LAUNCH_ROBOT_OTHER_X_ROUND_WEIGHT = Constant.WEIGHTS[13];
	private static final int LAUNCH_ROBOT_OTHER_Y_OFFSET = Constant.WEIGHTS[14];
	private static final int LAUNCH_ROBOT_OTHER_X_OFFSET = Constant.WEIGHTS[15];
	private static final int SELL_MATERIALS_AMOUNT_RATIO = Constant.WEIGHTS[16];
	private static final int SELL_MATERIALS_MIN_MAX_RATIO = Constant.WEIGHTS[17];
	private static final int SELL_MATERIALS_AMOUNT_THRESHOLD = Constant.WEIGHTS[18];
	private static final int SELECT_VEIN_CENTER_SCORE = Constant.WEIGHTS[19];
	private static final int SELECT_VEIN_MY_VEIN_FAVORITE_DISTANCE = Constant.WEIGHTS[20];
	private static final int SELECT_VEIN_MY_VEIN_SCORE = Constant.WEIGHTS[21];
	private static final int SELECT_VEIN_ENEMY_VEIN_SCORE = Constant.WEIGHTS[22];
	private static final int SELECT_VEIN_OTHER_VEIN_SCORE = Constant.WEIGHTS[23];
	private static final int CHOOSE_BY_ROBOT_PRODUCTIVITY = Constant.WEIGHTS[24];
	private static final int CHOOSE_BY_MATERIAL_PRODUCTIVITY = Constant.WEIGHTS[25];

	public static class PointComparator implements Comparator<TrianglePoint> {
		public final int compare(TrianglePoint left, TrianglePoint right) {
			if (left.getX() == right.getX())
				return left.getX() - right.getX();
			return left.getY() - right.getY();
		}
	}

	@Override
	public String getName() {
		return "Oyososan";
	}

	@Override
	public TrianglePoint selectVein(Game game) {
		Vein bestVein = null;
		double bestScore = Double.NEGATIVE_INFINITY;

		for (Vein vein : game.getField().getVeins()) {
			if (vein.getOwnerId() != -1) {
				continue;
			}

			double score = 0.0;
			for (Vein otherVein : game.getField().getVeins()) {
				if (vein.equals(otherVein)) {
					continue;
				}

				if (otherVein.getOwnerId() == -1) {
					// Other vein.
					score += SELECT_VEIN_OTHER_VEIN_SCORE
							* exp(-vein.getDistance(otherVein));
				} else if (otherVein.getOwnerId() != game.getMyPlayer().getId()) {
					// Enemy vein.
					score -= SELECT_VEIN_ENEMY_VEIN_SCORE
							* exp(-vein.getDistance(otherVein));
				} else {
					// My vein.
					score += SELECT_VEIN_MY_VEIN_SCORE
							* exp(-abs(SELECT_VEIN_MY_VEIN_FAVORITE_DISTANCE
									- vein.getDistance(otherVein)));
				}
			}

			// Distance from the center.
			score -= SELECT_VEIN_CENTER_SCORE
					* exp(-abs(vein.getLocation().getX())
							- abs(vein.getLocation().getY()));

			// Productivity
			score += vein.getInitialRobotProductivity()
					* CHOOSE_BY_ROBOT_PRODUCTIVITY / 100;
			score += vein.getInitialMaterialProductivity()
					* CHOOSE_BY_MATERIAL_PRODUCTIVITY / 100;

			if (bestScore < score) {
				bestScore = score;
				bestVein = vein;
			}
		}

		return bestVein.getLocation();
	}

	@Override
	public List<Command> selectActions(final Game game) {
		Player myPlayer = game.getMyPlayer();

		List<Command> commands = new ArrayList<Command>();
		this.saveTemporalCommands(commands);

		List<Vein> myVeins = new ArrayList<Vein>();
		List<Vein> enemyVeins = new ArrayList<Vein>();
		List<Vein> otherVeins = new ArrayList<Vein>();

		for (Vein vein : game.getField().getVeins()) {
			if (vein.getOwnerId() == myPlayer.getId()) {
				myVeins.add(vein);
			} else if (vein.getOwnerId() != -1) {
				enemyVeins.add(vein);
			} else {
				otherVeins.add(vein);
			}
		}
		Collections.sort(myVeins, new Comparator<Vein>() {
			@Override
			public int compare(Vein o1, Vein o2) {
				Field field = game.getField();
				return -(o1.getDistance(field
						.getVeinsOfOtherOwnersOrderedByDistance(o1).get(0)) - o2
						.getDistance(field
								.getVeinsOfOtherOwnersOrderedByDistance(o2)
								.get(0)));
			}
		});

		Map<Material, AtomicInteger> materials = new HashMap<Material, AtomicInteger>();
		int myMoney = myPlayer.getMoney();

		Material minMaterial = Material.Gas;
		Material maxMaterial = Material.Gas;
		for (Material material : Material.values()) {
			int amount = myPlayer.getMaterial(material);
			materials.put(material, new AtomicInteger(amount));

			if (myPlayer.getMaterial(minMaterial) > myPlayer
					.getMaterial(material)) {
				minMaterial = material;
			}
			if (myPlayer.getMaterial(maxMaterial) < myPlayer
					.getMaterial(material)) {
				maxMaterial = material;
			}
		}

		// Sell materials.
		if (materials.get(maxMaterial).get() > SELL_MATERIALS_AMOUNT_THRESHOLD
				&& materials.get(minMaterial).get()
						* SELL_MATERIALS_MIN_MAX_RATIO < materials.get(
						maxMaterial).get()) {
			int price = game.getAlienTrade().getSellPriceOf(maxMaterial);
			int sellAmount = materials.get(maxMaterial).get()
					* SELL_MATERIALS_AMOUNT_RATIO / 100;
			commands.add(Commands.sellToAlienTrade(maxMaterial, sellAmount));
			materials.get(maxMaterial).addAndGet(-sellAmount);
			myMoney += price * sellAmount;
		}

		// Buy materials.
		int price = game.getAlienTrade().getBuyPriceOf(minMaterial);
		int buyAmount = myMoney / price;
		if (buyAmount > 0) {
			commands.add(Commands.buyFromAlienTrade(minMaterial, buyAmount));
			materials.get(minMaterial).addAndGet(buyAmount);
		}

		this.saveTemporalCommands(commands);

		// Upgrade robots.
		{
			final int[] gasCost = { 0, 200, 0, Integer.MAX_VALUE };
			final int[] stoneCost = { 0, 0, 300, Integer.MAX_VALUE };
			final int[] metalCost = { 0, 200, 500, Integer.MAX_VALUE };

			Vein toUpdate = null;
			int distMin = Integer.MAX_VALUE;
			for (Vein vein : myVeins) {
				int rank = vein.getRobotRank();
				if (materials.get(Material.Gas).get() < gasCost[rank]
						|| materials.get(Material.Metal).get() < metalCost[rank]
						|| materials.get(Material.Stone).get() < stoneCost[rank]) {
					continue;
				}
				List<Vein> enemies = game.getField()
						.getVeinsOfOtherOwnersOrderedByDistance(vein);
				int dist = enemies.get(0).getDistance(vein);

				if (dist < distMin && dist > 3) {
					toUpdate = vein;
					distMin = dist;
				}
			}
			if (toUpdate != null) {
				int rank = toUpdate.getRobotRank();
				commands.add(Commands.upgradeRobot(toUpdate));
				materials.get(Material.Gas).addAndGet(-gasCost[rank]);
				materials.get(Material.Metal).addAndGet(-metalCost[rank]);
				materials.get(Material.Stone).addAndGet(-stoneCost[rank]);
			}
		}
		this.saveTemporalCommands(commands);

		// Upgrade materials.
		{
			final int[] gasCost = { 0, 200, 100, Integer.MAX_VALUE };
			final int[] stoneCost = { 0, 100, 300, Integer.MAX_VALUE };
			Vein toUpdate = null;
			int distMin = Integer.MAX_VALUE;
			for (Vein vein : myVeins) {
				int rank = vein.getMaterialRank();
				if (materials.get(Material.Gas).get() < gasCost[rank]
						|| materials.get(Material.Stone).get() < stoneCost[rank]) {
					continue;
				}
				List<Vein> enemies = game.getField()
						.getVeinsOfOtherOwnersOrderedByDistance(vein);
				int dist = enemies.get(0).getDistance(vein);
				if (dist < distMin && dist > 3) {
					toUpdate = vein;
					distMin = dist;
				}
			}
			if (toUpdate != null) {
				int rank = toUpdate.getMaterialRank();
				commands.add(Commands.upgradeMaterial(toUpdate));
				materials.get(Material.Gas).addAndGet(-gasCost[rank]);
				materials.get(Material.Stone).addAndGet(-stoneCost[rank]);
			}
		}
		this.saveTemporalCommands(commands);

		Map<Vein, AtomicInteger> numberOfRobots = new HashMap<Vein, AtomicInteger>();
		for (Vein myVein : myVeins) {
			numberOfRobots.put(myVein,
					new AtomicInteger(myVein.getNumberOfRobots()));
		}

		// Launch robots to other veins.
		Collections.shuffle(myVeins);
		for (final Vein myVein : myVeins) {
			Collections.sort(otherVeins, new Comparator<Vein>() {
				@Override
				public int compare(Vein o1, Vein o2) {
					return myVein.getDistance(o1) - myVein.getDistance(o2);
				}
			});

			for (Vein otherVein : otherVeins) {
				if (abs(myVein.getLocation().getX()
						- otherVein.getLocation().getX()) < LAUNCH_ROBOT_OTHER_X_OFFSET
						+ game.getRound()
						* LAUNCH_ROBOT_OTHER_X_ROUND_WEIGHT
						/ 100
						&& abs(myVein.getLocation().getY()
								- otherVein.getLocation().getY()) < LAUNCH_ROBOT_OTHER_Y_OFFSET
								+ game.getRound()
								* LAUNCH_ROBOT_OTHER_Y_ROUND_WEIGHT / 100) {
					if (numberOfRobots.get(myVein).get() > otherVein
							.getNumberOfRobots()
							+ game.getRound()
							* LAUNCH_ROBOT_OTHER_ROUND_WEIGHT / 100) {
						int robots = otherVein.getNumberOfRobots() + 1;
						commands.add(Commands.launch(robots,
								myVein.getLocation(), otherVein.getLocation()));
						numberOfRobots.get(myVein).addAndGet(-robots);
					}
				}
			}
		}
		this.saveTemporalCommands(commands);

		// Launch robots to enemy veins.
		Collections.shuffle(myVeins);
		if (game.getRound() > LAUNCH_ROBOT_ENEMY_BEGIN_ROUND) {
			for (Vein myVein : myVeins) {
				for (Vein enemyVein : game.getField()
						.getVeinsOfOtherOwnersOrderedByDistance(myVein)) {
					if (abs(myVein.getLocation().getX()
							- enemyVein.getLocation().getX()) < LAUNCH_ROBOT_ENEMY_X_OFFSET
							+ game.getRound()
							* LAUNCH_ROBOT_ENEMY_X_ROUND_WEIGHT / 100
							&& abs(myVein.getLocation().getY()
									- enemyVein.getLocation().getY()) < LAUNCH_ROBOT_ENEMY_Y_OFFSET
									+ game.getRound()
									* LAUNCH_ROBOT_ENEMY_Y_ROUND_WEIGHT / 100) {
						if (numberOfRobots.get(myVein).get() > enemyVein
								.getNumberOfRobots()
								* LAUNCH_ROBOT_ENEMY_NUMBER_WEIGHT
								/ 100
								- game.getRound()
								* LAUNCH_ROBOT_ENEMY_ROUND_WEIGHT / 100
								&& numberOfRobots.get(myVein).get() > enemyVein
										.getNumberOfRobots()
										+ enemyVein
												.getCurrentRobotProductivity()
										* myVein.getDistance(enemyVein) + 1) {
							int robots = enemyVein.getNumberOfRobots()
									+ enemyVein.getCurrentRobotProductivity()
									* myVein.getDistance(enemyVein) + 1;
							commands.add(Commands.launch(robots,
									myVein.getLocation(),
									enemyVein.getLocation()));
							numberOfRobots.get(myVein).addAndGet(-robots);
						}
					}
				}
			}
		}
		this.saveTemporalCommands(commands);

		// Launch robots from behind to front enemy veins.
		Collections.shuffle(myVeins);
		if (game.getRound() > LAUNCH_ROBOT_BEHIND_BEGIN_ROUND) {
			for (Vein myVein : myVeins) {
				if (numberOfRobots.get(myVein).get() < LAUNCH_ROBOT_BEHIND_NUMBER_THRESHOLD) {
					continue;
				}

				for (Vein enemyVein : game.getField()
						.getVeinsOfOtherOwnersOrderedByDistance(myVein)) {
					if (myVein.getDistance(enemyVein) < LAUNCH_ROBOT_BEHIND_DISTANCE_THRESHOLD) {
						continue;
					}

					int robots = numberOfRobots.get(myVein).get()
							* LAUNCH_ROBOT_BEHIND_NUMBER_RATIO / 100;
					commands.add(Commands.launch(robots, myVein.getLocation(),
							enemyVein.getLocation()));
					numberOfRobots.get(myVein).addAndGet(-robots);
					break;
				}
			}
		}
		this.saveTemporalCommands(commands);

		// TODO(tzik): Launch robot to veins under attacked to protect them.
		// calcInvasionList(myPlayer.getId(), game.getField().getSquads());

		return commands;
	}

	private static final class Invasion {
		public TrianglePoint destination;
		public int delay;
		public int number;
	}

	List<Invasion> calcInvasionList(int myId, List<Squad> squads) {
		List<Invasion> invasions = new ArrayList<Invasion>();
		for (Squad squad : squads) {
			Invasion invasion = new Invasion();
			invasion.destination = squad.getDestinationLocation();
			invasion.delay = squad.getPath().size() - 1;
			invasion.number = squad.getRobot();

			if (squad.getOwnerId() == myId)
				invasion.number *= -1;
			invasions.add(invasion);
		}
		return invasions;
	}
}

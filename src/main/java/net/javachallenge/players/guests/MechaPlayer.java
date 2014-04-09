package net.javachallenge.players.guests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.javachallenge.api.ComputerPlayer;
import net.javachallenge.api.Field;
import net.javachallenge.api.Game;
import net.javachallenge.api.Make;
import net.javachallenge.api.Material;
import net.javachallenge.api.Player;
import net.javachallenge.api.TrianglePoint;
import net.javachallenge.api.Vein;
import net.javachallenge.api.command.Command;
import net.javachallenge.api.command.Commands;

public class MechaPlayer extends ComputerPlayer {
  Game game = null;
  List<Vein> enemyVeins = null;
  List<Vein> emptyVeins = null;
  
  private void update(Game game){
	this.game = game;
	this.enemyVeins = new ArrayList<Vein>();
	this.emptyVeins = new ArrayList<Vein>();
	List<Vein> allVeins = game.getField().getVeins();
    for (Vein vein : allVeins) {
    	if(vein.getOwnerId() == game.getNeutralPlayerId())
    		emptyVeins.add(vein);
    	else if(vein.getOwnerId() != game.getMyPlayer().getId())
    		enemyVeins.add(vein);
    }
  }
  
  private Integer getInitialScore(Vein vein){
	  Integer score = 0;
	  for (Vein evein : enemyVeins){
    	  score += (vein.getDistance(evein))*(vein.getDistance(evein)) * 100;
	  }
	  for (Vein evein : emptyVeins){
		  score -= (vein.getDistance(evein))*(vein.getDistance(evein)) * 5;
	  }
	  score += vein.getLocation().getDistance(Make.point(0, 0)) * 100000;
	  return score;
  }
  
  @Override
  public String getName() {
    return "mecha_g3";
  }

  @Override
  public TrianglePoint selectVein(Game game) {
	update(game);
    this.saveTemporalVeinLocation(Make.point(0, 0));
    Vein selectVein = emptyVeins.get(0);
    Integer maxScore = Integer.MIN_VALUE;
    for (Vein vein : emptyVeins) {
      Integer score = getInitialScore(vein);
      if(maxScore < score){
    	  maxScore = score;
    	  selectVein = vein;
      }
    }
    return selectVein.getLocation();
  }

  @Override
  public List<Command> selectActions(Game game) {
	update(game);
    List<Command> commands = new ArrayList<Command>();

    Field field = game.getField();
    List<Vein> myVeinList = field.getVeins(game.getMyPlayer().getId());
    
    List<Vein> copiedVeinList = new ArrayList<Vein>();
    for (Vein vein : enemyVeins) {
      copiedVeinList.add(vein);
    }
    for (Vein vein : emptyVeins) {
      copiedVeinList.add(vein);
    }

    // Launch
    for (Vein myVein : myVeinList) {
      Collections.sort(copiedVeinList, new VeinDistanceComparator(myVein));
      Vein to = copiedVeinList.get(0);
      int diff = myVein.getNumberOfRobots() - to.getNumberOfRobots();
      if ( (to.getOwnerId() == game.getNeutralPlayerId() && (diff>1)) ||
    	   (to.getOwnerId() != game.getNeutralPlayerId() && (diff>30)) ){
        commands.add(Commands.launch(myVein.getNumberOfRobots()/2,
        		                     myVein.getLocation(),
        		                     to.getLocation()));
      }
    }

    // Upgrade
    for(Vein myVein : myVeinList){
      commands.add(Commands.upgradeRobot(myVein));
      commands.add(Commands.upgradeMaterial(myVein));
    }
    
    // Trade
    for (Material material : Material.values()) {
      int amount = game.getMyPlayer().getMaterial(material);
      if (amount > 1000) {
        commands.add(Commands.sellToAlienTrade(material, amount - 500));
      }
      if (amount < 500) {
        commands.add(Commands.buyFromAlienTrade(material, 500));
      }
    }
    return commands;
  }

  class VeinDistanceComparator implements Comparator<Vein> {
    Vein myVein;

    VeinDistanceComparator(Vein myVein) {
      this.myVein = myVein;
    }

    @Override
    public int compare(Vein o1, Vein o2) {

      if (o1.equals(myVein)) return 1;
      if (o2.equals(myVein)) return -1;

      if (o1.getOwnerId() == myVein.getOwnerId()) return 1;
      if (o2.getOwnerId() == myVein.getOwnerId()) return -1;
      return myVein.getDistance(o1) - myVein.getDistance(o2);
    }
  }
}

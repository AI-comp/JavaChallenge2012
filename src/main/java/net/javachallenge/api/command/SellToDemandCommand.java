package net.javachallenge.api.command;

import net.javachallenge.api.PlayerTrade;

import com.google.common.base.Preconditions;

/**
 * The {@link SellToDemandCommand} class represents a command to make an offer to other players.
 */
class SellToDemandCommand implements Command {

  private PlayerTrade trade;
  private int amount;

  /**
   * Constructs a {@link SellToDemandCommand} with the demand to respond to and the amount to sell.
   * 
   * @param trade the demand to respond to
   * @param amount the amount of material to sell. Should be less or equal to the amount of the
   *        demand.
   */
  SellToDemandCommand(PlayerTrade trade, int amount) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(trade);
    this.trade = trade;
    this.amount = amount;
  }

  @Override
  public String toString() {
    return String.format("sell %d %s %d", trade.getPlayerId(), trade.getMaterial(), amount);
  }
}

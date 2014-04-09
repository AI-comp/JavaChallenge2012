package net.javachallenge.api.command;

import net.javachallenge.api.PlayerTrade;

import com.google.common.base.Preconditions;

/**
 * The {@link BuyFromOfferCommand} class represents a command to buy the given material from another
 * player.
 */
class BuyFromOfferCommand implements Command {

  private PlayerTrade trade;
  private int amount;

  /**
   * Constructs a {@link BuyFromOfferCommand} with the given trade and the amount of material to buy
   * from it.
   * 
   * @param trade the offer to buy from
   * @param amount the amount of material to buy
   */
  BuyFromOfferCommand(PlayerTrade trade, int amount) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(trade);
    this.trade = trade;
    this.amount = amount;
  }

  @Override
  public String toString() {
    return String.format("buy %d %s %d", trade.getPlayerId(), trade.getMaterial(), amount);
  }
}

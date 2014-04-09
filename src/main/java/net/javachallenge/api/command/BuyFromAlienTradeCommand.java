package net.javachallenge.api.command;

import net.javachallenge.api.Material;

import com.google.common.base.Preconditions;

/**
 * The {@link BuyFromAlienTradeCommand} represents a command to buy the given material from aliens.
 */
class BuyFromAlienTradeCommand implements Command {
  private Material material;
  private int amount;

  /**
   * Constructs a {@link BuyFromAlienTradeCommand} with the given material and amount of it.
   * 
   * @param material the material to buy
   * @param amount the amount of material to buy
   */
  BuyFromAlienTradeCommand(Material material, int amount) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(material);
    this.material = material;
    this.amount = amount;
  }

  @Override
  public String toString() {
    return String.format("bank buy %s %d", material, amount);
  }
}

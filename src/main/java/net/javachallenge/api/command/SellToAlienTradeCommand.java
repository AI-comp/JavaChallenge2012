package net.javachallenge.api.command;

import net.javachallenge.api.Material;

import com.google.common.base.Preconditions;

/**
 * The {@link SellToAlienTradeCommand} class represents a command to sell the given material to
 * aliens.
 */
class SellToAlienTradeCommand implements Command {

  private Material material;
  private int amount;

  /**
   * Constructs a {@link SellToAlienTradeCommand} with the given material and amount of it.
   * 
   * @param material the material to sell to the aliens
   * @param amount the amount of material to sell
   */
  SellToAlienTradeCommand(Material material, int amount) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(material);
    this.material = material;
    this.amount = amount;
  }

  @Override
  public String toString() {
    return String.format("bank sell %s %d", material, amount);
  }
}

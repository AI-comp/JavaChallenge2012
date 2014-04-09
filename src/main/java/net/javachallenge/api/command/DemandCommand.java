package net.javachallenge.api.command;

import net.javachallenge.api.Material;

import com.google.common.base.Preconditions;

/**
 * The {@link DemandCommand} class represents a command to create a demand to buy material from
 * other players.
 */
class DemandCommand implements Command {

  private Material material;
  private int amount;
  private int pricePerSingleItem;

  /**
   * Constructs a {@link DemandCommand} with the material and amount to buy and the price for a unit
   * of this material.
   * 
   * @param material the material to buy
   * @param amount the amount of material to buy
   * @param pricePerSingleitem the price of a unit of the material to buy
   */
  DemandCommand(Material material, int amount, int pricePerSingleItem) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(material);
    this.material = material;
    this.amount = amount;
    this.pricePerSingleItem = pricePerSingleItem;
  }

  @Override
  public String toString() {
    return String.format("demand %s %d %d", material, amount, pricePerSingleItem);
  }
}

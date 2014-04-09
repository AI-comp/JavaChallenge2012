package net.javachallenge.api.command;

import net.javachallenge.api.Material;

import com.google.common.base.Preconditions;

/**
 * The {@link OfferCommand} class represents a command to create an offer to sell material to other
 * players.
 */
class OfferCommand implements Command {

  private Material material;
  private int amount;
  private int pricePerSingleItem;

  /**
   * Constructs an {@link OfferCommand} with the given material and amount to sell and the price for
   * a unit of this material.
   * 
   * @param material the material to sell
   * @param amount the amount of material to sell
   * @param pricePerSingleitem the price of a unit of the material to sell
   * 
   */
  OfferCommand(Material material, int amount, int pricePerSingleItem) {
    // Check toString method works well with no exception.
    Preconditions.checkNotNull(material);
    this.material = material;
    this.amount = amount;
    this.pricePerSingleItem = pricePerSingleItem;
  }

  @Override
  public String toString() {
    return String.format("offer %s %d %d", material, amount, pricePerSingleItem);
  }
}

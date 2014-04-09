package net.javachallenge.entity

import net.javachallenge.util.internationalization.I18n

/**
 * Helpers and defaults values for material class.
 */
object Material {
  case class UnknownMaterialException extends Exception

  val all = List(Gas, Stone, Metal)

  def apply(name: String) = name.toLowerCase() match {
    case "gas" => Gas
    case "stone" => Stone
    case "metal" => Metal
    case _ => throw new UnknownMaterialException
  }

  implicit def apply(material: net.javachallenge.api.Material): Material = material match {
    case net.javachallenge.api.Material.Gas => Gas
    case net.javachallenge.api.Material.Stone => Stone
    case net.javachallenge.api.Material.Metal => Metal
    case _ => throw new UnknownMaterialException
  }

  implicit def toJava(material: Material) = material.toJava

  def unapply(name: String) = name.toLowerCase() match {
    case "gas" => Some(Gas)
    case "stone" => Some(Stone)
    case "metal" => Some(Metal)
    case _ => None
  }
}

/**
 * A material that can be found in veins.
 */
abstract sealed class Material {
  /**
   * The name of the material.
   */
  val name: String

  protected def toJava(): net.javachallenge.api.Material

  override def toString: String = name
}

/**
 * A case object representing a 'gas'.
 */
@SerialVersionUID(0l)
case object Gas extends Material {
  /**
   * {@inheritDoc}
   */
  val name: String = I18n("gas")

  override def toJava() = net.javachallenge.api.Material.Gas
}

/**
 * A case object representing a 'stone'.
 */
@SerialVersionUID(0l)
case object Stone extends Material {
  /**
   * {@inheritDoc}
   */
  val name: String = I18n("stone")

  override def toJava() = net.javachallenge.api.Material.Stone
}

/**
 * A case object representing a 'metal'.
 */
@SerialVersionUID(0l)
case object Metal extends Material {
  /**
   * {@inheritDoc}
   */
  val name: String = I18n("metal")

  override def toJava() = net.javachallenge.api.Material.Metal
}
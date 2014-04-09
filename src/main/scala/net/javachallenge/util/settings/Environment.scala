/**
 * @author Daniel Perez
 */

package net.javachallenge.util.settings

/**
 * Parent class to represent an environment
 */
abstract class Environment {
  /**
   * The folder of the sources for the given environment
   */
  val folder: String
}

/**
 * The production environment
 */
case object Production extends Environment {
  /**
   * {@inheritDoc}
   */
  val folder = "main"
}

/**
 * The development/debug environment
 */
case object Development extends Environment {
  /**
   * {@inheritDoc}
   */
  val folder = "main"
}

/**
 * The test environment
 */
case object Test extends Environment {
  /**
   * {@inheritDoc}
   */
  val folder = "test"
}

/**
 * Companion object with current environment and its setter
 */
object Environment {
  var current: Environment = Defaults.ENV

  /**
   * Set the current environment
   * @param env the environment to set
   */
  def setEnvironment(env: Environment) = (current = env)
}
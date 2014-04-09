package net.javachallenge

import net.javachallenge.api.UserInterfaceMode
import net.javachallenge.api.PlayModeBuilder
import net.javachallenge.api.PlayMode

object PlayModeHelper {
  def build(b: PlayModeBuilder) = new PlayMode(b.getFps, b.getAvailableVeinSelectMilliseconds(), b.getAvailableTurnMilliseconds(), b.getUserInterfaceMode, b.isIgnoringExceptions)
  val defaultInstance = new PlayMode(10, 10000, 1000, UserInterfaceMode.SmallGraphical, true)
}
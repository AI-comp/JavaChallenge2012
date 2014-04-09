package net.javachallenge.scene

import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.GameEnvironment
import net.javachallenge.util.misc.ImageLoader

trait TitleScene extends Scene[GameEnvironment] {
  override def draw() = {
    val renderer = getRenderer()
    ImageLoader.prefetch(renderer)
    val title = ImageLoader.loadTitle(renderer)
    renderer.drawImage(title, 0, 0)
  }
}
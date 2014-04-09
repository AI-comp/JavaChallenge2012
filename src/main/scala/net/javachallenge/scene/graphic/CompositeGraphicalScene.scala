package net.javachallenge.scene.graphic

import jp.ac.waseda.cs.washi.gameaiarena.gui.Scene
import net.javachallenge.GameEnvironment
import net.javachallenge.util.misc.ImageLoader

trait CompositeGraphicalScene extends Scene[GameEnvironment] {
  var sceneList: List[Scene[GameEnvironment]] = List();  
  override def draw() = {
    for (scene <- sceneList) {
      scene.draw()
    }
  }
  
  def addScene(scene: Scene[GameEnvironment]) = {
    sceneList = scene :: sceneList
  }
}
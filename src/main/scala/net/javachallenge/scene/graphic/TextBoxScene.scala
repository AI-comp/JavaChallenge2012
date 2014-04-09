package net.javachallenge.scene.graphic

import net.javachallenge.scene.CommandBaseScene
import scala.collection.mutable.Queue

trait TextBoxScene extends CommandBaseScene {
  override def nextCommand = TextBoxScene.nextCommand

  override def displayCore(text: String) = TextBoxScene.display(text)
}

object TextBoxScene {
  var commands = Queue[String]()

  var display: (String => Unit) = null

  def addCommand(command: String) = commands.enqueue(command)

  def nextCommand = {
    if (commands.size > 0)
      Some(commands.dequeue.split(" ").filter(_.length > 0).toList)
    else
      None
  }
}
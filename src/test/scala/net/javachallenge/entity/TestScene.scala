package net.javachallenge.entity

import scala.collection.mutable
import net.javachallenge.scene.CommandBaseScene

trait TestScene extends CommandBaseScene {
  def nextCommand = TestScene.pop

  def displayCore(text: String) = print(text)
}

object TestScene {
  private val commands: mutable.Queue[List[String]] = mutable.Queue()
  def pop() = Some(commands.dequeue)
  def push(command: List[String]) = commands.enqueue(command)
  def push(command: String*) = commands.enqueue(command.toList)
}

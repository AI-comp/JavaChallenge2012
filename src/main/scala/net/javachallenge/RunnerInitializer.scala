package net.javachallenge

import java.io.ObjectOutputStream
import java.io.FileOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import org.apache.commons.lang.StringUtils
import jp.ac.waseda.cs.washi.gameaiarena.io.InputStreams
import java.io.BufferedInputStream
import java.io.ObjectInputStream
import net.javachallenge.runner.InitialRunner
import net.javachallenge.api.MockPlayer
import net.javachallenge.runner.InitialRunner
import net.javachallenge.runner.MainRunner
import java.util.Calendar
import net.javachallenge.api.ComputerPlayer
import net.javachallenge.entity.GameSetting
import java.util.Random
import net.javachallenge.api.PlayMode
import net.javachallenge.util.misc.DateUtils
import java.io.File

object RunnerInitializer {

  def initialize(comPlayers: Array[ComputerPlayer], setting: GameSetting, playMode: PlayMode) = {
    val names = comPlayers.map(_.getName.map(c => if (Character.isJavaIdentifierPart(c)) c else '_'))
    val fileName = DateUtils.dateStringForFileName + "__" + names.mkString("_") + ".rep"
    val rand = new Random()
    new File("replay").mkdir()
    val oos = openObjectOutputStream("replay/" + fileName, comPlayers.map(_.getName), setting, rand)

    var initialRunners = comPlayers.map(cmp => new InitialRunner(cmp))
      .map(r => if (playMode.isIgnoringExceptions()) r.ignoringException() else r)
      .map(r => r.limittingTime(playMode.getAvailableVeinSelectMilliseconds()))
      .map(r => r.recordingStream(oos))
    var mainRunners = comPlayers.map(cmp => (new MainRunner(cmp)))
      .map(r => if (playMode.isIgnoringExceptions()) r.ignoringException() else r)
      .map(r => r.limittingTime(playMode.getAvailableTurnMilliseconds()))
      .map(r => r.recordingStream(oos))
    (initialRunners, mainRunners, rand)
  }

  def initializeReplay(filePath: String) = {
    val stream = InputStreams.openFileOrResource(filePath);
    if (stream == null) {
      throw new IOException("Cant open the replay file:\n" + filePath);
    }
    var version = 0;
    if (stream.read() == 'V') {
      version = stream.read() - '0';
    }
    val bis = new BufferedInputStream(stream);
    val ois = new ObjectInputStream(bis);
    val (names, setting, rand) = version match {
      case 0 => {
        val names = ois.readObject().asInstanceOf[Seq[String]]
        val setting = ois.readObject().asInstanceOf[GameSetting]
        val rand = ois.readObject().asInstanceOf[Random]
        (names, setting, rand)
      }
      case _ =>
        throw new IOException("Unsupported replay file.")
    }
    val initialRunners = names.map(name => (new InitialRunner(new MockPlayer(name))).replayingStream(ois)).toSeq
    val mainRunners = names.map(name => (new MainRunner(new MockPlayer(name))).replayingStream(ois)).toSeq
    (initialRunners, mainRunners, names, setting, rand)
  }

  private def openObjectOutputStream(recordFileName: String, names: Seq[String], setting: GameSetting, rand: Random) = {
    try {
      if (!StringUtils.isEmpty(recordFileName)) {
        val fos = new FileOutputStream(recordFileName);
        fos.write('V');
        fos.write('0');
        val oos = new ObjectOutputStream(fos);
        oos.writeObject(names)
        oos.writeObject(setting)
        oos.writeObject(rand)
        oos
      } else {
        null
      }
    } catch {
      case e: FileNotFoundException =>
        null
      case e: IOException =>
        null
    }
  }
}
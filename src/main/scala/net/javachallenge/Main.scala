package net.javachallenge

import net.javachallenge.util.settings.Defaults
import net.javachallenge.api.PlayMode
import net.javachallenge.api.UserInterfaceMode
import com.google.common.io.Files
import scala.collection.mutable
import java.util.Random
import java.awt._
import java.awt.event._
import javax.swing._
import jp.ac.waseda.cs.washi.gameaiarena.gui.GamePanels
import net.javachallenge.entity._
import net.javachallenge.scene._
import net.javachallenge.util.misc.ImageLoader
import net.javachallenge.scene.graphic._
import net.javachallenge.scene.console._
import net.javachallenge.entity.GameSetting
import net.javachallenge.api.ComputerPlayer
import net.javachallenge.api.MockPlayer
import jp.ac.waseda.cs.washi.gameaiarena.gui._
import jp.ac.waseda.cs.washi.gameaiarena.api._
import jp.ac.waseda.cs.washi.gameaiarena.key._
import jp.ac.waseda.cs.washi.gameaiarena.runner.Runners
import net.javachallenge.printer.Printer
import org.apache.commons.cli._
import net.javachallenge.runner._

object Main {

  val HELP = "h"
  val CUI_MODE = "c"
  val LARGE_MODE = "l"
  var logFunction: String => Unit = null

  def log(text: String) = logFunction(text)

  def printHelp(options: Options) {
    val help = new HelpFormatter();
    help.printHelp(
      "java -jar JavaChallenge2012-X.X.X.jar [OPTIONS]\n"
        + "[OPTIONS]: ", "", options, "", true);
  }

  def main(args: Array[String]) {
    val options = new Options()
      .addOption(HELP, false, "Print this help.")
      .addOption(CUI_MODE, false, "Enable CUI mode instead of GUI mode.")
      .addOption(LARGE_MODE, false, "Enable GUI mode with large images.")
    try {
      val parser = new BasicParser();
      val cl = parser.parse(options, args);
      if (cl.hasOption(HELP)) {
        printHelp(options);
      } else {
        startGame(options, cl);
      }
    } catch {
      case e: ParseException => {
        System.err.println("Error: " + e.getMessage());
        printHelp(options);
        System.exit(-1);
      }
    }
  }

  def startConsoleGame() = {
    val env = GameEnvironment()
    env.getSceneManager().setFps(1000)
    val endScene = new EmptyScene(null) with ResultScene with ConsoleScene
    val mainScene = new MainScene(endScene) with ConsoleScene
    val veinScene = new VeinScene(mainScene) with ConsoleScene
    val playerScene = new PlayerScene(veinScene) with ConsoleScene
    env.start(playerScene)
  }

  def startGame(options: Options, cl: CommandLine) {
    if (cl.hasOption(CUI_MODE)) {
      startConsoleGame()
    } else {
      val (window, env) = initializeComponents(cl.hasOption(LARGE_MODE))
      val setting = GameSetting(mapSize = 10, veinCount = 40)

      val endScene = new WaitingScene(null) with ResultScene with GraphicalScene with TextBoxScene
      val mainScene = new MainScene(endScene) with GraphicalScene with TextBoxScene
      val veinScene = new VeinScene(mainScene) with GraphicalScene with TextBoxScene
      val playerScene = new PlayerScene(veinScene, setting) with GraphicalScene with TextBoxScene

      val f: Function1[String, Unit] = mainScene.displayCore(_)

      env.start(playerScene)

      window.dispose()
    }
  }

  def consoleRunnerScenes(nextScene: Scene[GameEnvironment]) = {
    val mainScene = new MainScene(nextScene) with ConsoleScene with MainRunnerScene
    val veinScene = new VeinScene(mainScene) with ConsoleScene with InitialRunnerScene
    val titleScene = new WaitingScene(veinScene) with ConsoleScene
    logFunction = mainScene.displayCore(_)
    (titleScene, veinScene, mainScene, nextScene)
  }

  def graphicalRunnerScenes(nextScene: Scene[GameEnvironment]) = {
    val mainScene = new MainScene(nextScene) with GraphicalScene with TextBoxScene with MainRunnerScene
    val waitScene = new WaitingScene(mainScene) with GraphicalScene with TextBoxScene
    val veinScene = new VeinScene(waitScene) with GraphicalScene with TextBoxScene with InitialRunnerScene
    val titleScene = new WaitingScene(veinScene) with CompositeGraphicalScene with TextBoxScene
    titleScene.addScene(new EmptyScene(null) with TitleScene with TextBoxScene)
    logFunction = mainScene.displayCore(_)
    (titleScene, veinScene, mainScene, nextScene)
  }

  def initializeWindowAndEnvironment(playMode: PlayMode) =
    playMode.getUserInterfaceMode() match {
      case UserInterfaceMode.SmallGraphical =>
        initializeComponents(false)
      case UserInterfaceMode.LargeGraphical =>
        initializeComponents(true)
      case UserInterfaceMode.CharacterBased =>
        (null, GameEnvironment())
    }

  def startAIGame(comPlayers: Array[ComputerPlayer], apiSetting: net.javachallenge.api.GameSetting, playMode: PlayMode) = {
    val (window, env) = initializeWindowAndEnvironment(playMode)
    val setting = apiSetting.asInstanceOf[GameSetting]
    val random = new Random()

    val ((_, veinScene, mainScene, _), resultScene) =
      if (playMode.getUserInterfaceMode == UserInterfaceMode.CharacterBased) {
        val resultScene_ = new EmptyScene(null) with ResultScene with ConsoleScene
        (consoleRunnerScenes(resultScene_), resultScene_)
      } else {
        val resultScene_ = new WaitingScene(null) with ResultScene with GraphicalScene with TextBoxScene
        (graphicalRunnerScenes(resultScene_), resultScene_)
      }
    val (irs, mrs, rand) = RunnerInitializer.initialize(comPlayers, setting, playMode)
    mainScene.runners = Vector(mrs: _*)
    veinScene.runners = Vector(irs: _*)

    env.game = Game(comPlayers.map(_.getName()), setting, Field(setting, rand))
    env.getSceneManager().setFps(playMode.getFps())
    env.start(veinScene)

    if (window != null) {
      window.dispose()
    }
    resultScene.sortedPlayers
  }

  def startReplayGame(replayFileName: String, playMode: PlayMode) {
    val (window, env) = initializeWindowAndEnvironment(playMode)

    val ((titleScene, veinScene, mainScene, _), resultScene) =
      if (playMode.getUserInterfaceMode == UserInterfaceMode.CharacterBased) {
        val resultScene_ = new EmptyScene(null) with ResultScene with ConsoleScene
        (consoleRunnerScenes(resultScene_), resultScene_)
      } else {
        val resultScene_ = new WaitingScene(null) with ResultScene with TextBoxScene
        val finalScene_ = new WaitingScene(resultScene_) with GraphicalScene with TextBoxScene
        (graphicalRunnerScenes(finalScene_), resultScene_)
      }

    val (irs, mrs, names, setting, rand) = RunnerInitializer.initializeReplay(replayFileName)
    mainScene.runners = Vector(mrs: _*)
    veinScene.runners = Vector(irs: _*)

    env.game = Game(names, setting, Field(setting, rand))
    env.getSceneManager().setFps(playMode.getFps())
    env.start(titleScene)

    if (window != null) {
      window.dispose()
    }
  }

  def initializeComponents(isLargeMode: Boolean) = {
    // TODO: Use scala.swing package instead of javax.swing package

    // Initialize layout components
    val mainPanel = new JPanel()
    val layout = new SpringLayout();
    mainPanel.setLayout(layout)

    // Initialize each component
    val window = new JFrame()
    window.setTitle("JavaChallenge2012")
    val gamePanel = GamePanels.newWithDefaultImage()
    if (isLargeMode) {
      window.setSize(1280, 1000)
      gamePanel.setPreferredSize(new Dimension(1280, 720))
    } else {
      window.setSize(1024, 740)
      gamePanel.setPreferredSize(new Dimension(1024, 495))
    }
    mainPanel.add(gamePanel);
    val logArea = new JTextArea();
    val logScrollPane = new JScrollPane(logArea)
    logScrollPane.setPreferredSize(new Dimension(0, 0))
    mainPanel.add(logScrollPane);
    logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    logArea.setEditable(false)
    val commandField = new JTextField();
    commandField.setPreferredSize(new Dimension(0, 20))
    commandField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    commandField.addActionListener(new ActionListener() {
      def actionPerformed(e: ActionEvent) = {
        val command = commandField.getText()
        commandField.setText("")
        TextBoxScene.addCommand(command)
      }
    })
    TextBoxScene.display = (text) => {
      logArea.append(text)
      logArea.setCaretPosition(logArea.getText().length())
    }
    mainPanel.add(commandField);

    // Layout compo6nents
    layout.putConstraint(SpringLayout.NORTH, gamePanel, 0, SpringLayout.NORTH, mainPanel);
    layout.putConstraint(SpringLayout.NORTH, logScrollPane, 0, SpringLayout.SOUTH, gamePanel);
    layout.putConstraint(SpringLayout.SOUTH, logScrollPane, 0, SpringLayout.NORTH, commandField);
    layout.putConstraint(SpringLayout.SOUTH, commandField, 0, SpringLayout.SOUTH, mainPanel);
    layout.putConstraint(SpringLayout.WEST, logScrollPane, 0, SpringLayout.WEST, mainPanel);
    layout.putConstraint(SpringLayout.WEST, commandField, 0, SpringLayout.WEST, mainPanel);
    layout.putConstraint(SpringLayout.EAST, logScrollPane, 0, SpringLayout.EAST, mainPanel);
    layout.putConstraint(SpringLayout.EAST, commandField, 0, SpringLayout.EAST, mainPanel);

    window.getContentPane().add(mainPanel)
    //window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    // Show the window
    window.setVisible(true);
    gamePanel.initializeAfterShowing()

    val env = GameEnvironment(gamePanel, tileSize = if (isLargeMode) 48 else 32)
    ImageLoader.prefetch(env.getRenderer)
    env.getSceneManager.addWindowListenerForTerminating(window);
    env.getSceneManager().setFps(5)
      .addWindowListenerForTerminating(window)
    initializeListener(gamePanel, env, commandField, logArea)

    commandField.requestFocus()

    (window, env)
  }

  def initializeListener(gamePanel: JPanel, env: GameEnvironment, commandField: JTextField, logArea: JTextArea) {
    gamePanel.addMouseListener(new MouseAdapter() {
      override def mouseClicked(e: MouseEvent) = {
        if (env.game != null) {
          val p = new Point2(e.getPoint())
          val range = 16
          val dx = -4
          val dy = -4
          for (
            (tp, px) <- env.trianglePointToPixelPoint.filter {
              case (tp, px) =>
                px.x + dx <= p.x && p.x <= px.x + dx + range &&
                  px.y + dy <= p.y && p.y <= px.y + dy + range
            }
          ) {
            TextBoxScene.display("Your clicked location is " + tp.cmdStr)
            TextBoxScene.display(Defaults.NEW_LINE)
            if (env.game.field.veins.toMap.contains(tp)) {
              val v = env.game.field.veins(tp)
              TextBoxScene.display("There is a vein: ")
              Printer.vein(tp, v, TextBoxScene.display)
              TextBoxScene.display(Defaults.NEW_LINE)
              for (squad <- env.game.field.squads.filter(s => s.current == tp)) {
                TextBoxScene.display("There is a squad: " + squad)
                TextBoxScene.display(Defaults.NEW_LINE)
              }
            }
            if (e.getButton() == 3) {
              commandField.setText(commandField.getText + " " + tp.cmdStr + " ")
            }
            commandField.requestFocus()
          }
        }
      }
    });
    val memorizer = new AwtKeyMemorizer()
    gamePanel.addKeyListener(memorizer)
    logArea.addKeyListener(memorizer);
    commandField.addKeyListener(memorizer)
    env.getInputer().add(0, memorizer.getKeyPressChecker(KeyEvent.VK_ENTER))
  }
}

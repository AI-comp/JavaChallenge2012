package net.javachallenge.players;

import java.util.ArrayList;
import java.util.Collections;

import net.javachallenge.api.ComputerPlayer;
import net.javachallenge.api.GameSetting;
import net.javachallenge.api.Make;
import net.javachallenge.api.PlayMode;
import net.javachallenge.players.guests.Hasi;
import net.javachallenge.players.others.JoeJack;
import net.javachallenge.players.others.Myu;
import net.javachallenge.players.others.NearPlayer;
import net.javachallenge.players.others.Sabateur;
import net.javachallenge.players.others.Tokoharu;
import net.javachallenge.players.others.Wand;

import com.google.common.collect.Lists;

public class Main {

  public static void main(String[] args) {
    ArrayList<ComputerPlayer> players =
        Lists.newArrayList(new Hasi(), new JoeJack(), new Myu(), new Sabateur(), new Tokoharu(),
            new NearPlayer());
    Collections.shuffle(players);

    // You can customize game setting.
    GameSetting setting = Make.gameSettingsBuilder().build();
    // You can customize play setting which is independent on game rule.
    PlayMode playMode = Make.playModeBuilder().setFps(9999)
    // .setIgnoringExceptions(false) for debugging
    // .setUserInterfaceMode(UserInterfaceMode.CharacterBased)
        .build();

    net.javachallenge.Main.startAIGame(players.toArray(new ComputerPlayer[0]), setting, playMode);
  }
}

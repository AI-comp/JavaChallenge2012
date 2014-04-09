package net.javachallenge.contest;

import java.util.List;

import net.javachallenge.api.Make;
import net.javachallenge.api.PlayMode;

import com.google.common.collect.Lists;

public class GuestQualificationContestReplayer {
  public static void main(String[] args) {
    PlayMode playMode = Make.playModeBuilder().setFps(20).build();
    List<String> fileNames =
        Lists
            .newArrayList(
                "replay-guest-qual/2012_11_18_8_57_0__mecha_g3_Wand_Player_JoeJack_Oyososan_hasi_methane1.rep",
                "replay-guest-qual/2012_11_18_8_57_4__JoeJack_methane1_hasi_Wand_Player_Oyososan_mecha_g3.rep",
                "replay-guest-qual/2012_11_18_8_57_6__Oyososan_Wand_Player_mecha_g3_hasi_JoeJack_methane1.rep",
                "replay-guest-qual/2012_11_18_8_57_9__methane1_Wand_Player_JoeJack_Oyososan_hasi_mecha_g3.rep",
                "replay-guest-qual/2012_11_18_8_57_12__hasi_mecha_g3_Wand_Player_methane1_JoeJack_Oyososan.rep",
                "replay-guest-qual/2012_11_18_8_57_15__methane1_hasi_Wand_Player_Oyososan_mecha_g3_JoeJack.rep",
                "replay-guest-qual/2012_11_18_8_57_17__methane1_JoeJack_Wand_Player_hasi_Oyososan_mecha_g3.rep",
                "replay-guest-qual/2012_11_18_8_57_19__JoeJack_Oyososan_methane1_hasi_mecha_g3_Wand_Player.rep",
                "replay-guest-qual/2012_11_18_8_57_22__methane1_mecha_g3_Wand_Player_JoeJack_Oyososan_hasi.rep",
                "replay-guest-qual/2012_11_18_8_56_56__methane1_JoeJack_Oyososan_hasi_Wand_Player_mecha_g3.rep");

    for (String fileName : fileNames) {
      net.javachallenge.Main.startReplayGame(fileName, playMode);
    }
  }
}

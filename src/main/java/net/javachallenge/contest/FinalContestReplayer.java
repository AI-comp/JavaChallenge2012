package net.javachallenge.contest;

import java.util.List;

import net.javachallenge.api.Make;
import net.javachallenge.api.PlayMode;

import com.google.common.collect.Lists;

public class FinalContestReplayer {
  public static void main(String[] args) {
    PlayMode playMode = Make.playModeBuilder().setFps(20).build();
    List<String> fileNames =
        Lists
            .newArrayList(
                "replay-final/2012_11_18_8_46_36__oshieteZukky_not_shiokawa_Mi_Sawa2012_wakaba_Gunma_s_Ambition___o______o__.rep",
                "replay-final/2012_11_18_8_46_40__not_shiokawa_Gunma_s_Ambition_Mi_Sawa2012_oshieteZukky_wakaba___o______o__.rep",
                "replay-final/2012_11_18_8_46_43__wakaba_not_shiokawa_oshieteZukky_Mi_Sawa2012___o______o___Gunma_s_Ambition.rep",
                "replay-final/2012_11_18_11_25_35____o______o___oshieteZukky.rep");
    for (String fileName : fileNames) {
      net.javachallenge.Main.startReplayGame(fileName, playMode);
    }
  }
}

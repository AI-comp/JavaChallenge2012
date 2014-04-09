package net.javachallenge.contest;

import java.util.List;

import net.javachallenge.api.Make;
import net.javachallenge.api.PlayMode;

import com.google.common.collect.Lists;

public class GuestFinalContestReplayer {
  public static void main(String[] args) {
    PlayMode playMode = Make.playModeBuilder().setFps(20).build();
    List<String> fileNames =
        Lists
            .newArrayList(
                "replay-guest-final/2012_11_18_9_8_46__methane1_not_shiokawa_wakaba_oshieteZukky___o______o___hasi.rep",
                "replay-guest-final/2012_11_18_9_8_51__wakaba_methane1_oshieteZukky_hasi___o______o___not_shiokawa.rep",
                "replay-guest-final/2012_11_18_9_8_53__wakaba___o______o___oshieteZukky_not_shiokawa_methane1_hasi.rep");

    for (String fileName : fileNames) {
      net.javachallenge.Main.startReplayGame(fileName, playMode);
    }
  }
}

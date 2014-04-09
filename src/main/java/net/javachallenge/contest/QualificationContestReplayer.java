package net.javachallenge.contest;

import java.util.List;

import net.javachallenge.api.Make;
import net.javachallenge.api.PlayMode;

import com.google.common.collect.Lists;

public class QualificationContestReplayer {
  public static void main(String[] args) {
    PlayMode playMode = Make.playModeBuilder().setFps(20).build();
    List<String> fileNames =
        Lists
            .newArrayList(
                "replay-qual/2012_11_18_8_19_11__You_And_Java__Download_Freely____w___Gunma_s_Ambition_Sendy_Enumerable_C_hokudai.rep",
                "replay-qual/2012_11_18_8_19_14__mofu_txt_not_shiokawa_GlasgowHaskellPlayer_Chrome_0xFF7_oshieteZukky.rep",
                "replay-qual/2012_11_18_8_19_18_____3__________usagisan_Mi_Sawa2012_Amadeus___d__0w0__b_Hikikomori____Okubyoumono_wakaba.rep",
                "replay-qual/2012_11_18_8_19_19__icp_py_Myu_TeamTakapt_muteki_shogun_ma_bo______w____________.rep",
                "replay-qual/2012_11_18_8_19_21__There_s_more_than_one_WA_to_do_AC__JoeJack_Guan_Wun_CityU_EEngineer_UECoders_zerohachi.rep",
                "replay-qual/2012_11_18_8_19_23__hiyokko_team_Otoshigami_tmt514___o______o___THE_2DM_STER_Mr__Tantan.rep");
    for (String fileName : fileNames) {
      net.javachallenge.Main.startReplayGame(fileName, playMode);
    }
  }
}

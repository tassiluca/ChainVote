package it.unibo.ds.chainvote.presentation;

import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.utils.Utils;

public class SerializerCustomUtils {

    public static String serializeElectionInfo(ElectionInfo info) {
        return "{\"id\":\"" + Utils.calculateID(info) + "\",\"affluence\":\"" + affluence +  "%\"}";
    }

    public static String serializeElectionInfoWithResults(ElectionInfo info, Election object) {
        double affluence = (double) object.getBallots().size() * 100 / (double) info.getVotersNumber();
        return "{\"id\":\"" + Utils.calculateID(info) + "\",\"affluence\":\"" + affluence +  "%\"}";
    }
}

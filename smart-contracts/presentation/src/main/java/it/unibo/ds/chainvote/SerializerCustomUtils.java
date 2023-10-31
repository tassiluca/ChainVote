package it.unibo.ds.chainvote;

import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.utils.Utils;

public class SerializerCustomUtils {
    public static String serializeForAffluence(Election object, ElectionInfo info) {
        double affluence = (double) object.getBallots().size() * 100 / (double) info.getVotersNumber();
        return "{\"id\":\"" + Utils.calculateID(info) + "\",\"affluence\":\"" + affluence +  "%\"}";
    }
}

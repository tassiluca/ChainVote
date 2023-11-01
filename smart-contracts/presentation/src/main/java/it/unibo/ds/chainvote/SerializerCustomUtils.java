package it.unibo.ds.chainvote;

import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.utils.Utils;

/**
 * TODO to document
 */
public final class SerializerCustomUtils {

    private SerializerCustomUtils() { }

    /**
     * TODO to document (now placeholder just to generate javadoc)
     * @param object the election
     * @param info the info
     * @return a string
     */
    public static String serializeForAffluence(final Election object, final ElectionInfo info) {
        double affluence = (double) object.getBallots().size() * 100 / (double) info.getVotersNumber();
        return "{\"id\":\"" + Utils.calculateID(info) + "\",\"affluence\":\"" + affluence +  "%\"}";
    }
}

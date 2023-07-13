package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import it.unibo.ds.core.Voting;
import it.unibo.ds.core.VotingImpl;

/**
 * Utility class for Genson (de)serialization stuffs.
 */
public final class GensonUtils {

    private GensonUtils() { }

    /**
     * @return a new {@link Genson} instance, already configured.
     */
    public static Genson create() {
        return new GensonBuilder()
            .withConverter(new VotingConverter(), VotingImpl.class)
            .withConverter(new VotingConverter(), Voting.class)
            .create();
    }
}

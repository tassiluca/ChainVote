package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import it.unibo.ds.chainvote.elections.Ballot;
import it.unibo.ds.chainvote.elections.BallotImpl;
import it.unibo.ds.chainvote.elections.Election;
import it.unibo.ds.chainvote.elections.ElectionImpl;
import it.unibo.ds.chainvote.elections.ElectionInfo;
import it.unibo.ds.chainvote.elections.ElectionInfoImpl;
import it.unibo.ds.chainvote.codes.OneTimeCode;
import it.unibo.ds.chainvote.codes.OneTimeCodeImpl;
import it.unibo.ds.chainvote.converters.*;
import it.unibo.ds.chainvote.utils.Choice;

/**
 * Utility class for Genson (de)serialization stuffs.
 */
public final class GensonUtils {

    private GensonUtils() { }

    /**
     * @return a new {@link Genson} instance, already configured.
     */
    public static GensonBuilder defaultBuilder() {
        return new GensonBuilder()
            .useRuntimeType(false)
            .withConverter(new OneTimeCodeConverter(), OneTimeCodeImpl.class)
            .withConverter(new OneTimeCodeConverter(), OneTimeCode.class)
            .withConverter(new BallotConverter(), Ballot.class)
            .withConverter(new BallotConverter(), BallotImpl.class)
            .withConverter(new ElectionConverter(), Election.class)
            .withConverter(new ElectionConverter(), ElectionImpl.class)
            .withConverter(new ElectionInfoConverter(), ElectionInfo.class)
            .withConverter(new ElectionInfoConverter(), ElectionInfoImpl.class)
            .withConverter(new ChoiceConverter(), Choice.class);
    }
}

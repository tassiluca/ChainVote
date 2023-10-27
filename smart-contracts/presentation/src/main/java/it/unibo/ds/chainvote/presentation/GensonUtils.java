package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import it.unibo.ds.core.assets.*;
import it.unibo.ds.core.codes.OneTimeCode;
import it.unibo.ds.core.codes.OneTimeCodeImpl;
import it.unibo.ds.core.utils.Choice;

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
            .useRuntimeType(false)
            .useConstructorWithArguments(true)
            .withConverter(new OneTimeCodeConverter(), OneTimeCodeImpl.class)
            .withConverter(new OneTimeCodeConverter(), OneTimeCode.class)
            .withConverter(new BallotConverter(), Ballot.class)
            .withConverter(new BallotConverter(), BallotImpl.class)
            .withConverter(new ElectionConverter(), Election.class)
            .withConverter(new ElectionConverter(), ElectionImpl.class)
            .withConverter(new ElectionInfoConverter(), ElectionInfo.class)
            .withConverter(new ElectionInfoConverter(), ElectionInfoImpl.class)
            .withConverter(new ChoiceConverter(), Choice.class)
            .create();
    }
}

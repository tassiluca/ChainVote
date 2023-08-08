package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import it.unibo.ds.core.codes.OneTimeCode;
import it.unibo.ds.core.codes.OneTimeCodeImpl;

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
            .withConverter(new OneTimeCodeConverter(), OneTimeCodeImpl.class)
            .withConverter(new OneTimeCodeConverter(), OneTimeCode.class)
            .useRuntimeType(true)
            .create();
    }
}

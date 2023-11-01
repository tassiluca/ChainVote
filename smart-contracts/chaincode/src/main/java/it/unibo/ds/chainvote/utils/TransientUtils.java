package it.unibo.ds.chainvote.utils;

import org.hyperledger.fabric.shim.ChaincodeException;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

/**
 * A utility class exposing utilities methods to extract and convert data from the transient map.
 */
public final class TransientUtils {

    private enum Error {
        INCOMPLETE_INPUT,
        WRONG_INPUT
    }

    private TransientUtils() { }

    private static <X> X getFromTransient(
        final Map<String, byte[]> transientData,
        final String key,
        final Function<byte[], X> converter
    ) {
        if (!transientData.containsKey(key) || transientData.get(key).length == 0) {
            final String errorMsg = "An entry with key `" + key + "` was expected in the transient map.";
            throw new ChaincodeException(errorMsg, Error.INCOMPLETE_INPUT.toString());
        }
        return converter.apply(transientData.get(key));
    }

    /**
     * @param transientData the {@link Map} containing the transient data
     * @param key the key of the entry to extract
     * @return the {@link String} associated to the given key
     * @throws ChaincodeException if the requested key is not present in the map
     */
    public static String getStringFromTransient(final Map<String, byte[]> transientData, final String key) {
        return getFromTransient(transientData, key, b -> new String(b, StandardCharsets.UTF_8));
    }

    /**
     * @param transientData the {@link Map} containing the transient data
     * @param key the key of the entry to extract
     * @return the {@link Long} associated to the given key
     * @throws ChaincodeException if the requested key is not present in the map
     * or the associated value cannot be converted to a Long.
     */
    public static Long getLongFromTransient(final Map<String, byte[]> transientData, final String key) {
        try {
            return Long.valueOf(getStringFromTransient(transientData, key));
        } catch (NumberFormatException exception) {
            final String errorMsg = "The `" + key + "`s input was expected to be a Long.";
            throw new ChaincodeException(errorMsg, Error.WRONG_INPUT.toString());
        }
    }
}

package it.unibo.ds.chaincode.utils;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.utils.Choice;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A utility class exposing method to extract and convert data from the transient map.
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
        if (!transientData.containsKey(key)) {
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

    /**
     * @param transientData the {@link Map} containing the transient data.
     * @param key the key of the entry to extract.
     * @return the {@link Choice} associated to the given key.
     */
    public static Choice getChoiceFromTransient(final Map<String, byte[]> transientData, final String key) {
        return getFromTransient(transientData, key, b -> {
            Genson genson = GensonUtils.create();
            return genson.deserialize(getStringFromTransient(transientData, key), Choice.class);
        });
    }

    /**
     * @param transientData the {@link Map} containing the transient data.
     * @param key the key of the entry to extract.
     * @return the {@link Choice} associated to the given key.
     */
    public static LocalDateTime getDateFromTransient(final Map<String, byte[]> transientData, final String key) {
        final Genson genson = GensonUtils.create();
        return getFromTransient(transientData, key, b ->
                genson.deserialize(getStringFromTransient(transientData, key), LocalDateTime.class)
        );
    }

    /**
     * @param transientData the {@link Map} containing the transient data.
     * @param key the key of the entry to extract.
     * @return the {@link Choice} associated to the given key.
     */
    public static List<Choice> getListFromTransient(final Map<String, byte[]> transientData, final String key) {
        final Genson genson = GensonUtils.create();
        return getFromTransient(transientData, key, b ->
            genson.deserialize(getStringFromTransient(transientData, key).replaceAll("\"", "\\\""), new GenericType<>() { })
        );
    }

    /**
     * @param transientData the {@link Map} containing the transient data.
     * @return the {@link Map} of results associated to the given keys.
     */
    public static Map<Choice, Long> getMapOfResultsFromTransient(final Map<String, byte[]> transientData, final String key) {
        final Genson genson = GensonUtils.create();
        return getFromTransient(transientData, key, b ->
            genson.deserialize(getStringFromTransient(transientData, key), new GenericType<Map<Choice, Long>>() { })
        );
    }


    public static <P, R> R applyToTransients(final Context context,
                                             final Function<Map<String, byte[]>, P> build,
                                             final Function<P, R> action) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final P param = build.apply(transientMap);
        return action.apply(param);
    }

    public static <P1, P2, R> R applyToTransients(final Context context,
                                                  final Function<Map<String, byte[]>, P1> buildFirst,
                                                  final Function<Map<String, byte[]>, P2> buildSecond,
                                                  final BiFunction<P1, P2, R> action) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final P1 firstParam = buildFirst.apply(transientMap);
        final P2 secondParam = buildSecond.apply(transientMap);
        return action.apply(firstParam, secondParam);
    }

    @FunctionalInterface
    public interface ThreeParameterFunction<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }


    public static <P1, P2, P3, R> R applyToTransients(final Context context,
                                                              final Function<Map<String, byte[]>, P1> buildFirst,
                                                              final Function<Map<String, byte[]>, P2> buildSecond,
                                                              final Function<Map<String, byte[]>, P3> buildThird,
                                                              final ThreeParameterFunction<P1, P2, P3, R> action) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final P1 firstParam = buildFirst.apply(transientMap);
        final P2 secondParam = buildSecond.apply(transientMap);
        final P3 thirdParam = buildThird.apply(transientMap);
        return action.apply(firstParam, secondParam, thirdParam);
    }

    @FunctionalInterface
    public interface FiveParameterFunction<T1, T2, T3, T4, T5, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
    }


    public static <P1, P2, P3, P4, P5, R> R applyToTransients(final Context context,
                                                  final Function<Map<String, byte[]>, P1> buildFirst,
                                                  final Function<Map<String, byte[]>, P2> buildSecond,
                                                  final Function<Map<String, byte[]>, P3> buildThird,
                                                  final Function<Map<String, byte[]>, P4> buildForth,
                                                  final Function<Map<String, byte[]>, P5> buildFifth,
                                                  final FiveParameterFunction<P1, P2, P3, P4, P5, R> action) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final P1 firstParam = buildFirst.apply(transientMap);
        final P2 secondParam = buildSecond.apply(transientMap);
        final P3 thirdParam = buildThird.apply(transientMap);
        final P4 forthParam = buildForth.apply(transientMap);
        final P5 fifthParam = buildFifth.apply(transientMap);
        return action.apply(firstParam, secondParam, thirdParam, forthParam, fifthParam);
    }
}


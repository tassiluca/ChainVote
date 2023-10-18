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
import java.util.function.BiConsumer;
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

    /**
     * Parses the data from transient.
     * @param transientData the transient map.
     * @param key the data's label used to index it.
     * @param converter a function that build the data given the bytes read from the transient.
     * @return the data parsed from the transient labelled by the key.
     * @param <X> the generic type of the data parsed.
     */
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
     * Parses a string labelled by the key from the transient.
     * @param transientData the {@link Map} containing the transient data.
     * @param key the key of the entry to extract.
     * @return the {@link String} associated to the given key.
     * @throws ChaincodeException if the requested key is not present in the map.
     */
    public static String getStringFromTransient(final Map<String, byte[]> transientData, final String key) {
        return getFromTransient(transientData, key, b -> new String(b, StandardCharsets.UTF_8));
    }

    /**
     * Parses a long labelled by the key from the transient.
     * @param transientData the {@link Map} containing the transient data.
     * @param key the key of the entry to extract.
     * @return the {@link Long} associated to the given key.
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
     * Parses a {@link Choice} labelled by the key from the transient.
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
     * Parses a {@link LocalDateTime} labelled by the key from the transient.
     * @param transientData the {@link Map} containing the transient data.
     * @param key the key of the entry to extract.
     * @return the {@link LocalDateTime} associated to the given key.
     */
    public static LocalDateTime getDateFromTransient(final Map<String, byte[]> transientData, final String key) {
        final Genson genson = GensonUtils.create();
        return getFromTransient(transientData, key, b ->
                genson.deserialize(getStringFromTransient(transientData, key), LocalDateTime.class)
        );
    }

    /**
     * Parses a {@link List} of {@link Choice}s labelled by the key from the transient.
     * @param transientData the {@link Map} containing the transient data.
     * @param key the key of the entry to extract.
     * @return the {@link List} of {@link Choice}s associated to the given key.
     */
    public static List<Choice> getListFromTransient(final Map<String, byte[]> transientData, final String key) {
        final Genson genson = GensonUtils.create();
        return getFromTransient(transientData, key, b ->
            genson.deserialize(getStringFromTransient(transientData, key).replaceAll("\"", "\\\""), new GenericType<>() { })
        );
    }

    /**
     * Parses a {@link Map} of {@link Choice}s and {@link Long} labelled by the key from the transient.
     * @param transientData the {@link Map} containing the transient data.
     * @param key the key of the entry to extract.
     * @return the {@link Map} of results associated to the given keys.
     */
    public static Map<Choice, Long> getMapOfResultsFromTransient(final Map<String, byte[]> transientData, final String key) {
        final Genson genson = GensonUtils.create();
        return getFromTransient(transientData, key, b ->
            genson.deserialize(getStringFromTransient(transientData, key), new GenericType<Map<Choice, Long>>() { })
        );
    }

    /**
     * Build 2 parameters as specified in buildFirst and buildSecond from the transient and applies the action.
     * @param context The {@link Context} containing the transient.
     * @param buildFirst The {@link Function} used to build first parameter from transient.
     * @param buildSecond The {@link Function} used to build second parameter from transient.
     * @param action The {@link BiFunction} in which parameters has to be used.
     * @return The result of the action.
     * @param <P1> The {@link Class} of the first parameter.
     * @param <P2> The {@link Class} of the second parameter.
     * @param <R> The returning {@link java.lang.reflect.Type} of the action.
     */
    public static <P1, P2, R> R applyToTransients(final Context context,
                                                  final Function<Map<String, byte[]>, P1> buildFirst,
                                                  final Function<Map<String, byte[]>, P2> buildSecond,
                                                  final BiFunction<P1, P2, R> action) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final P1 firstParam = buildFirst.apply(transientMap);
        final P2 secondParam = buildSecond.apply(transientMap);
        return action.apply(firstParam, secondParam);
    }

    /**
     * Build 2 parameters as specified in buildFirst and buildSecond from the transient and applies the action.
     * @param context The {@link Context} containing the transient.
     * @param buildFirst The {@link Function} used to build first parameter from transient.
     * @param buildSecond The {@link Function} used to build second parameter from transient.
     * @param action The {@link BiConsumer} in which parameters has to be used.
     * @param <P1> The {@link Class} of the first parameter.
     * @param <P2> The {@link Class} of the second parameter.
     */
    public static <P1, P2> void doWithTransients(final Context context,
                                                  final Function<Map<String, byte[]>, P1> buildFirst,
                                                  final Function<Map<String, byte[]>, P2> buildSecond,
                                                  final BiConsumer<P1, P2> action) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final P1 firstParam = buildFirst.apply(transientMap);
        final P2 secondParam = buildSecond.apply(transientMap);
        action.accept(firstParam, secondParam);
    }

    @FunctionalInterface
    public interface ThreeParameterFunction<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }

    /**
     * Build 2 parameters as specified in buildFirst and buildSecond from the transient and applies the action.
     * @param context The {@link Context} containing the transient.
     * @param buildFirst The {@link Function} used to build first parameter from transient.
     * @param buildSecond The {@link Function} used to build second parameter from transient.
     * @param buildThird The {@link Function} used to build third parameter from transient.
     * @param action The {@link ThreeParameterFunction} in which parameters has to be used.
     * @return The result of the action.
     * @param <P1> The {@link Class} of the first parameter.
     * @param <P2> The {@link Class} of the second parameter.
     * @param <P3> The {@link Class} of the third parameter.
     * @param <R> The returning {@link java.lang.reflect.Type} of the action.
     */
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
}


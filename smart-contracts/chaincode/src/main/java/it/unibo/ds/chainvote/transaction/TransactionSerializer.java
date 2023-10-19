package it.unibo.ds.chainvote.transaction;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.utils.ArgsData;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.utils.Choice;
import org.hyperledger.fabric.contract.annotation.Serializer;
import org.hyperledger.fabric.contract.execution.SerializerInterface;
import org.hyperledger.fabric.contract.metadata.TypeSchema;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * A hyperledger custom serializer for calling the smart contract transactions
 * with custom data types.
 */
@Serializer()
public final class TransactionSerializer implements SerializerInterface {

    private final Genson genson = GensonUtils.create();

    private String getFromStringFormattedAsKeyValue(final String formatted, final int idx) {
        return formatted.split(":", 2)[idx];
    }

    /**
     * Serialize the value into bytes.
     * @param value the {@link Object} to serialize.
     * @param ts the {@link TypeSchema} of the value.
     * @return the bytes representing the value serialized.
     */
    @Override
    public byte[] toBuffer(final Object value, final TypeSchema ts) {
        String beforeOutput = "";
        if (value.getClass().equals(LocalDateTime.class)) {
            beforeOutput = ArgsData.DATE.getKey() + ":";
        } else if (value.getClass().equals(Choice.class)) {
            beforeOutput = ArgsData.CHOICE.getKey() + ":";
        } else if (value.getClass().equals(Long.class)) {
            beforeOutput = ArgsData.VOTERS.getKey() + ":";
        } else if (value instanceof List<?>) {
            beforeOutput = ArgsData.CHOICES.getKey() + ":";
        } else if (value instanceof Map<?, ?>) {
            beforeOutput = ArgsData.RESULTS.getKey() + ":";
        } else if (value.getClass().equals(ElectionInfo.class)) {
            beforeOutput = ArgsData.ELECTION_INFO.getKey() + ":";
        } else if (value.getClass().equals(Election.class)) {
            beforeOutput = ArgsData.ELECTION.getKey() + ":";
        } else if (value.getClass().equals(String.class)) { }
        else if (value.getClass().equals(Boolean.class)) { }
        return (beforeOutput + genson.serialize(value)).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Deserialize the object previously serialized in bytes.
     * @param buffer the byte buffer from the wire.
     * @param ts     the TypeSchema representing the type.
     * @return the {@link Object} deserialized.
     */
    @Override
    public Object fromBuffer(final byte[] buffer, final TypeSchema ts) {
        String key = getFromStringFormattedAsKeyValue(new String(buffer, StandardCharsets.UTF_8), 0);
        String value = getFromStringFormattedAsKeyValue(new String(buffer, StandardCharsets.UTF_8), 1);
        if (key.equals(ArgsData.DATE.getKey()) || key.equals(ArgsData.STARTING_DATE.getKey()) || key.equals(ArgsData.ENDING_DATE.getKey())) {
            return genson.deserialize(value, LocalDateTime.class);
        } else if (key.equals(ArgsData.CHOICE.getKey())) {
            return genson.deserialize(value, Choice.class);
        } else if (key.equals(ArgsData.CHOICES.getKey())) {
            return genson.deserialize(value, new GenericType<List<Choice>>() { });
        } else if (key.equals(ArgsData.RESULTS.getKey())) {
            return genson.deserialize(value, new GenericType<Map<Choice, Long>>() { });
        } else if (key.equals(ArgsData.VOTERS.getKey())) {
            return Long.valueOf(value);
        } else if (key.equals(ArgsData.ELECTION.getKey())) {
            return genson.deserialize(value, Election.class);
        } else if (key.equals(ArgsData.ELECTION_INFO.getKey())) {
            return genson.deserialize(value, ElectionInfo.class);
        } else {
            return value;
        }
    }
}

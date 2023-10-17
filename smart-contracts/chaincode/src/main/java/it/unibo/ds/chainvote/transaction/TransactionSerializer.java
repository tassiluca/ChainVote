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

    @Override
    public byte[] toBuffer(final Object value, final TypeSchema ts) {
        System.out.println("[TS - toBuffer]");
        System.out.println("TS: " + ts);
        System.out.println("Value: " + value);
        System.out.println("Value class: " + value.getClass());
        String beforeOutput = "";
        if (value.getClass().equals(LocalDateTime.class)) {
            System.out.println("It's a date");
            beforeOutput = ArgsData.DATE.getKey() + ":";
        } else if (value.getClass().equals(Choice.class)) {
            System.out.println("It's a choice");
            beforeOutput = ArgsData.CHOICE.getKey() + ":";
        } else if (value.getClass().equals(Long.class)) {
            System.out.println("It's a long");
            beforeOutput = ArgsData.VOTERS.getKey() + ":";
        } else if (value instanceof List<?>) {
            System.out.println("It's a list");
            beforeOutput = ArgsData.CHOICES.getKey() + ":";
        } else if (value instanceof Map<?, ?>) {
            System.out.println("It's a result");
            beforeOutput = ArgsData.RESULTS.getKey() + ":";
        } else if (value.getClass().equals(String.class) && getFromStringFormattedAsKeyValue((String) value, 0).equals(ArgsData.ELECTION_INFO.getKey())) {
            System.out.println("It's an electionInfo");
        } else if (value.getClass().equals(String.class) && getFromStringFormattedAsKeyValue((String) value, 0).equals(ArgsData.ELECTION.getKey())) {
            System.out.println("It's an election");
        } else if (value.getClass().equals(String.class)) {
            System.out.println("It's a string");
        } else if (value.getClass().equals(Boolean.class)) {
            System.out.println("It's a boolean");
        }
        return (beforeOutput + genson.serialize(value)).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object fromBuffer(final byte[] buffer, final TypeSchema ts) {
        System.out.println("[TS - fromBuffer]");
        System.out.println("TS: " + ts);
        System.out.println("Buffer: " + new String(buffer, StandardCharsets.UTF_8));
        String key = getFromStringFormattedAsKeyValue(new String(buffer, StandardCharsets.UTF_8), 0);
        String value = getFromStringFormattedAsKeyValue(new String(buffer, StandardCharsets.UTF_8), 1);
        System.out.println("Key: " + key);
        System.out.println("Value: " + value);
        if (key.equals(ArgsData.DATE.getKey()) || key.equals(ArgsData.STARTING_DATE.getKey()) || key.equals(ArgsData.ENDING_DATE.getKey())) {
            System.out.println("It's a date: " + genson.deserialize(value, LocalDateTime.class));
            return genson.deserialize(value, LocalDateTime.class);
        } else if (key.equals(ArgsData.CHOICE.getKey())) {
            System.out.println("It's a choice: " + genson.deserialize(value, Choice.class));
            return genson.deserialize(value, Choice.class);
        } else if (key.equals(ArgsData.CHOICES.getKey())) {
            System.out.println("It's a list of choices: " + genson.deserialize(value, new GenericType<List<Choice>>() { }));
            return genson.deserialize(value, new GenericType<List<Choice>>() { });
        } else if (key.equals(ArgsData.RESULTS.getKey())) {
            System.out.println("It's a result: " + genson.deserialize(value, new GenericType<Map<Choice, Long>>() { }));
            return genson.deserialize(value, new GenericType<Map<Choice, Long>>() { });
        } else if (key.equals(ArgsData.VOTERS.getKey())) {
            System.out.println("It's a long: " + Long.valueOf(value));
            return Long.valueOf(value);
        } else if (key.equals(ArgsData.ELECTION.getKey())) {
            System.out.println("It's an election: " + genson.deserialize(value, Election.class));
            return genson.deserialize(value, Election.class);
        } else if (key.equals(ArgsData.ELECTION_INFO.getKey())) {
            System.out.println("It's an electionInfo: " + genson.deserialize(value, ElectionInfo.class));
            return genson.deserialize(value, ElectionInfo.class);
        } else {
            String type = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") ? "boolean" : "string";
            System.out.println("It's a " + type + ": " + value);
            return value;
        }
    }
}

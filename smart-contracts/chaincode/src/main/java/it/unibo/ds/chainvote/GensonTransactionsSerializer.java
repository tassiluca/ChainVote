package it.unibo.ds.chainvote;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.utils.Choice;
import org.hyperledger.fabric.contract.execution.SerializerInterface;
import org.hyperledger.fabric.contract.metadata.TypeSchema;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * A hyperledger custom serializer for calling the smart contract transactions
 * with custom data types.
 */
public abstract class GensonTransactionsSerializer implements SerializerInterface {

    private final Genson genson;

    public GensonTransactionsSerializer(final Genson genson) {
        this.genson = genson;
    }

    /**
     * Serialize the value into bytes.
     * @param value the {@link Object} to serialize.
     * @param ts the {@link TypeSchema} of the value.
     * @return the bytes representing the value serialized.
     */
    @Override
    public byte[] toBuffer(final Object value, final TypeSchema ts) {
        return genson.serialize(new Response<>(value)).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Deserialize the object previously serialized in bytes.
     * @param buffer the byte buffer from the wire.
     * @param ts the TypeSchema representing the type.
     * @return the {@link Object} deserialized.
     */
    @Override
    public Object fromBuffer(final byte[] buffer, final TypeSchema ts) {
        System.out.println("[TS] fromBuffer");
        String value = new String(buffer, StandardCharsets.UTF_8);
        System.out.println("[TS fb] value: " + value);
        String type = ts.get("schema").toString();
        String key = "";
        if (ts.getRef() == null) {
            key = ts.getType();
        } else {
            key = ts.getRef().split("/")[type.split("/").length - 1];
        }
        System.out.println("[TS fb] key type: " + key);
        System.out.print("Value deserialized: ");
        switch (key) {
            case "List":
                System.out.println(genson.deserialize(value, new GenericType<List<Choice>>() { }));
                return genson.deserialize(value, new GenericType<List<Choice>>() { });
            case "Map":
                System.out.println(genson.deserialize(value, new GenericType<Map<String, Long>>() { }));
                return genson.deserialize(value, new GenericType<Map<String, Long>>() { });
            case "integer":
                System.out.println(Long.valueOf(value));
                return Long.valueOf(value);
            case "Election":
                System.out.println(genson.deserialize(value, Election.class));
                return genson.deserialize(value, Election.class);
            case "ElectionInfo":
                System.out.println(genson.deserialize(value, ElectionInfo.class));
                return genson.deserialize(value, ElectionInfo.class);
            case "LocalDateTime":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                System.out.println("That's a date.. " + LocalDateTime.parse(value, formatter));
                return LocalDateTime.parse(value, formatter);
            case "Choice":
                System.out.println(genson.deserialize(value, Choice.class));
                return genson.deserialize(value, Choice.class);
        }
        System.out.println("It's a string " + value);
        return value;
    }
}

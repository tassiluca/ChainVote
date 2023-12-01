package it.unibo.ds.chainvote;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.elections.Election;
import it.unibo.ds.chainvote.elections.ElectionInfo;
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
 * with custom data types. It leverages on {@link Genson} library to (de)serialize.
 */
public abstract class GensonTransactionsSerializer implements SerializerInterface {

    private final Genson genson;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    /**
     * Creates a {@link Genson} specific transaction serializer.
     * @param genson the instance of genson to use to (de)serialize.
     */
    protected GensonTransactionsSerializer(final Genson genson) {
        this.genson = genson;
    }

    /**
     * Convert the {@link Object} value in a {@link Response} and serialize it in bytes.
     * @param value the {@link Object} to serialize.
     * @param ts the {@link TypeSchema} of the value.
     * @return bytes representing the {@link Response} of the value serialized.
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
        final String value = new String(buffer, StandardCharsets.UTF_8);
        final String type = ts.get("schema").toString();
        final String key = ts.getRef() == null ? ts.getType() : ts.getRef().split("/")[type.split("/").length - 1];
        switch (key) {
            case "List":
                return genson.deserialize(value, new GenericType<List<Choice>>() { });
            case "Map":
                return genson.deserialize(value, new GenericType<Map<String, Long>>() { });
            case "integer":
                return Long.valueOf(value);
            case "Election":
                return genson.deserialize(value, Election.class);
            case "ElectionInfo":
                return genson.deserialize(value, ElectionInfo.class);
            case "LocalDateTime":
                return LocalDateTime.parse(value, formatter);
            case "Choice":
                return genson.deserialize(value, Choice.class);
            default:
                return value;
        }
    }
}

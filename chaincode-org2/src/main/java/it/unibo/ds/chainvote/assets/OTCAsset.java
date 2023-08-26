package it.unibo.ds.chainvote.assets;

import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@DataType
public class OTCAsset {

    private static final Genson genson = GensonUtils.create();

    private final Long code;

    private final String electionId;

    public OTCAsset(@JsonProperty("electionId") final String electionId, @JsonProperty("code") final Long code) {
        this.electionId = electionId;
        this.code = code;
    }

    public Long getCode() {
        return code;
    }

    public String getElectionId() {
        return electionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OTCAsset otcAsset = (OTCAsset) o;
        return Objects.equals(code, otcAsset.code) && Objects.equals(electionId, otcAsset.electionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, electionId);
    }

    @Override
    public String toString() {
        return "OTCAsset{" +
            "code=" + code +
            ", electionId='" + electionId + '\'' +
            '}';
    }

    public static String serialize(final OTCAsset asset) {
        return genson.serialize(asset);
    }

    public static OTCAsset deserialize(final byte[] serializedAsset) {
        return deserialize(new String(serializedAsset, StandardCharsets.UTF_8));
    }

    public static OTCAsset deserialize(final String serializedAsset) {
        return genson.deserialize(serializedAsset, OTCAsset.class);
    }
}

package it.unibo.ds.chainvote.assets;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.core.codes.OneTimeCode;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * Hyperledger Fabric {@link OneTimeCode} asset.
 */
@DataType
public final class OneTimeCodeAsset {

    @Property
    private final OneTimeCode asset;

    @Property
    private final String userId;

    @Property
    private final String electionId;

    /**
     * Creates the one-time-code asset.
     * @param code the generated code.
     */
    public OneTimeCodeAsset(
        @JsonProperty("asset") final OneTimeCode code,
        @JsonProperty("userId") final String userId,
        @JsonProperty("electionId") final String electionId
    ) {
        this.asset = code;
        this.userId = userId;
        this.electionId = electionId;
    }

    /**
     * @return the code
     */
    public OneTimeCode getAsset() {
        return asset;
    }

    public String getUserId() {
        return userId;
    }

    public String getElectionId() {
        return electionId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OneTimeCodeAsset that = (OneTimeCodeAsset) o;
        return Objects.equals(asset, that.asset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asset);
    }

    @Override
    public String toString() {
        return "OneTimeCodeAsset{asset=" + asset + '}';
    }
}

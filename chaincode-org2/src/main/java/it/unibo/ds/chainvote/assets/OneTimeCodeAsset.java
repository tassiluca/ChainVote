package it.unibo.ds.chainvote.assets;

import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.OneTimeCode;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

/**
 * Hyperledger Fabric {@link OneTimeCode} asset.
 */
@DataType
public final class OneTimeCodeAsset {

    private static final Genson genson = GensonUtils.create();

    @Property
    private final String electionId;

    @Property
    private final String userId;

    @Property
    private final OneTimeCode asset;

    public OneTimeCodeAsset(
        @JsonProperty("electionId") final String electionId,
        @JsonProperty("userId") final String userId,
        @JsonProperty("asset") final OneTimeCode code
    ) {
        this.electionId = electionId;
        this.asset = code;
        this.userId = userId;
    }

    public String getElectionId() {
        return electionId;
    }

    public OneTimeCode getAsset() {
        return asset;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "OneTimeCodeAsset{" +
            "electionId='" + electionId + '\'' +
            ", asset=" + asset +
            ", userId='" + userId + '\'' +
            '}';
    }
}

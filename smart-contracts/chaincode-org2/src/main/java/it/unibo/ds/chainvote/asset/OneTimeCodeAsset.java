package it.unibo.ds.chainvote.asset;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.chainvote.codes.OneTimeCode;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

/**
 * Hyperledger Fabric {@link OneTimeCode} asset.
 */
@DataType
public final class OneTimeCodeAsset {

    @Property
    private final String electionId;

    @Property
    private final String userId;

    @Property
    private final OneTimeCode code;

    /**
     * Creates a new asset.
     * @param electionId the election identifier
     * @param userId the user identifier
     * @param code the code to be associated to the given election and user
     */
    public OneTimeCodeAsset(
        @JsonProperty("electionId") final String electionId,
        @JsonProperty("userId") final String userId,
        @JsonProperty("code") final OneTimeCode code
    ) {
        this.electionId = electionId;
        this.code = code;
        this.userId = userId;
    }

    /**
     * @return the election identifier
     */
    public String getElectionId() {
        return electionId;
    }

    /**
     * @return the code
     */
    public OneTimeCode getCode() {
        return code;
    }

    /**
     * @return the user identifier
     */
    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "OneTimeCodeAsset{"
            + "electionId='" + electionId + '\''
            + ", code=" + code
            + ", userId='" + userId + '\''
            + '}';
    }
}

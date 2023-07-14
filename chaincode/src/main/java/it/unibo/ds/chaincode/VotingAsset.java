package it.unibo.ds.chaincode;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.core.Voting;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

/**
 * The hyperledger fabric notion of voting asset.
 * @param asset the voting asset.
 */
@DataType
public record VotingAsset(@Property Voting asset) {

    /**
     * Creates an asset with the given voting.
     * @param asset the concrete voting.
     */
    public VotingAsset(final @JsonProperty("asset") Voting asset) {
        this.asset = asset;
    }
}

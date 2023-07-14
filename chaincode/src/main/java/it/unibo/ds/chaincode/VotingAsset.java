package it.unibo.ds.chaincode;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.core.Voting;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public record VotingAsset(@Property Voting asset) {

    public VotingAsset(@JsonProperty("asset") Voting asset) {
        this.asset = asset;
    }
}

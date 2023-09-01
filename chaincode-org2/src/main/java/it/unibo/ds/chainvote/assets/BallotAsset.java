package it.unibo.ds.chainvote.assets;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.core.assets.Ballot;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType
public final class BallotAsset {

    @Property()
    private final String electionID;

    @Property()
    private final Ballot asset;

    public BallotAsset(@JsonProperty("electionID") final String electionID,
                       @JsonProperty("asset") final Ballot asset) {
        this.electionID = electionID;
        this.asset = asset;
    }

    public String getElectionID() {
        return this.electionID;
    }

    public Ballot getAsset() {
        return this.asset;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BallotAsset that = (BallotAsset) o;
        return Objects.equals(asset, that.getAsset()) && electionID.equals(that.getElectionID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(asset);
    }

    @Override
    public String toString() {
        return "ElectionAsset{asset=" + asset + '}';
    }

}

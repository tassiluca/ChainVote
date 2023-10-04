package it.unibo.ds.chainvote.assets;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.core.assets.Election;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class ElectionAsset {

    @Property()
    private final String electionId;

    @Property()
    private final Election asset;

    public ElectionAsset(@JsonProperty("electionID") final String electionId,
                         @JsonProperty("asset") final Election asset) {
        this.asset = asset;
        this.electionId = electionId;
    }

    public Election getAsset() {
        return this.asset;
    }

    public String getElectionId() {
        return this.electionId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ElectionAsset that = (ElectionAsset) o;
        return Objects.equals(asset, that.getAsset()) && electionId.equals(that.getElectionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(asset);
    }

    @Override
    public String toString() {
        return "ElectionAsset{asset=" + asset + ", electionID=" + electionId + "}";
    }
}

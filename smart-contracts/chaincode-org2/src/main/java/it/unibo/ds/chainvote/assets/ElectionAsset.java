package it.unibo.ds.chainvote.assets;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.core.assets.Election;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * Hyperledger fabric {@link Election} asset.
 * TODO improve documentation
 */
@DataType()
public final class ElectionAsset {

    @Property()
    private final String electionId;

    @Property()
    private final Election asset;

    /**
     * Creates a new election asset.
     * @param electionId the election identifier
     * @param asset TODO
     */
    public ElectionAsset(
        @JsonProperty("electionID") final String electionId,
        @JsonProperty("asset") final Election asset
    ) {
        this.asset = asset;
        this.electionId = electionId;
    }

    /**
     * @return TODO
     */
    public Election getAsset() {
        return this.asset;
    }

    /**
     * @return TODO
     */
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

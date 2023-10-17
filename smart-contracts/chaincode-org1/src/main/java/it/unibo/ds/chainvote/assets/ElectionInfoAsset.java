package it.unibo.ds.chainvote.assets;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.core.assets.ElectionInfo;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * TODO document better
 * Hyperledger Fabric {@link ElectionInfo} asset.
 */
@DataType()
public final class ElectionInfoAsset {

    @Property()
    private final String electionId;

    @Property()
    private final ElectionInfo asset;

    /**
     * Creates a new election info asset.
     * @param electionId the election identifier.
     * @param asset the {@link ElectionInfo}
     */
    public ElectionInfoAsset(
        @JsonProperty("electionID") final String electionId,
        @JsonProperty("asset") final ElectionInfo asset
    ) {
        this.asset = asset;
        this.electionId = electionId.isEmpty() ? this.asset.getElectionID() : electionId;
    }

    /**
     * @return the asset.
     */
    public ElectionInfo getAsset() {
        return this.asset;
    }

    /**
     * @return the election identifier.
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
        final ElectionInfoAsset that = (ElectionInfoAsset) o;
        return Objects.equals(asset, that.getAsset()) && electionId.equals(that.getElectionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(asset);
    }

    @Override
    public String toString() {
        return "ElectionInfoAsset{asset=" + asset + ", electionID=" + electionId + "}";
    }
}

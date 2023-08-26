package it.unibo.ds.chainvote.assets;

import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.OneTimeCode;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Hyperledger Fabric {@link OneTimeCode} asset.
 */
@DataType
public final class OTCAssetPrivateDetails {

    private static final Genson genson = GensonUtils.create();

    @Property
    private final String electionId;

    @Property
    private String userId;

    @Property
    private final OneTimeCode asset;

    public OTCAssetPrivateDetails(
        @JsonProperty("asset") final OneTimeCode code,
        @JsonProperty("electionId") final String electionId,
        @JsonProperty("userId") final String userId
    ) {
        this.electionId = electionId;
        this.asset = code;
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

    public static String serialize(final OTCAssetPrivateDetails asset) {
        return genson.serialize(asset);
    }

    public static OTCAssetPrivateDetails deserialize(final byte[] serializedAsset) {
        return deserialize(new String(serializedAsset, StandardCharsets.UTF_8));
    }

    public static OTCAssetPrivateDetails deserialize(final String serializedAsset) {
        try {
            final JSONObject json = new JSONObject(serializedAsset);
            final String userId = json.getString("userId");
            final String electionId = json.getString("electionId");
            final OneTimeCode asset = genson.deserialize(json.getJSONObject("asset").toString(), OneTimeCode.class);
            return new OTCAssetPrivateDetails(asset, electionId, userId);
        } catch (JSONException exception) {
            throw new ChaincodeException("Deserialize error: " + exception.getMessage(), "DATA_ERROR");
        }
    }
}

package it.unibo.ds.chainvote.assets;

import com.owlike.genson.Genson;
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
    private final OneTimeCode asset;

    @Property
    private String userId;

    public OTCAssetPrivateDetails(final OneTimeCode code, final String electionId) {
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

    public void setUserId(final String userId) {
        if (this.userId != null && !this.userId.isBlank()) {
            throw new IllegalStateException("This asset was already associated to a user");
        }
        this.userId = userId;
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
            final String userId = json.get("userId") instanceof String ? json.getString("userId") : null;
            final String electionId = json.getString("electionId");
            final OneTimeCode asset = genson.deserialize(json.getJSONObject("asset").toString(), OneTimeCode.class);
            final OTCAssetPrivateDetails otcAsset = new OTCAssetPrivateDetails(asset, electionId);
            if (userId != null) { otcAsset.setUserId(userId); }
            return otcAsset;
        } catch (JSONException exception) {
            throw new ChaincodeException("Deserialize error: " + exception.getMessage(), "DATA_ERROR");
        }
    }
}

package it.unibo.ds.chainvote;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.core.codes.OneTimeCode;
import org.hyperledger.fabric.contract.annotation.DataType;

import java.util.Objects;

/**
 * Hyperledger Fabric {@link OneTimeCode} asset.
 */
@DataType
public final class OneTimeCodeAsset {

    @JsonProperty
    private final OneTimeCode code;

    /**
     * Creates the one-time-code asset.
     * @param code the generated code.
     */
    public OneTimeCodeAsset(@JsonProperty("code") final OneTimeCode code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public OneTimeCode getCode() {
        return code;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OneTimeCodeAsset that = (OneTimeCodeAsset) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "OneTimeCodeAsset{" + "code=" + code + '}';
    }
}

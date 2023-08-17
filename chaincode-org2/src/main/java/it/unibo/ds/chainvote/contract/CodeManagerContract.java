package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.OneTimeCodeAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.CodeManager;
import it.unibo.ds.core.codes.CodeManagerImpl;
import it.unibo.ds.core.codes.CodeRepository;
import it.unibo.ds.core.codes.OneTimeCode;
import it.unibo.ds.core.codes.OneTimeCodeImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An Hyperledger Fabric chaincode entry point contract to manage one-time-codes.
 * TODO replace args with transient data
 */
@Contract(
    name = "CodeManagerContract",
    info = @Info(
        title = "Code Manager Contract",
        description = "Contract used to manage one-time-codes"
    )
)
public final class CodeManagerContract implements ContractInterface, CodeRepository<Context> {

    static final String CODES_COLLECTION = "CodesCollection";
    private final CodeManager<Context> codeManager = new CodeManagerImpl<>(this);
    private final Genson genson = GensonUtils.create();

    private enum CodeManagerErrors {
        INCOMPLETE_INPUT,
        ALREADY_GENERATED_CODE,
        WRONG_BIND
    }

    /**
     * Generate a new one-time-code for the given user and election passed in a transient map.
     * Note: a `userId` and `electionId` transient data are expected.
     * @param context the transaction context
     * @return the code asset.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public OneTimeCodeAsset generateFor(final Context context) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String userId = getFromTransient(transientMap, "userId");
        final String electionId = getFromTransient(transientMap, "electionId");
        try {
            return new OneTimeCodeAsset(codeManager.generateFor(context, electionId, userId));
        } catch (IllegalStateException exception) {
            throw new ChaincodeException(exception.getMessage(), CodeManagerErrors.ALREADY_GENERATED_CODE.toString());
        }
    }

    /**
     * Check if the given code is still valid, i.e. has not been consumed yet for the given election
     * passed in a transient map.
     * Note: a `electionId` and `code` transient data are expected.
     * @param context the transaction context
     * @return true if the given code is still valid, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(final Context context) {
        return false; // TODO
    }

    /**
     * Invalidate the given code for the given election passed in a transient map.
     * After calling this method the code can no longer be used.
     * Note: a `electionId` and `code` transient data are expected.
     * @param context the transaction context
     */
    @Transaction
    public void invalidate(final Context context) {
        // TODO
    }

    /**
     * Verifies if the given code has been generated for the given user and election passed in a transient map.
     * Note: a `electionId`, `userId` and `code` transient data are expected.
     * @param context the transaction context
     * @return true if the given code is correct, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean verifyCodeOwner(final Context context) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String userId = getFromTransient(transientMap, "userId");
        final String electionId = getFromTransient(transientMap, "electionId");
        final Long code = Long.valueOf(getFromTransient(transientMap, "code"));
        return codeManager.verifyCodeOwner(context, electionId, userId, new OneTimeCodeImpl(code));
    }

    private String getFromTransient(final Map<String, byte[]> transientMap, final String key) {
        if (!transientMap.containsKey(key)) {
            final String errorMsg = "A `" + key + "` transient input was expected.";
            throw new ChaincodeException(errorMsg, CodeManagerErrors.INCOMPLETE_INPUT.toString());
        }
        return Arrays.toString(transientMap.get(key));
    }

    @Override
    public Optional<OneTimeCode> get(final Context context, final String electionId, final String userId) {
        final OneTimeCodeAsset data = genson.deserialize(
            context.getStub().getPrivateData(
                CODES_COLLECTION,
                new CompositeKey(electionId, userId).getObjectType()
            ),
            OneTimeCodeAsset.class
        );
        return Optional.ofNullable(data).map(OneTimeCodeAsset::getAsset);
    }

    @Override
    public void put(final Context context, final String electionId, final String userId, final OneTimeCode code) {
        context.getStub().putPrivateData(
            CODES_COLLECTION,
            new CompositeKey(electionId, userId).getObjectType(),
            genson.serialize(new OneTimeCodeAsset(code))
        );
    }

    @Override
    public void replace(final Context context, final String electionId, final OneTimeCode code) {
        // TODO implement
    }

    @Override
    public Set<OneTimeCode> getAllOf(final Context context, final String electionId) {
        // TODO implement
        return Set.of();
    }
}

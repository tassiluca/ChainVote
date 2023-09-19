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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static it.unibo.ds.chainvote.utils.TransientUtils.getLongFromTransient;
import static it.unibo.ds.chainvote.utils.TransientUtils.getStringFromTransient;

/**
 * A Hyperledger Fabric contract to manage one-time-codes.
 */
@Contract(
    name = "CodesManagerContract",
    info = @Info(
        title = "Code Manager Contract",
        description = "Contract used to manage one-time-codes"
    )
)
public final class CodesManagerContract implements ContractInterface, CodeRepository<Context> {

    static final String CODES_COLLECTION = "CodesCollection";
    private final CodeManager<Context> codeManager = new CodeManagerImpl<>(this);
    private final Genson genson = GensonUtils.create();

    private enum CodeManagerErrors {
        INCOMPLETE_INPUT,
        ALREADY_GENERATED_CODE,
        ALREADY_INVALIDATED_CODE
    }

    private enum TransientData {
        USER_ID("userId"),
        ELECTION_ID("electionId"),
        CODE("code");

        final String key;

        TransientData(final String key) {
            this.key = key;
        }
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public void testCCI(final Context context) {
        context.getStub().invokeChaincodeWithStringArgs("chaincode-org1", List.of("sampleFunction"), "ch1");
    }

    /**
     * Generate a new one-time-code for the given user and election passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *               key-value pairs: `userId` and `electionId`.
     * @return the code asset.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Long generateFor(final Context context) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String userId = getStringFromTransient(transientMap, TransientData.USER_ID.key);
        final String electionId = getStringFromTransient(transientMap, TransientData.ELECTION_ID.key);
        try {
            return codeManager.generateFor(context, electionId, userId).getCode();
        } catch (IllegalStateException exception) {
            throw new ChaincodeException(exception.getMessage(), CodeManagerErrors.ALREADY_GENERATED_CODE.toString());
        }
    }

    /**
     * Check if the given code is still valid, i.e. has not been consumed yet for the given election
     * passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId`, `userId` and `code`.
     * @return true if the given code is still valid, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(final Context context) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String electionId = getStringFromTransient(transientMap, TransientData.ELECTION_ID.key);
        final String userId = getStringFromTransient(transientMap, TransientData.USER_ID.key);
        final Long code = getLongFromTransient(transientMap, TransientData.CODE.key);
        return codeManager.isValid(context, electionId, userId, new OneTimeCodeImpl(code));
    }

    /**
     * Invalidate the given code for the given election passed in a transient map.
     * After calling this method the code can no longer be used.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId`, `userId` and `code`.
     */
    @Transaction
    public void invalidate(final Context context) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String electionId = getStringFromTransient(transientMap, TransientData.ELECTION_ID.key);
        final String userId = getStringFromTransient(transientMap, TransientData.USER_ID.key);
        final Long code = getLongFromTransient(transientMap, TransientData.CODE.key);
        try {
            codeManager.invalidate(context, electionId, userId, new OneTimeCodeImpl(code));
        } catch (IllegalStateException exception) {
            throw new ChaincodeException(exception.getMessage(), CodeManagerErrors.ALREADY_INVALIDATED_CODE.toString());
        }
    }

    /**
     * Verifies if the given code has been generated for the given user and election passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId`, `userId` and `code`
     * @return true if the given code is correct, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean verifyCodeOwner(final Context context) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String electionId = getStringFromTransient(transientMap, TransientData.ELECTION_ID.key);
        final String userId = getStringFromTransient(transientMap, TransientData.USER_ID.key);
        final Long code = getLongFromTransient(transientMap, TransientData.CODE.key);
        return codeManager.verifyCodeOwner(context, electionId, userId, new OneTimeCodeImpl(code));
    }

    @Override
    public Optional<OneTimeCode> get(final Context context, final String electionId, final String userId) {
        final OneTimeCodeAsset data = genson.deserialize(
            context.getStub().getPrivateData(
                CODES_COLLECTION,
                new CompositeKey(electionId, userId).toString()
            ),
            OneTimeCodeAsset.class
        );
        return Optional.ofNullable(data).map(OneTimeCodeAsset::getCode);
    }

    @Override
    public void put(final Context context, final String electionId, final String userId, final OneTimeCode code) {
        context.getStub().putPrivateData(
            CODES_COLLECTION,
            new CompositeKey(electionId, userId).toString(),
            genson.serialize(new OneTimeCodeAsset(electionId, userId, code))
        );
    }

    @Override
    public void replace(final Context context, final String electionId, final String userId, final OneTimeCode code) {
        put(context, electionId, userId, code);
    }
}

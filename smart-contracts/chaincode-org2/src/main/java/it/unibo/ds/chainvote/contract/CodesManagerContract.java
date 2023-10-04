package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.OneTimeCodeAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.AlreadyConsumedCodeException;
import it.unibo.ds.core.codes.AlreadyGeneratedCodeException;
import it.unibo.ds.core.codes.CodeManager;
import it.unibo.ds.core.codes.CodeManagerImpl;
import it.unibo.ds.core.codes.CodeRepository;
import it.unibo.ds.core.codes.NotValidCodeException;
import it.unibo.ds.core.codes.OneTimeCode;
import it.unibo.ds.core.codes.OneTimeCodeImpl;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.util.TriConsumer;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static it.unibo.ds.chaincode.utils.TransientData.CODE;
import static it.unibo.ds.chaincode.utils.TransientData.ELECTION_ID;
import static it.unibo.ds.chaincode.utils.TransientData.USER_ID;
import static it.unibo.ds.chaincode.utils.TransientUtils.getLongFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getStringFromTransient;

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

    private enum Error {
        INCOMPLETE_INPUT,
        INVALID_INPUT,
        ALREADY_GENERATED_CODE,
        ALREADY_INVALIDATED_CODE
    }

    /**
     * Generate a new one-time-code for the given user and election passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `userId` and `electionId`.
     * @return the code asset.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Long generateFor(final Context context) {
        return applyToTransients(context, (electionId, userId) -> {
            if (!electionExists(context, electionId)) {
                throw new ChaincodeException("The given election doesn't exists", Error.INVALID_INPUT.toString());
            }
            try {
                return codeManager.generateFor(context, electionId, userId).getCode();
            } catch (AlreadyGeneratedCodeException exception) {
                throw new ChaincodeException(exception.getMessage(), Error.ALREADY_GENERATED_CODE.toString());
            }
        });
    }

    private boolean electionExists(final Context context, final String electionId) {
        final Chaincode.Response response = context.getStub().invokeChaincodeWithStringArgs(
            "chaincode-org2",
            List.of("electionExists", electionId),
            "ch2"
        );
        return Boolean.parseBoolean(response.getStringPayload());
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
        return applyToTransients(context, (electionId, userId, code) ->
            codeManager.isValid(context, electionId, userId, new OneTimeCodeImpl(code))
        );
    }

    /**
     * Serialize the isValid result in order to route response as result of a cross-chaincode invocation.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId`, `userId` and `code`.
     * @return the boolean value serialized.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String isValidSerialized(final Context context) {
        return genson.serialize(isValid(context));
    }

    /**
     * Invalidate the given code for the given election passed in a transient map.
     * After calling this method the code can no longer be used.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId`, `userId` and `code`.
     */
    @Transaction
    public void invalidate(final Context context) {
        doWithTransients(context, (electionId, userId, code) -> {
            try {
                codeManager.invalidate(context, electionId, userId, new OneTimeCodeImpl(code));
            } catch (AlreadyConsumedCodeException exception) {
                throw new ChaincodeException(exception.getMessage(), Error.ALREADY_INVALIDATED_CODE.toString());
            } catch (NotValidCodeException exception) {
                throw new ChaincodeException(exception.getMessage(), Error.INVALID_INPUT.toString());
            }
        });
    }

    /**
     * Verifies if the given code has been generated for the given user and election passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId`, `userId` and `code`
     * @return true if the given code is correct, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean verifyCodeOwner(final Context context) {
        return applyToTransients(context, (electionId, userId, code) ->
            codeManager.verifyCodeOwner(context, electionId, userId, new OneTimeCodeImpl(code))
        );
    }

    private void doWithTransients(final Context context, final TriConsumer<String, String, Long> action) {
        applyToTransients(context, (electionId, userId, code) -> {
            action.accept(electionId, userId, code);
            return null;
        });
    }

    private <T> T applyToTransients(final Context context, final TriFunction<String, String, Long, T> action) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String electionId = getStringFromTransient(transientMap, ELECTION_ID.getKey());
        final String userId = getStringFromTransient(transientMap, USER_ID.getKey());
        final Long code = getLongFromTransient(transientMap, CODE.getKey());
        return action.apply(electionId, userId, code);
    }

    private <T> T applyToTransients(final Context context, final BiFunction<String, String, T> action) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String electionId = getStringFromTransient(transientMap, ELECTION_ID.getKey());
        final String userId = getStringFromTransient(transientMap, USER_ID.getKey());
        return action.apply(electionId, userId);
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

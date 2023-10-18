package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chaincode.utils.ArgsData;
import it.unibo.ds.chainvote.assets.OneTimeCodeAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.*;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.List;
import java.util.Optional;

import static it.unibo.ds.chaincode.utils.TransientData.*;
import static it.unibo.ds.chaincode.utils.TransientUtils.*;

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
        return applyToTransients(context,
                t -> getStringFromTransient(t, ELECTION_ID.getKey()),
                t -> getStringFromTransient(t, USER_ID.getKey()),
                (electionId, userId) -> {
            // TODO ASAP chaincode-org1 is ready
            //  if (!electionExists(context, electionId)) {
            //      throw new ChaincodeException(
            //          "The given election doesn't exists", CodeManagerErrors.INVALID_INPUT.toString()
            //      );
            //  }
            try {
                return codeManager.generateFor(context, electionId, userId).getCode();
            } catch (AlreadyGeneratedCodeException exception) {
                throw new ChaincodeException(exception.getMessage(), Error.ALREADY_GENERATED_CODE.toString());
            }
        });
    }

    private boolean electionExists(final Context context, final String electionId) {
        final Chaincode.Response response = context.getStub().invokeChaincodeWithStringArgs(
            "chaincode-org1",
            List.of("electionExists", ArgsData.ELECTION_ID.getKey() + ":" + electionId),
            "ch1"
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
    public boolean isValid(final Context context, String electionId) {
        return applyToTransients(context,
                t -> getStringFromTransient(t, USER_ID.getKey()),
                t -> getLongFromTransient(t, CODE.getKey()),
                (userId, code) -> codeManager.isValid(context, electionId, userId, new OneTimeCodeImpl(code))
        );
    }

    /**
     * Invalidate the given code for the given election passed in a transient map.
     * After calling this method the code can no longer be used.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId`, `userId` and `code`.
     */
    @Transaction
    public void invalidate(final Context context, String electionId) {
        doWithTransients(context,
                t -> getStringFromTransient(t, USER_ID.getKey()),
                t -> getLongFromTransient(t, CODE.getKey()),
                (userId, code) -> {
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
        return applyToTransients(context,
                t -> getStringFromTransient(t, ELECTION_ID.getKey()),
                t -> getStringFromTransient(t, USER_ID.getKey()),
                t -> getLongFromTransient(t, CODE.getKey()),
                (electionId, userId, code) ->
            codeManager.verifyCodeOwner(context, electionId, userId, new OneTimeCodeImpl(code))
        );
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

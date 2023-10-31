package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.Response;
import it.unibo.ds.chainvote.codes.*;
import it.unibo.ds.chainvote.utils.TransientUtils;
import it.unibo.ds.chainvote.assets.OneTimeCodeAsset;
import it.unibo.ds.chainvote.GensonUtils;
import it.unibo.ds.chainvote.utils.UserCodeData;
import it.unibo.ds.chainvote.utils.Pair;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.Optional;

import static it.unibo.ds.chainvote.utils.UserCodeData.USER_ID;

/**
 * A Hyperledger Fabric contract to manage one-time-codes.
 */
@Contract(
    name = "CodesManagerContract",
    info = @Info(
        title = "Code Manager Contract",
        description = "Contract used to manage one-time-codes"
    ),
    transactionSerializer = "it.unibo.ds.chainvote.transaction.TransactionSerializer"
)
public final class CodesManagerContract implements ContractInterface {

    static final String CODES_COLLECTION = "CodesCollection";
    private final CodeManager<Context> codeManager = new CodeManagerImpl<>(new LedgerRepository(), new HashGenerator());
    private final ElectionContract electionContract = new ElectionContract();

    private enum Error {
        INCORRECT_INPUT,
        ALREADY_GENERATED_CODE,
        ALREADY_INVALIDATED_CODE
    }

    /**
     * Generate a new one-time-code for the given election and user.
     * @param context the transaction context. A transient map is expected with the {@code userId} key-value entry.
     * @param electionId the election identifier
     * @param seed a random (non-deterministic) seed for the code generation
     * @return a {@link Response} json-object with the one time code.
     * @throws ChaincodeException with {@link Error#INCORRECT_INPUT} payload if the given election doesn't exist
     * or the seed is blank and {@link Error#ALREADY_GENERATED_CODE} payload if the given code is not valid.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Response<OneTimeCode> generateCodeFor(final Context context, final String electionId, final String seed) {
        final var userId = TransientUtils.getStringFromTransient(context.getStub().getTransient(), USER_ID.getKey());
        if (seed.isBlank()) {
            throw new ChaincodeException("Seed cannot be blank", Error.INCORRECT_INPUT.toString());
        } else if (!electionContract.electionExists(context, electionId)) {
            throw new ChaincodeException("The given election doesn't exists", Error.INCORRECT_INPUT.toString());
        }
        try {
            return Response.success(codeManager.generateCodeFor(context, electionId, userId, seed + userId));
        } catch (AlreadyGeneratedCodeException exception) {
            throw new ChaincodeException(exception.getMessage(), Error.ALREADY_GENERATED_CODE.toString());
        }
    }

    /**
     * Check if the given code is still valid, i.e. has not been consumed yet for the given election.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value entries: {@code userId} and {@code code}.
     * @param electionId the election identifier
     * @return a {@link Response} json object whose result is true if the given code is still valid, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Response<Boolean> isValid(final Context context, final String electionId) {
        final Pair<String, String> codeUserPair = UserCodeData.getUserCodePairFrom(context.getStub().getTransient());
        return Response.success(
            codeManager.isValid(context, electionId, codeUserPair.first(), codeUserPair.second())
        );
    }

    /**
     * Invalidate the given code for the given election passed in a transient map.
     * After calling this method the code can no longer be used.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: {@code userId} and {@code code}.
     * @param electionId the election identifier
     * @throws ChaincodeException with {@link Error#ALREADY_INVALIDATED_CODE} payload if the given code was already
     * been invalidated and with {@link Error#INCORRECT_INPUT} if the given code is not valid.
     */
    @Transaction
    public Response<Boolean> invalidate(final Context context, final String electionId) {
        final Pair<String, String> codeUserPair = UserCodeData.getUserCodePairFrom(context.getStub().getTransient());
        try {
            codeManager.invalidate(context, electionId, codeUserPair.first(), codeUserPair.second());
            return Response.success(true);
        } catch (InvalidCodeException exception) {
            throw new ChaincodeException(exception.getMessage(), Error.ALREADY_INVALIDATED_CODE.toString());
        } catch (IncorrectCodeException exception) {
            throw new ChaincodeException(exception.getMessage(), Error.INCORRECT_INPUT.toString());
        }
    }

    /**
     * Verifies if the given code has been generated for the given user and election passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: {@code userId} and {@code code}.
     * @param electionId the election identifier
     * @return a json object whose {@code result} field is true if the given code is correct, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Response<Boolean> verifyCodeOwner(final Context context, final String electionId) {
        final Pair<String, String> codeUserPair = UserCodeData.getUserCodePairFrom(context.getStub().getTransient());
        return Response.success(
            codeManager.verifyCodeOwner(context, electionId, codeUserPair.first(), codeUserPair.second())
        );
    }

    private static class LedgerRepository implements CodeRepository<Context> {

        private final Genson genson = GensonUtils.create();

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
}

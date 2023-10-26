package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.utils.ArgsData;
import it.unibo.ds.chainvote.utils.Pair;
import it.unibo.ds.chainvote.utils.TransientUtils;
import it.unibo.ds.chainvote.assets.OneTimeCodeAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.chainvote.utils.UserCodeData;
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

import static it.unibo.ds.chainvote.utils.UserCodeData.USER_ID;

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
public final class CodesManagerContract implements ContractInterface {

    static final String CODES_COLLECTION = "CodesCollection";
    private final CodeManager<Context> codeManager = new CodeManagerImpl<>(new LedgerRepository(), new HashGenerator());

    private enum Error {
        INVALID_INPUT,
        ALREADY_GENERATED_CODE,
        ALREADY_INVALIDATED_CODE
    }

    /**
     * Generate a new one-time-code for the given user and election passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: {@link UserCodeData#USER_ID}.
     * @param electionId the election identifier
     * @param seed a random (non-deterministic) seed for the code generation
     * @return the code asset.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String generateCodeFor(final Context context, final String electionId, final String seed) {
        final var userId = TransientUtils.getStringFromTransient(context.getStub().getTransient(), USER_ID.getKey());
        if (!electionExists(context, electionId)) {
            throw new ChaincodeException("The given election doesn't exists", Error.INVALID_INPUT.toString());
        }
        try {
            return codeManager.generateCodeFor(context, electionId, userId, seed).getCode();
        } catch (AlreadyGeneratedCodeException exception) {
            throw new ChaincodeException(exception.getMessage(), Error.ALREADY_GENERATED_CODE.toString());
        }
    }

    private boolean electionExists(final Context context, final String electionId) {
        final String electionSerialized = context.getStub().getStringState(electionId);
        return (electionSerialized != null && !electionSerialized.isBlank());
    }

    /**
     * Check if the given code is still valid, i.e. has not been consumed yet for the given election.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: {@link UserCodeData#USER_ID} and {@link UserCodeData#CODE}.
     * @param electionId the election identifier
     * @return true if the given code is still valid, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(final Context context, final String electionId) {
        final Pair<String, String> codeUserPair = UserCodeData.getUserCodePairFrom(context.getStub().getTransient());
        return codeManager.isValid(context, electionId, codeUserPair._1(), new OneTimeCodeImpl(codeUserPair._2()));
    }

    /**
     * Invalidate the given code for the given election passed in a transient map.
     * After calling this method the code can no longer be used.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: {@link UserCodeData#USER_ID} and {@link UserCodeData#CODE}.
     * @param electionId the election identifier
     */
    @Transaction
    public void invalidate(final Context context, final String electionId) {
        final Pair<String, String> codeUserPair = UserCodeData.getUserCodePairFrom(context.getStub().getTransient());
        try {
            codeManager.invalidate(context, electionId, codeUserPair._1(), new OneTimeCodeImpl(codeUserPair._2()));
        } catch (AlreadyConsumedCodeException exception) {
            throw new ChaincodeException(exception.getMessage(), Error.ALREADY_INVALIDATED_CODE.toString());
        } catch (NotValidCodeException exception) {
            throw new ChaincodeException(exception.getMessage(), Error.INVALID_INPUT.toString());
        }
    }

    /**
     * Verifies if the given code has been generated for the given user and election passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs:  {@link UserCodeData#USER_ID} and {@link UserCodeData#CODE}.
     * @param electionId the election identifier
     * @return true if the given code is correct, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean verifyCodeOwner(final Context context, final String electionId) {
        final Pair<String, String> codeUserPair = UserCodeData.getUserCodePairFrom(context.getStub().getTransient());
        return codeManager.verifyCodeOwner(context, electionId, codeUserPair._1(), new OneTimeCodeImpl(codeUserPair._2()));
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

package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.SerializersUtils;
import it.unibo.ds.chainvote.TransientUtils;
import it.unibo.ds.chainvote.asset.OneTimeCodeAsset;
import it.unibo.ds.chainvote.codes.AlreadyGeneratedCodeException;
import it.unibo.ds.chainvote.codes.CodesManager;
import it.unibo.ds.chainvote.codes.CodesManagerImpl;
import it.unibo.ds.chainvote.codes.CodesRepository;
import it.unibo.ds.chainvote.codes.HashGenerator;
import it.unibo.ds.chainvote.codes.IncorrectCodeException;
import it.unibo.ds.chainvote.codes.InvalidCodeException;
import it.unibo.ds.chainvote.codes.OneTimeCode;
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

import static it.unibo.ds.chainvote.utils.UserCodeData.SEED;
import static it.unibo.ds.chainvote.utils.UserCodeData.USER_ID;

/**
 * <p>A Hyperledger Fabric contract to manage one-time-codes.</p>
 * <p>
 *   The API Gateway client will receive the transaction returned values wrapped inside a
 *   <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">
 *       Response json object
 *   </a>.
 * </p>
 */
@Contract(
    name = "CodesManagerContract",
    info = @Info(
        title = "Code Manager Contract",
        description = "Contract used to manage one-time-codes"
    ),
    transactionSerializer = "it.unibo.ds.chainvote.TransactionSerializer"
)
public final class CodesManagerContract implements ContractInterface {

    static final String CODES_COLLECTION = "CodesCollection";
    private final CodesManager<Context> codesManager = new CodesManagerImpl<>(new LedgerRepository(), new HashGenerator());
    private final ElectionContract electionContract = new ElectionContract();
    private enum Error {
        INCORRECT_INPUT,
        ALREADY_GENERATED_CODE,
        ALREADY_INVALIDATED_CODE
    }

    /**
     * Generate a new one-time-code for the given election and user.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: {@code userId} adn {@code seed}.
     * @param electionId the election identifier
     * @return a string representation of the generated one-time-code.
     * @throws ChaincodeException with:
     * <ul>
     *     <li>{@code INCORRECT_INPUT} payload if the given election doesn't exist or the seed is blank</li>
     *     <li>{@code ALREADY_GENERATED_CODE} payload if a code for the given election and user has already been generated</li>
     * </ul>
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String generateCodeFor(final Context context, final String electionId) {
        final var userId = TransientUtils.getStringFromTransient(context.getStub().getTransient(), USER_ID.getKey());
        final var seed = TransientUtils.getStringFromTransient(context.getStub().getTransient(), SEED.getKey());
        if (seed.isBlank()) {
            throw new ChaincodeException("Seed cannot be blank", Error.INCORRECT_INPUT.toString());
        } else if (!electionContract.electionExists(context, electionId)) {
            throw new ChaincodeException("The given election doesn't exists", Error.INCORRECT_INPUT.toString());
        }
        try {
            return codesManager.generateCodeFor(context, electionId, userId, seed).getCode();
        } catch (AlreadyGeneratedCodeException exception) {
            throw new ChaincodeException(exception.getMessage(), Error.ALREADY_GENERATED_CODE.toString());
        }
    }

    /**
     * Check if the given code is still valid, i.e. has not been consumed yet for the given election.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value entries: {@code userId} and {@code code}.
     * @param electionId the election identifier
     * @return true if the given code is still valid, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(final Context context, final String electionId) {
        final Pair<String, String> codeUserPair = UserCodeData.getUserCodePairFrom(context.getStub().getTransient());
        return codesManager.isValid(context, electionId, codeUserPair.first(), codeUserPair.second());
    }

    /**
     * Invalidate the given code for the given election passed in a transient map.
     * After calling this method the code can no longer be used.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: {@code userId} and {@code code}.
     * @param electionId the election identifier
     * @return the result outcome.
     * @throws ChaincodeException with
     * <ul>
     *   <li>{@code ALREADY_INVALIDATED_CODE} payload if the given code has already been invalidated</li>
     *   <li>{@code INCORRECT_INPUT} payload if the given code is not valid anymore</li>
     * </ul>
     */
    @Transaction
    public boolean invalidate(final Context context, final String electionId) {
        final Pair<String, String> codeUserPair = UserCodeData.getUserCodePairFrom(context.getStub().getTransient());
        try {
            codesManager.invalidate(context, electionId, codeUserPair.first(), codeUserPair.second());
            return true;
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
     * @return true if the given code is correct, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean verifyCodeOwner(final Context context, final String electionId) {
        final Pair<String, String> codeUserPair = UserCodeData.getUserCodePairFrom(context.getStub().getTransient());
        return codesManager.verifyCodeOwner(context, electionId, codeUserPair.first(), codeUserPair.second());
    }

    private static final class LedgerRepository implements CodesRepository<Context> {

        private final Genson genson = SerializersUtils.gensonInstance();

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

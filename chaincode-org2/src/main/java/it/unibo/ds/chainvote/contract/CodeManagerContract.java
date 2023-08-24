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
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.*;
import java.util.stream.Collectors;

import static it.unibo.ds.chainvote.utils.TransientUtils.getLongFromTransient;
import static it.unibo.ds.chainvote.utils.TransientUtils.getStringFromTransient;

/**
 * A Hyperledger Fabric contract to manage one-time-codes.
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
     * @param context the transaction context. A transient map is expected with the following
     *               key-value pairs: `userId` and `electionId`.
     * @return the code asset.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public OneTimeCodeAsset generateFor(final Context context) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String userId = getStringFromTransient(transientMap, "userId");
        final String electionId = getStringFromTransient(transientMap, "electionId");
        try {
            return new OneTimeCodeAsset(codeManager.generateFor(context, electionId, userId), userId, electionId);
        } catch (IllegalStateException exception) {
            throw new ChaincodeException(exception.getMessage(), CodeManagerErrors.ALREADY_GENERATED_CODE.toString());
        }
    }

    /**
     * Check if the given code is still valid, i.e. has not been consumed yet for the given election
     * passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId` and `code`.
     * @return true if the given code is still valid, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(final Context context) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String electionId = getStringFromTransient(transientMap, "electionId");
        final Long code = getLongFromTransient(transientMap, "code");
        return codeManager.isValid(context, electionId, new OneTimeCodeImpl(code));
    }

    /**
     * Invalidate the given code for the given election passed in a transient map.
     * After calling this method the code can no longer be used.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId` and `code`.
     */
    @Transaction
    public void invalidate(final Context context) {
        final Map<String, byte[]> transientMap = context.getStub().getTransient();
        final String electionId = getStringFromTransient(transientMap, "electionId");
        final Long code = getLongFromTransient(transientMap, "code");
        codeManager.invalidate(context, electionId, new OneTimeCodeImpl(code));
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
        final String userId = getStringFromTransient(transientMap, "userId");
        final String electionId = getStringFromTransient(transientMap, "electionId");
        final Long code = getLongFromTransient(transientMap, "code");
        return codeManager.verifyCodeOwner(context, electionId, userId, new OneTimeCodeImpl(code));
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public OneTimeCodeAsset[] queryElection(final Context context, final String electionId) {
        final ChaincodeStub stub = context.getStub();
        final Set<OneTimeCodeAsset> queryResults = new HashSet<>();
        final String queryString = "{\"selector\":{\"electionId\":\"" + electionId + "\"}}";
        try (final QueryResultsIterator<KeyValue> results = stub.getPrivateDataQueryResult(CODES_COLLECTION, queryString)) {
            for (final KeyValue result : results) {
                if (result.getStringValue() == null || result.getStringValue().length() == 0) {
                    System.err.printf("Invalid Asset json: %s\n", result.getStringValue());
                    continue;
                }
                final OneTimeCodeAsset asset = genson.deserialize(result.getStringValue(), OneTimeCodeAsset.class);
                queryResults.add(asset);
                System.out.println("QueryResult: " + asset.toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        System.out.println("[QUERY] ENDED");
        final OneTimeCodeAsset[] returnValue = new OneTimeCodeAsset[queryResults.size()];
        return queryResults.toArray(returnValue);
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
        return Optional.ofNullable(data).map(OneTimeCodeAsset::getAsset);
    }

    @Override
    public void put(final Context context, final String electionId, final String userId, final OneTimeCode code) {
        System.out.println("[PUT] election: " + electionId + " - user: " + userId + " - code: " + code);
        context.getStub().putPrivateData(
            CODES_COLLECTION,
            new CompositeKey(electionId, userId).toString(),
            genson.serialize(new OneTimeCodeAsset(code, userId, electionId))
        );
    }

    @Override
    public void replace(final Context context, final String electionId, final OneTimeCode code) {
        // TODO implement
    }

    @Override
    public Set<OneTimeCode> getAllOf(final Context context, final String electionId) {
        return Set.of();
//        return getQueryResults(context, electionId).stream()
//            .map(OneTimeCodeAsset::getAsset)
//            .collect(Collectors.toSet());
    }
}

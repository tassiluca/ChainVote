package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.OTCAsset;
import it.unibo.ds.chainvote.assets.OTCAssetPrivateDetails;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.*;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        ALREADY_GENERATED_CODES,
        WRONG_BIND
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean nopeTransaction(final Context context) {
        return true;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean testInvokeOtherTransaction(final Context context) {
        final Chaincode.Response response = context.getStub().invokeChaincodeWithStringArgs(
            "chaincode-org2",
            "CodeManagerContract:nopeTransaction"
        );
        return !response.getStringPayload().isBlank();
    }

    /**
     * Generate all the one-time-codes for the given election.
     * @param context the transaction context.
     * @param electionId the election identifier.
     * @param votersNumber the number of voters, i.e. the number of codes to be generated.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void generateAllFor(final Context context, final String electionId, final long votersNumber) {
        // TODO: check if the given `electionId` is valid
        // TODO: replace votersNumber with a cross-contract and cross-ledger invocation?
        // TODO: allow to be called only by administrators
        try {
            codeManager.generateAllFor(context, electionId, votersNumber);
        } catch (IllegalStateException exception) {
            throw new ChaincodeException(exception.getMessage(), CodeManagerErrors.ALREADY_GENERATED_CODES.toString());
        }
//        final Map<String, byte[]> transientMap = context.getStub().getTransient();
//        final String userId = getStringFromTransient(transientMap, "userId");
//        final String electionId = getStringFromTransient(transientMap, "electionId");
//        try {
//            return new OneTimeCodeAsset(codeManager.generateFor(context, electionId, userId), userId, electionId);
//        } catch (IllegalStateException exception) {
//            throw new ChaincodeException(exception.getMessage(), CodeManagerErrors.ALREADY_GENERATED_CODE.toString());
//        }
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
//        final Map<String, byte[]> transientMap = context.getStub().getTransient();
//        final String electionId = getStringFromTransient(transientMap, "electionId");
//        final Long code = getLongFromTransient(transientMap, "code");
//        return codeManager.isValid(context, electionId, new OneTimeCodeImpl(code));
        return false;
    }

    /**
     * Invalidate the given code for the given election passed in a transient map.
     * After calling this method the code can no longer be used.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId` and `code`.
     */
    @Transaction
    public void invalidate(final Context context) {
//        final Map<String, byte[]> transientMap = context.getStub().getTransient();
//        final String electionId = getStringFromTransient(transientMap, "electionId");
//        final Long code = getLongFromTransient(transientMap, "code");
//        codeManager.invalidate(context, electionId, new OneTimeCodeImpl(code));
    }

    /**
     * Verifies if the given code has been generated for the given user and election passed in a transient map.
     * @param context the transaction context. A transient map is expected with the following
     *                key-value pairs: `electionId`, `userId` and `code`
     * @return true if the given code is correct, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean verifyCodeOwner(final Context context) {
//        final Map<String, byte[]> transientMap = context.getStub().getTransient();
//        final String userId = getStringFromTransient(transientMap, "userId");
//        final String electionId = getStringFromTransient(transientMap, "electionId");
//        final Long code = getLongFromTransient(transientMap, "code");
//        return codeManager.verifyCodeOwner(context, electionId, userId, new OneTimeCodeImpl(code));
        return false;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public OTCAssetPrivateDetails[] queryElection(final Context context, final String electionId) {
        final ChaincodeStub stub = context.getStub();
        final Set<OTCAssetPrivateDetails> queryResults = new HashSet<>();
        final String queryString = "{\"selector\":{\"electionId\":\"" + electionId + "\"}}";
        try (final QueryResultsIterator<KeyValue> results = stub.getPrivateDataQueryResult(CODES_COLLECTION, queryString)) {
            for (final KeyValue result : results) {
                if (result.getStringValue() == null || result.getStringValue().length() == 0) {
                    System.err.printf("Invalid Asset json: %s\n", result.getStringValue());
                    continue;
                }
                final OTCAssetPrivateDetails asset = genson.deserialize(result.getStringValue(), OTCAssetPrivateDetails.class);
                queryResults.add(asset);
                System.out.println("QueryResult: " + asset.toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        System.out.println("[QUERY] ENDED");
        final OTCAssetPrivateDetails[] returnValue = new OTCAssetPrivateDetails[queryResults.size()];
        return queryResults.toArray(returnValue);
    }

    @Override
    public Optional<OneTimeCode> get(final Context context, final String electionId, final String userId) {
//        final OneTimeCodeAsset data = genson.deserialize(
//            context.getStub().getPrivateData(
//                CODES_COLLECTION,
//                new CompositeKey(electionId, userId).toString()
//            ),
//            OneTimeCodeAsset.class
//        );
//        return Optional.ofNullable(data).map(OneTimeCodeAsset::getAsset);
        return Optional.empty();
    }

    @Override
    public void put(final Context context, final String electionId, final String userId, final OneTimeCode code) {
        System.out.println("[PUT PVD] election: " + electionId + " - user: " + userId + " - code: " + code);
//        context.getStub().putPrivateData(
//            CODES_COLLECTION,
//            new CompositeKey(electionId, userId).toString(),
//            genson.serialize(new OneTimeCodeAsset(code, userId, electionId))
//        );
    }

    @Override
    public void put(Context context, String electionId, OneTimeCode code) {
        System.out.println("[PUT] election: " + electionId + " - code: " + code);
        context.getStub().putStringState(
            new CompositeKey(electionId, code.getCode().toString()).toString(),
            OTCAsset.serialize(new OTCAsset(electionId, code.getCode()))
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

package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.chainvote.utils.ArgsData;
import it.unibo.ds.chainvote.utils.Pair;
import it.unibo.ds.chainvote.utils.UserCodeData;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.BallotImpl;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.factory.ElectionFactory;
import it.unibo.ds.core.manager.ElectionManagerImpl;
import it.unibo.ds.core.utils.Choice;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contracts managing {@link Election}.
 */
@Contract(
    name = "ElectionContract",
    info = @Info(
        title = "Election Contract",
        description = "Contract used to manage election"
    ),
    transactionSerializer = "it.unibo.ds.chainvote.transaction.TransactionSerializer"
)

@Default
public final class ElectionContract implements ContractInterface {

    private static final String CHANNEL_INFO_NAME_CH1 = "ch1";
    private static final String CHAINCODE_INFO_NAME_CH1 = "chaincode-org1";
    private final Genson genson = GensonUtils.create();

    private enum ElectionContractErrors {
        ELECTION_NOT_FOUND,
        ELECTION_ALREADY_EXISTS,
        ELECTION_INFO_NOT_FOUND,
        ELECTION_INVALID_CREDENTIALS_TO_CAST_VOTE,
        ELECTION_INVALID_BUILD_ARGUMENT,
        ELECTION_INVALID_BALLOT_ARGUMENTS,
        ELECTION_INVALID_BALLOT_CAST_ARGUMENTS
    }

    /**
     * Create an {@link Election}.
     * @param ctx The {@link Context}.
     * @param electionId The id of the {@link ElectionInfo} and the {@link Election}.
     * @param results The initial results of the {@link Election} to create.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createElection(final Context ctx, final String electionId, final Map<Choice, Long> results) {
        System.out.println("[EC] createElection");
        ChaincodeStub stub = ctx.getStub();
        if (electionExists(ctx, electionId)) {
            String errorMessage = String.format("Election %s already exists", electionId);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_ALREADY_EXISTS.toString());
        }
        ElectionInfo electionInfo = null;
        try {
            electionInfo = readElectionInfo(ctx, electionId);
        } catch (JsonBindingException e) {
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.ELECTION_INFO_NOT_FOUND.toString());
        }
        try {
            Election election = ElectionFactory
                .buildElection(electionInfo, results);
            String sortedJson = genson.serialize(election);
            stub.putStringState(electionId, sortedJson);
            // TODO check if it's the right exception
        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.ELECTION_INVALID_BUILD_ARGUMENT.toString());
        }
    }

    /**
     * Return the {@link Election}.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to retrieve.
     * @return the {@link Election}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Election readElection(final Context ctx, String electionId) {
        System.out.println("[EC] readElectionAsset");
        if (electionExists(ctx, electionId)) {
            ChaincodeStub stub = ctx.getStub();
            String electionSerialized = stub.getStringState(electionId);
            return genson.deserialize(electionSerialized, Election.class);
        } else {
            String errorMessage = String.format("Election %s does not exist", electionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }
    }

    /**
     * Return the {@link ElectionInfo}.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link ElectionInfo} to retrieve.
     * @return the {@link ElectionInfo}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    private ElectionInfo readElectionInfo(final Context ctx, final String electionId) {
        System.out.println("[EC] readElectionInfo");
        Chaincode.Response response = ctx.getStub().invokeChaincodeWithStringArgs(
                CHAINCODE_INFO_NAME_CH1,
                List.of("ElectionInfoContract:readElectionInfo", ArgsData.ELECTION_ID.getKey() + ":" + electionId),
                CHANNEL_INFO_NAME_CH1
        );
        String electionDeserialized = response.getStringPayload();
        return genson.deserialize(electionDeserialized, ElectionInfo.class);
    }

    /**
     * Cast a vote in an existing {@link Election}.
     * @param ctx the {@link Context}.  A transient map is expected with the following
     *        key-value pairs: {@link UserCodeData#USER_ID} and {@link UserCodeData#CODE}..
     * @param choice the {@link Choice} of the vote.
     * @param electionId the id of the {@link Election} where the vote is cast.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void castVote(final Context ctx, Choice choice, String electionId) {
        System.out.println("[EC] castVote");
        final Pair<String, Long> userCodePair = UserCodeData.getUserCodePairFrom(ctx.getStub().getTransient());

        if (!electionExists(ctx, electionId)) {
            String errorMessage = String.format("Election %s does not exist", electionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }

        CodesManagerContract cmc = new CodesManagerContract();

        if (!cmc.isValid(ctx, electionId)) {
            String errorMessage = "The given one-time-code is not valid.";
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_INVALID_CREDENTIALS_TO_CAST_VOTE.toString());
        }

        Ballot ballot = null;
        try {
            ballot = new BallotImpl.Builder().electionID(electionId)
                .voterID(userCodePair._1())
                .date(LocalDateTime.now())
                .choice(choice)
                .build();
        // TODO check if it's the right exception
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.ELECTION_INVALID_BALLOT_ARGUMENTS.toString());
        }
        ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        Election election = readElection(ctx, electionId);
        try {
            ElectionManagerImpl.getInstance().castVote(election, electionInfo, ballot);
            // TODO check if it's the right exception
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.ELECTION_INVALID_BALLOT_CAST_ARGUMENTS.toString());
        }
        cmc.invalidate(ctx, electionId);
        String electionSerialized = genson.serialize(election);
        ctx.getStub().putStringState(electionId, electionSerialized);
    }

    /**
     * Delete an {@link Election}.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to delete.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteElection(final Context ctx, final String electionId) {
        System.out.println("[EC] deleteAsset");
        ChaincodeStub stub = ctx.getStub();
        if (!electionExists(ctx, electionId)) {
            String errorMessage = String.format("Election %s does not exist", electionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }
        stub.delState(electionId);
    }

    /**
     * Check if an {@link Election} exists.
     * @param ctx the {@link Context}.
     * @param electionId the {@link Election}'s id.
     * @return a boolean representing if the {@link Election} exists.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    private boolean electionExists(final Context ctx, final String electionId) {
        System.out.println("[EC] electionExists");
        ChaincodeStub stub = ctx.getStub();
        String electionSerialized = stub.getStringState(electionId);
        return (electionSerialized != null && !electionSerialized.isEmpty());
    }

    /**
     * Return all the existing {@link Election}s.
     * @param ctx the {@link Context}.
     * @return the {@link Election}s serialized.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getAllElection(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<Election> queryResults = new ArrayList<Election>();
        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");
        for (KeyValue result: results) {
            Election election = genson.deserialize(result.getStringValue(), Election.class);
            // System.out.println(election);
            queryResults.add(election);
        }
        return genson.serialize(queryResults);
    }
}

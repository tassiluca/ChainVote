package it.unibo.ds.chainvote.contract;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.chainvote.presentation.SerializerCustomUtils;
import it.unibo.ds.chainvote.presentation.GensonUtils;
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
    private static final CodesManagerContract CODES_MANAGER_CONTRACT = new CodesManagerContract();

    private enum ElectionContractErrors {
        ELECTION_NOT_FOUND,
        ELECTION_ALREADY_EXISTS,
        ELECTION_READ_WHILE_OPEN,
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
    public void createElection(final Context ctx, final String electionId, final Map<String, Long> results) {
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
    private Election readElection(final Context ctx, String electionId) {
        System.out.println("[EC] readElection");
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
     * Return the {@link Election} information of electionId and affluence.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to retrieve open information (electionId and affluence).
     * @return the {@link String} representing the {@link Election} serialized in order to get info about affluence.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String readOpenElection(final Context ctx, String electionId) {
        System.out.println("[EC] readOpenElection");
        Election election = readElection(ctx, electionId);
        ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        return SerializerCustomUtils.serializeElectionInfoWithResults(electionInfo, election);
    }

    /**
     * Return the {@link Election} if it's already closed.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to retrieve.
     * @return the {@link Election}.
     * @throws ChaincodeException in case {@link Election} retrieved is still open.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Election readClosedElection(final Context ctx, String electionId) {
        System.out.println("[EC] readClosedElection");
        Election election = readElection(ctx, electionId);
        ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        if (electionInfo.isOpen()) {
            String errorMessage = String.format("Election %s is still open", electionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_READ_WHILE_OPEN.toString());
        }
        return election;
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
                List.of("ElectionInfoContract:readElectionInfo", electionId),
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

        final Pair<String, String> userCodePair = UserCodeData.getUserCodePairFrom(ctx.getStub().getTransient());

        if (!electionExists(ctx, electionId)) {
            String errorMessage = String.format("Election %s does not exist", electionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }

        if (!CODES_MANAGER_CONTRACT.isValid(ctx, electionId)) {
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
        CODES_MANAGER_CONTRACT.invalidate(ctx, electionId);
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
    public List<String> getAllElection(final Context ctx) {
        System.out.println("[EC] getAllElection");

        List<String> electionsSerialized = new ArrayList<>();

        Chaincode.Response response = ctx.getStub().invokeChaincodeWithStringArgs(
                CHAINCODE_INFO_NAME_CH1,
                List.of("ElectionInfoContract:getAllElectionInfo"),
                CHANNEL_INFO_NAME_CH1
        );
        String electionInfosSerialized = response.getStringPayload();
        System.out.println("[EC] getAllElection response from GAEI: " + electionInfosSerialized);
        List<ElectionInfo> electionInfos = GensonUtils.create().deserialize(electionInfosSerialized, new GenericType<>() { });

        for (ElectionInfo electionInfo : electionInfos) {
            String electionId = electionInfo.getElectionId();
            Election election = this.readElection(ctx, electionId);
            electionsSerialized.add(SerializerCustomUtils.serializeElectionInfoWithResults(electionInfo, election));
        }

        System.out.println("[EC] getAllElection results serialized: " + genson.serialize(electionsSerialized));

        return electionsSerialized;
    }
}

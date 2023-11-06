package it.unibo.ds.chainvote.contract;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import it.unibo.ds.chainvote.GensonUtils;
import it.unibo.ds.chainvote.Response;
import it.unibo.ds.chainvote.SerializersUtils;
import it.unibo.ds.chainvote.facades.ElectionFacade;
import it.unibo.ds.chainvote.facades.ElectionFacadeImpl;
import it.unibo.ds.chainvote.facades.ElectionCompleteFacade;
import it.unibo.ds.chainvote.facades.ElectionCompleteFacadeImpl;
import it.unibo.ds.chainvote.utils.UserCodeData;
import it.unibo.ds.chainvote.assets.Ballot;
import it.unibo.ds.chainvote.assets.BallotImpl;
import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.factory.ElectionFactory;
import it.unibo.ds.chainvote.manager.ElectionManagerImpl;
import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.Pair;
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
    transactionSerializer = "it.unibo.ds.chainvote.TransactionSerializer"
)
@Default
public final class ElectionContract implements ContractInterface {

    private static final String CHANNEL_INFO_NAME_CH1 = "ch1";
    private static final String CHAINCODE_INFO_NAME_CH1 = "chaincode-org1";
    private final Genson genson = SerializersUtils.gensonInstance();
    private static final CodesManagerContract CODES_MANAGER_CONTRACT = new CodesManagerContract();

    private enum ElectionContractErrors {
        ELECTION_NOT_FOUND,
        ELECTION_ALREADY_EXISTS,
        ELECTION_READ_WHILE_OPEN,
        ELECTION_INFO_NOT_FOUND,
        ELECTION_INVALID_CREDENTIALS_TO_CAST_VOTE,
        ELECTION_INVALID_BUILD_ARGUMENT,
        ELECTION_INVALID_BALLOT_ARGUMENTS,
        ELECTION_INVALID_BALLOT_CAST_ARGUMENTS,
        CROSS_INVOCATION_FAILED
    }

    /**
     * Create an {@link Election}.
     * @param ctx The {@link Context}.
     * @param electionId The id of the {@link ElectionInfo} and the {@link Election}.
     * @param results The initial results of the {@link Election} to create.
     * @return the {@link Election} built.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Election createElection(final Context ctx, final String electionId, final Map<String, Long> results) {
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
            return election;
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
    private Election readStandardElection(final Context ctx, String electionId) {
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
     * Return the {@link ElectionCompleteFacade} related to {@link Election} and {@link ElectionInfo} labeled with
     * the given electionId.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to retrieve open information (electionId and affluence).
     * @return the {@link ElectionCompleteFacade}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionCompleteFacade readElection(final Context ctx, String electionId) {
        System.out.println("[EC] readOpenElection");
        Election election = readStandardElection(ctx, electionId);
        ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        return new ElectionCompleteFacadeImpl(election, electionInfo);
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
        if (response.getStatus().equals(Chaincode.Response.Status.SUCCESS)) {
            final Response<ElectionInfo> responsePayload = genson.deserialize(response.getStringPayload(), new GenericType<>() {});
            return responsePayload.getResult();
        } else {
            final String errorMessage = "Something went wrong with cross invocation call.";
            throw new ChaincodeException(errorMessage, ElectionContractErrors.CROSS_INVOCATION_FAILED.toString());
        }
    }

    /**
     * Cast a vote in an existing {@link Election}.
     * @param ctx the {@link Context}.  A transient map is expected with the following
     *        key-value pairs: {@link UserCodeData#USER_ID} and {@link UserCodeData#CODE}..
     * @param choice the {@link Choice} of the vote.
     * @param electionId the id of the {@link Election} where the vote is cast.
     * @return the {@link Boolean} representing the successfulness of the operation.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean castVote(final Context ctx, Choice choice, String electionId) {
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
        Election election = readStandardElection(ctx, electionId);
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
        return true;
    }

    /**
     * Delete an {@link Election}.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to delete.
     * @return the {@link Election} deleted.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)

    public Election deleteElection(final Context ctx, final String electionId) {
        System.out.println("[EC] deleteAsset");
        ChaincodeStub stub = ctx.getStub();
        if (!electionExists(ctx, electionId)) {
            final String errorMessage = String.format("Election %s does not exist", electionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }

        Election election = readStandardElection(ctx, electionId);

        stub.delState(electionId);
        return election;
    }

    /**
     * Check if an {@link Election} exists.
     * @param ctx the {@link Context}.
     * @param electionId the {@link Election}'s id.
     * @return a boolean representing if the {@link Election} exists.
     */
    public boolean electionExists(final Context ctx, final String electionId) {
        System.out.println("[EC] electionExists");
        ChaincodeStub stub = ctx.getStub();
        final String electionSerialized = stub.getStringState(electionId);
        return (electionSerialized != null && !electionSerialized.isEmpty());
    }

    /**
     * Return all the existing {@link Election}s.
     * @param ctx the {@link Context}.
     * @return the {@link Election}s serialized.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public List<ElectionFacade> getAllElection(final Context ctx) {
        System.out.println("[EC] getAllElection");
        List<ElectionFacade> allElections = new ArrayList<>();
        Chaincode.Response response = ctx.getStub().invokeChaincodeWithStringArgs(
                CHAINCODE_INFO_NAME_CH1,
                List.of("ElectionInfoContract:getAllElectionInfo"),
                CHANNEL_INFO_NAME_CH1
        );
        if (response.getStatus().equals(Chaincode.Response.Status.SUCCESS)) {
            final Response<List<ElectionInfo>> responsePayload = genson.deserialize(response.getStringPayload(), new GenericType<>() {
            });
            final List<ElectionInfo> electionInfos = responsePayload.getResult();
            System.out.println("[EC] getAllElection response from GAEI: " + electionInfos);
            for (ElectionInfo electionInfo : electionInfos) {
                String electionId = electionInfo.getElectionId();
                if (electionExists(ctx, electionId)) {
                    Election election = this.readStandardElection(ctx, electionId);
                    allElections.add(new ElectionFacadeImpl(election, electionInfo));
                }
            }
            System.out.println("[EC] getAllElection results serialized: " + genson.serialize(allElections));
            return allElections;
        } else {
            final String errorMessage = "Something went wrong with cross invocation call.";
            throw new ChaincodeException(errorMessage, ElectionContractErrors.CROSS_INVOCATION_FAILED.toString());
        }
    }
}

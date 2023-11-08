package it.unibo.ds.chainvote.contract;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.Response;
import it.unibo.ds.chainvote.SerializersUtils;
import it.unibo.ds.chainvote.assets.Ballot;
import it.unibo.ds.chainvote.assets.BallotImpl;
import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.facade.ElectionFacade;
import it.unibo.ds.chainvote.facade.ElectionFacadeImpl;
import it.unibo.ds.chainvote.factory.ElectionFactory;
import it.unibo.ds.chainvote.manager.ElectionManagerImpl;
import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.Pair;
import it.unibo.ds.chainvote.utils.UserCodeData;
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
 * A Hyperledger Contract to manage {@link Election}s.
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
        INVALID_CREDENTIALS_TO_CAST_VOTE,
        ELECTION_INVALID_BUILD_ARGUMENT,
        INVALID_BALLOT_BUILD_ARGUMENTS,
        INVALID_BALLOT_CAST_ARGUMENTS,
        CROSS_INVOCATION_FAILED
    }

    /**
     * Create an {@link Election}.
     * Expected JSON input in the following format:
     * {
     *      "function": "ElectionContract:createElection",
     *      "Args": [
     *          "your_election_id",
     *          {["{"choice":"your_choiceN"}":n]*[,"{"choice":"your_choiceM"}":m]}
     *      ]
     * }
     * Constraints: n >= 0, m >= 0, String must be non-empty.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link ElectionInfo} and the {@link Election}.
     * @param results the initial results of the {@link Election} to create.
     * @return the {@link Election} built.
     * @throws ChaincodeException with {@link ElectionContractErrors#ELECTION_ALREADY_EXISTS} as payload if it has
     * already been created an {@link Election} with the same electionId
     * with {@link ElectionContractErrors#ELECTION_INVALID_BUILD_ARGUMENT} as payload if at least one of the given arguments
     * is not valid and {@link ElectionContractErrors#ELECTION_INFO_NOT_FOUND} as payload if the {@link ElectionInfo}
     * labeled by electionId hasn't been created.
     * @see <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">Response json object</a>
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Election createElection(final Context ctx, final String electionId, final Map<String, Long> results) {
        ChaincodeStub stub = ctx.getStub();
        if (electionExists(ctx, electionId)) {
            String errorMessage = String.format("Election %s already exists", electionId);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_ALREADY_EXISTS.toString());
        }
        ElectionInfo electionInfo;
        try {
            electionInfo = readElectionInfo(ctx, electionId);
        } catch (ChaincodeException e) {
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.ELECTION_INFO_NOT_FOUND.toString());
        }
        try {
            Election election = ElectionFactory
                .buildElection(electionInfo, results);
            String sortedJson = genson.serialize(election);
            stub.putStringState(electionId, sortedJson);
            return election;
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.ELECTION_INVALID_BUILD_ARGUMENT.toString());
        }
    }

    /**
     * Return the {@link Election}.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to retrieve.
     * @return the {@link Election}.
     * @throws ChaincodeException with  {@link ElectionContractErrors#ELECTION_INFO_NOT_FOUND} as payload if the
     * {@link Election} labeled by electionId hasn't been created.
     * @see <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">Response json object</a>
     */
    private Election readStandardElection(final Context ctx, String electionId) {
        if (electionExists(ctx, electionId)) {
            ChaincodeStub stub = ctx.getStub();
            String electionSerialized = stub.getStringState(electionId);
            return genson.deserialize(electionSerialized, Election.class);
        } else {
            String errorMessage = String.format("Election %s does not exist", electionId);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }
    }

    /**
     * Return the {@link ElectionFacade} related to {@link Election} and {@link ElectionInfo} labeled with
     * the given electionId. If the {@link Election} is still open, {@link Map} representing its results will be empty.
     * Expected JSON input in the following format:
     * {
     *      "function": "ElectionContract:readElection",
     *      "Args": [
     *          "your_election_id"
     *      ]
     * }
     * Constraints: String must be non-empty.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to retrieve open information (electionId and affluence).
     * @return the {@link ElectionFacade}.
     * @see <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">Response json object</a>
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionFacade readElection(final Context ctx, String electionId) {
        Election election = readStandardElection(ctx, electionId);
        ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        return new ElectionFacadeImpl(election, electionInfo);
    }

    /**
     * Return the {@link ElectionInfo}.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link ElectionInfo} to retrieve.
     * @return the {@link ElectionInfo}.
     * @throws ChaincodeException with  {@link ElectionContractErrors#CROSS_INVOCATION_FAILED} as payload if something
     * went wrong during cross channel invocation.
     * @see <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">Response json object</a>
     */
    private ElectionInfo readElectionInfo(final Context ctx, final String electionId) {
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
     * Expected JSON input in the following format:
     * {
     *      "function": "ElectionContract:castVote",
     *      "Args": [
     *          "{"choice":"your_choice"}",
     *          "your_election_id"
     *      ]
     * }
     * Expected transient map:
     * {
     *      "userId":"your_user_id",
     *      "code":"your_code"
     * }
     * Constraints: String must be non-empty.
     * Transient String values must be converted in base64 bytes.
     * @param ctx the {@link Context}.  A transient map is expected with the following
     *        key-value pairs: {@link UserCodeData#USER_ID} and {@link UserCodeData#CODE}.
     * @param choice the {@link Choice} of the vote.
     * @param electionId the id of the {@link Election} where the vote is cast.
     * @return the {@link Boolean} representing the successfulness of the operation.
     * @throws ChaincodeException with {@link ElectionContractErrors#ELECTION_NOT_FOUND} as payload if the {@link Election}
     * in which the vote should be cast doesn't exist, {@link ElectionContractErrors#INVALID_CREDENTIALS_TO_CAST_VOTE} as payload
     * in case {@link UserCodeData#USER_ID} or {@link UserCodeData#CODE} aren't valid, {@link ElectionContractErrors#INVALID_BALLOT_BUILD_ARGUMENTS}
     * in case the arguments used to build the {@link Ballot} are not valid, {@link ElectionContractErrors#INVALID_BALLOT_CAST_ARGUMENTS}
     * in case something went wrong with casting.
     * @see <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">Response json object</a>
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean castVote(final Context ctx, Choice choice, String electionId) {
        final Pair<String, String> userCodePair = UserCodeData.getUserCodePairFrom(ctx.getStub().getTransient());

        if (!electionExists(ctx, electionId)) {
            String errorMessage = String.format("Election %s does not exist", electionId);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }

        if (!CODES_MANAGER_CONTRACT.isValid(ctx, electionId)) {
            String errorMessage = "The given one-time-code is not valid.";
            throw new ChaincodeException(errorMessage, ElectionContractErrors.INVALID_CREDENTIALS_TO_CAST_VOTE.toString());
        }

        Ballot ballot;
        try {
            ballot = new BallotImpl.Builder().electionID(electionId)
                .voterID(userCodePair._1())
                .date(LocalDateTime.now())
                .choice(choice)
                .build();
        } catch (IllegalArgumentException e) {
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.INVALID_BALLOT_BUILD_ARGUMENTS.toString());
        }
        ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        Election election = readStandardElection(ctx, electionId);
        try {
            ElectionManagerImpl.getInstance().castVote(election, electionInfo, ballot);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.INVALID_BALLOT_CAST_ARGUMENTS.toString());
        }
        CODES_MANAGER_CONTRACT.invalidate(ctx, electionId);
        String electionSerialized = genson.serialize(election);
        ctx.getStub().putStringState(electionId, electionSerialized);
        return true;
    }

    /**
     * Delete an {@link Election}.
     * Expected JSON input in the following format:
     * {
     *      "function": "ElectionContract:deleteElection",
     *      "Args": [
     *          "your_election_id"
     *      ]
     * }
     * Constraints: String must be non-empty.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to delete.
     * @return the {@link Election} deleted.
     * @throws ChaincodeException with {@link ElectionContractErrors#ELECTION_NOT_FOUND} as payload if the
     * {@link Election} labeled by electionId doesn't exist.
     * @see <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">Response json object</a>
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Election deleteElection(final Context ctx, final String electionId) {
        ChaincodeStub stub = ctx.getStub();
        if (!electionExists(ctx, electionId)) {
            final String errorMessage = String.format("Election %s does not exist", electionId);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }

        Election election = readStandardElection(ctx, electionId);

        stub.delState(electionId);
        return election;
    }

    /**
     * Check if an {@link Election} exists.
     * Expected JSON input in the following format:
     * {
     *      "function": "ElectionContract:electionExists",
     *      "Args": [
     *          "your_election_id"
     *      ]
     * }
     * @param ctx the {@link Context}.
     * @param electionId the {@link Election}'s id.
     * @return a boolean representing if the {@link Election} exists.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean electionExists(final Context ctx, final String electionId) {
        ChaincodeStub stub = ctx.getStub();
        final String electionSerialized = stub.getStringState(electionId);
        return (electionSerialized != null && !electionSerialized.isEmpty());
    }

    /**
     * Return all the existing {@link Election}s.
     * Expected JSON input in the following format:
     * {
     *      "function": "ElectionContract:getAllElection",
     *      "Args": []
     * }
     * @param ctx the {@link Context}.
     * @return the {@link Election}s serialized.
     * @throws ChaincodeException with  {@link ElectionContractErrors#CROSS_INVOCATION_FAILED} as payload if something
     * went wrong during cross channel invocation.
     * @see <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">Response json object</a>
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public List<ElectionFacade> getAllElection(final Context ctx) {
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
            for (ElectionInfo electionInfo : electionInfos) {
                String electionId = electionInfo.getElectionId();
                if (electionExists(ctx, electionId)) {
                    Election election = this.readStandardElection(ctx, electionId);
                    allElections.add(new ElectionFacadeImpl(election, electionInfo));
                }
            }
            return allElections;
        } else {
            final String errorMessage = "Something went wrong with cross invocation call.";
            throw new ChaincodeException(errorMessage, ElectionContractErrors.CROSS_INVOCATION_FAILED.toString());
        }
    }
}

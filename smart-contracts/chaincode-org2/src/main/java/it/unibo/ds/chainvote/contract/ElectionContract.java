package it.unibo.ds.chainvote.contract;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>A Hyperledger Fabric contract to manage elections.</p>
 * <p>
 *   The API Gateway client will receive the transaction returned values wrapped inside a
 *   <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">
 *       Response json object
 *   </a>.
 * </p>
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
     * <pre>
     * {
     *      "function": "ElectionContract:createElection",
     *      "Args": [
     *          "your_election_id",
     *          {["{"choice":"your_choiceN"}":n]*[,"{"choice":"your_choiceM"}":m]}
     *      ]
     * }
     * </pre>
     * Constraints: n >= 0, m >= 0, String must be non-empty.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link ElectionInfo} and the {@link Election}.
     * @param results the initial results of the {@link Election} to create.
     * @return the {@link Election} built.
     * @throws ChaincodeException with
     * <ul>
     *     <li>
     *         {@code ELECTION_ALREADY_EXISTS} as payload if it has already been created an {@link Election}
     *         with the same {@code electionId}
     *     </li>
     *     <li>{@code ELECTION_INVALID_BUILD_ARGUMENT} as payload if at least one of the given arguments is not valid</li>
     *     <li>
     *         {@link ElectionContractErrors#ELECTION_INFO_NOT_FOUND} as payload if the {@link ElectionInfo}
     *         labeled by electionId hasn't been created.
     *     </li>
     * </ul>
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Election createElection(final Context ctx, final String electionId, final Map<String, Long> results) {
        if (electionExists(ctx, electionId)) {
            final String errorMessage = String.format("Election %s already exists", electionId);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_ALREADY_EXISTS.toString());
        }
        final ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        try {
            final Election election = ElectionFactory.buildElection(electionInfo, results);
            final String sortedJson = genson.serialize(election);
            ctx.getStub().putStringState(electionId, sortedJson);
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
     * @throws ChaincodeException with {@link ElectionContractErrors#ELECTION_INFO_NOT_FOUND} as payload if the
     * {@link Election} labeled by electionId hasn't been created.
     */
    Election readStandardElection(final Context ctx, final String electionId) {
        if (electionExists(ctx, electionId)) {
            final String electionSerialized = ctx.getStub().getStringState(electionId);
            return genson.deserialize(electionSerialized, Election.class);
        } else {
            final String errorMessage = String.format("Election %s does not exist", electionId);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }
    }

    /**
     * Return the {@link ElectionFacade} related to {@link Election} and {@link ElectionInfo} labeled with
     * the given electionId. If the {@link Election} is still open, {@link Map} representing its results will be empty.
     * Expected JSON input in the following format:
     * <pre>
     * {
     *      "function": "ElectionContract:readElection",
     *      "Args": [
     *          "your_election_id"
     *      ]
     * }
     * </pre>
     * Constraints: String must be non-empty.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to retrieve open information (electionId and affluence).
     * @return the {@link ElectionFacade}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionFacade readElection(final Context ctx, final String electionId) {
        final Election election = readStandardElection(ctx, electionId);
        final ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        return new ElectionFacadeImpl(election, electionInfo);
    }

    /**
     * Return the {@link ElectionInfo}.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link ElectionInfo} to retrieve.
     * @return the {@link ElectionInfo}.
     * @throws ChaincodeException with
     * <ul>
     *     <li>
     *         {@link ElectionContractErrors#CROSS_INVOCATION_FAILED} as payload if something went wrong during
     *         cross channel invocation
     *     </li>
     *     <li>
     *         {@link ElectionContractErrors#ELECTION_INFO_NOT_FOUND} as payload if {@link Chaincode.Response}
     *         received doesn't contain a {@link ElectionInfo}.
     *     </li>
     * </ul>
     */
    private ElectionInfo readElectionInfo(final Context ctx, final String electionId) {
        final Chaincode.Response response = ctx.getStub().invokeChaincodeWithStringArgs(
            CHAINCODE_INFO_NAME_CH1,
            List.of("ElectionInfoContract:readElectionInfo", electionId),
            CHANNEL_INFO_NAME_CH1
        );
        if (response.getStatus().equals(Chaincode.Response.Status.SUCCESS)) {
            try {
                final Response<ElectionInfo> responsePayload = genson.deserialize(
                    response.getStringPayload(),
                    new GenericType<>() { }
                );
                return responsePayload.getResult();
            } catch (JsonBindingException | NullPointerException e) {
                throw new ChaincodeException(e.getMessage(), ElectionContractErrors.ELECTION_INFO_NOT_FOUND.toString());
            }
        } else {
            final String errorMessage = "Something went wrong with cross invocation call.";
            throw new ChaincodeException(errorMessage, ElectionContractErrors.CROSS_INVOCATION_FAILED.toString());
        }
    }

    /**
     * Cast a vote in an existing {@link Election}.
     * Expected JSON input in the following format:
     * <pre>
     * {
     *      "function": "ElectionContract:castVote",
     *      "Args": [
     *          "{"choice":"your_choice"}",
     *          "your_election_id"
     *      ]
     * }
     * </pre>
     * Expected transient map:
     * <pre>
     * {
     *      "userId":"your_user_id",
     *      "code":"your_code"
     * }
     * </pre>
     * Constraints: String must be non-empty.
     * Transient String values must be converted in base64 bytes.
     * @param ctx the {@link Context}.  A transient map is expected with the following
     *        key-value pairs: {@link UserCodeData#USER_ID} and {@link UserCodeData#CODE}.
     * @param choice the {@link Choice} of the vote.
     * @param electionId the id of the {@link Election} where the vote is cast.
     * @return the {@link Boolean} representing the successfulness of the operation.
     * @throws ChaincodeException with
     * <ul>
     *     <li>
     *         {@link ElectionContractErrors#ELECTION_NOT_FOUND} as payload if the {@link Election}
     *         in which the vote should be cast doesn't exist
     *     </li>
     *     <li>
     *         {@link ElectionContractErrors#INVALID_CREDENTIALS_TO_CAST_VOTE} as payload in case
     *         {@link UserCodeData#USER_ID} or {@link UserCodeData#CODE} aren't valid
     *     </li>
     *     <li>
     *         {@link ElectionContractErrors#INVALID_BALLOT_BUILD_ARGUMENTS} in case the arguments used to build the
     *         {@link Ballot} are not valid
     *     </li>
     *     <li>
     *         {@link ElectionContractErrors#INVALID_BALLOT_CAST_ARGUMENTS} in case something went wrong with casting.
     *     </li>
     * </ul>
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean castVote(final Context ctx, final Choice choice, final String electionId) {
        final Pair<String, String> userCodePair = UserCodeData.getUserCodePairFrom(ctx.getStub().getTransient());
        if (!electionExists(ctx, electionId)) {
            final String errorMessage = String.format("Election %s does not exist", electionId);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }
        if (!CODES_MANAGER_CONTRACT.isValid(ctx, electionId)) {
            final String errorMessage = "The given one-time-code is not valid.";
            throw new ChaincodeException(errorMessage, ElectionContractErrors.INVALID_CREDENTIALS_TO_CAST_VOTE.toString());
        }
        Ballot ballot;
        try {
            ballot = new BallotImpl.Builder().electionID(electionId)
                .voterID(userCodePair._1())
                .date(LocalDateTime.now())
                .choice(choice)
                .build();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.INVALID_BALLOT_BUILD_ARGUMENTS.toString());
        }
        final ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        final Election election = readStandardElection(ctx, electionId);
        try {
            ElectionManagerImpl.getInstance().castVote(election, electionInfo, ballot);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ChaincodeException(e.getMessage(), ElectionContractErrors.INVALID_BALLOT_CAST_ARGUMENTS.toString());
        }
        CODES_MANAGER_CONTRACT.invalidate(ctx, electionId);
        final String electionSerialized = genson.serialize(election);
        ctx.getStub().putStringState(electionId, electionSerialized);
        return true;
    }

    /**
     * Delete an {@link Election}.
     * Expected JSON input in the following format:
     * <pre>
     * {
     *      "function": "ElectionContract:deleteElection",
     *      "Args": [
     *          "your_election_id"
     *      ]
     * }
     * </pre>
     * Constraints: String must be non-empty.
     * @param ctx the {@link Context}.
     * @param electionId the id of the {@link Election} to delete.
     * @return the {@link Election} deleted.
     * @throws ChaincodeException with {@link ElectionContractErrors#ELECTION_NOT_FOUND} as payload if the
     * {@link Election} labeled by electionId doesn't exist.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Election deleteElection(final Context ctx, final String electionId) {
        if (!electionExists(ctx, electionId)) {
            final String errorMessage = String.format("Election %s does not exist", electionId);
            throw new ChaincodeException(errorMessage, ElectionContractErrors.ELECTION_NOT_FOUND.toString());
        }
        final Election election = readStandardElection(ctx, electionId);
        ctx.getStub().delState(electionId);
        return election;
    }

    /**
     * Check if an {@link Election} exists.
     * Expected JSON input in the following format:
     * <pre>
     * {
     *      "function": "ElectionContract:electionExists",
     *      "Args": [
     *          "your_election_id"
     *      ]
     * }
     * </pre>
     * @param ctx the {@link Context}.
     * @param electionId the {@link Election}'s id.
     * @return a boolean representing if the {@link Election} exists.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean electionExists(final Context ctx, final String electionId) {
        final String electionSerialized = ctx.getStub().getStringState(electionId);
        return (electionSerialized != null && !electionSerialized.isEmpty());
    }

    /**
     * Return all the existing {@link Election}s.
     * Expected JSON input in the following format:
     * <pre>
     * {
     *      "function": "ElectionContract:getAllElection",
     *      "Args": []
     * }
     * </pre>
     * @param ctx the {@link Context}.
     * @return the {@link Election}s serialized.
     * @throws ChaincodeException with  {@link ElectionContractErrors#CROSS_INVOCATION_FAILED} as payload if something
     * went wrong during cross channel invocation.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public List<ElectionFacade> getAllElection(final Context ctx) {
        final List<ElectionFacade> allElections = new ArrayList<>();
        final Chaincode.Response response = ctx.getStub().invokeChaincodeWithStringArgs(
            CHAINCODE_INFO_NAME_CH1,
            List.of("ElectionInfoContract:getAllElectionInfo"),
            CHANNEL_INFO_NAME_CH1
        );
        if (response.getStatus().equals(Chaincode.Response.Status.SUCCESS)) {
            final Response<List<ElectionInfo>> responsePayload = genson.deserialize(
                response.getStringPayload(),
                new GenericType<>() { }
            );
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

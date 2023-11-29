package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.SerializersUtils;
import it.unibo.ds.chainvote.elections.ElectionInfo;
import it.unibo.ds.chainvote.factory.ElectionFactory;
import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.Utils;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A Hyperledger Fabric contract to manage {@link ElectionInfo}.</p>
 * <p>
 *   The API Gateway client will receive the transaction returned values wrapped inside a
 *   <a href="https://tassiluca.github.io/ds-project-antonioni-rubboli-tassinari-ay2223/smart-contracts/javadoc/presentation/it/unibo/ds/chainvote/Response.html">
 *       Response json object
 *   </a>.
 * </p>
 */
@Contract(
    name = "ElectionInfoContract",
    info = @Info(
        title = "Election Info Contract",
        description = "Contract used to manage election info"
    ),
    transactionSerializer = "it.unibo.ds.chainvote.TransactionSerializer"
)
@Default
public final class ElectionInfoContract implements ContractInterface {

    private final Genson genson = SerializersUtils.gensonInstance();

    private enum ElectionInfoTransferErrors {
        ELECTION_INFO_NOT_FOUND,
        ELECTION_INFO_ALREADY_EXISTS,
        INVALID_ARGUMENT
    }

    /**
     * Create a {@link ElectionInfo}.
     * Expected JSON input in the following format:
     * <pre>
     * {
     *      "function": "ElectionInfoContract:createElectionInfo",
     *      "Args": [
     *          "your_goal",
     *          n,
     *          "yyyy-MM-ddThh:mm:ss",
     *          "yyyy-MM-ddThh:mm:ss",
     *          [{"choice":"your_choice1"},{"choice":"your_choice2"}*[,{"choice":"your_choiceN"}]]
     *      ]
     * }
     * </pre>
     * Constraints: n > 1, String must be non-empty.
     * @param ctx the {@link Context}.
     * @param goal the goal of the {@link ElectionInfo} to build.
     * @param votersNumber the number of voters that could cast a vote in the {@link ElectionInfo} to build.
     * @param sDate the {@link String} representing the encoded (ISO format) start date.
     * @param eDate the {@link String} representing the encoded (ISO format) end date.
     * @param choices the {@link List} of {@link Choice} that the {@link ElectionInfo} to build has. The {@link List}
     *                must contains at least two different {@link Choice}s different from the
     *                {@link it.unibo.ds.chainvote.utils.FixedVotes#INFORMAL_BALLOT}.
     *                If {@link it.unibo.ds.chainvote.utils.FixedVotes#INFORMAL_BALLOT} is not present, it will be
     *                added by the system.
     * @return the {@link ElectionInfo} built.
     * @throws ChaincodeException with
     * <ul>
     *      <li>
     *          {@code ELECTION_INFO_ALREADY_EXISTS} as payload if it has already been created an
     *          {@link ElectionInfo} with same arguments
     *      </li>
     *      <li>{@code INVALID_ARGUMENT} as payload if at least one of the given arguments is not valid</li>
     * </ul>
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ElectionInfo createElectionInfo(
        final Context ctx,
        final String goal,
        final Long votersNumber,
        final String sDate,
        final String eDate,
        final List<Choice> choices
    ) {
        LocalDateTime startingDate;
        LocalDateTime endingDate;
        try {
            startingDate = LocalDateTime.parse(sDate, DateTimeFormatter.ISO_DATE_TIME);
            endingDate = LocalDateTime.parse(eDate, DateTimeFormatter.ISO_DATE_TIME);
        } catch (NullPointerException | DateTimeParseException e) {
            throw new ChaincodeException(e.getMessage(), ElectionInfoTransferErrors.INVALID_ARGUMENT.toString());
        }
        final String electionId = Utils.calculateID(goal, startingDate, endingDate, choices);
        if (electionInfoExists(ctx, electionId)) {
            final String errorMessage = String.format("Election info %s already exists", electionId);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_ALREADY_EXISTS.toString());
        }
        ElectionInfo electionInfo;
        try {
            electionInfo = ElectionFactory.buildElectionInfo(goal, votersNumber, startingDate, endingDate, choices);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ChaincodeException(e.getMessage(), ElectionInfoTransferErrors.INVALID_ARGUMENT.toString());
        }
        final String sortedJson = genson.serialize(electionInfo);
        ctx.getStub().putStringState(electionId, sortedJson);
        return electionInfo;
    }

    /**
     * Return the {@link ElectionInfo}.
     * Expected JSON input in the following format:
     * <pre>
     * {
     *      "function": "ElectionInfoContract:readElectionInfo",
     *      "Args": [
     *          "your_election_id"
     *      ]
     * }
     * </pre>
     * Constraints: String must be non-empty.
     * @param ctx the {@link Context}.
     * @param electionId the electionId of the {@link ElectionInfo} to retrieve.
     * @return the {@link ElectionInfo}.
     * @throws ChaincodeException with {@code ELECTION_INFO_NOT_FOUND} as payload if no {@link ElectionInfo} is
     * labeled by the given electionId.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionInfo readElectionInfo(final Context ctx, final String electionId) {
        if (electionInfoExists(ctx, electionId)) {
            final String electionJSON = ctx.getStub().getStringState(electionId);
            return genson.deserialize(electionJSON, ElectionInfo.class);
        } else {
            final String errorMessage = String.format("Election info %s does not exist", electionId);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
        }
    }

    /**
     * Delete an {@link ElectionInfo} from the ledger.
     * Expected JSON input in the following format:
     * <pre>
     * {
     *      "function": "ElectionInfoContract:deleteElectionInfo",
     *      "Args": [
     *          "your_election_id",
     *      ]
     * }
     * </pre>
     * Constraints: String must be non-empty.
     * @param ctx the {@link Context}.
     * @param electionId the electionId of the {@link ElectionInfo} to delete.
     * @return the {@link ElectionInfo} deleted.
     * @throws ChaincodeException with {@code ELECTION_INFO_NOT_FOUND} as payload if there
     * isn't an {@link ElectionInfo} labeled by the given electionId.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ElectionInfo deleteElectionInfo(final Context ctx, final String electionId) {
        if (!electionInfoExists(ctx, electionId)) {
            final String errorMessage = String.format("Election info %s does not exist", electionId);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
        }
        final ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        ctx.getStub().delState(electionId);
        return electionInfo;
    }

    /**
     * Check if an {@link ElectionInfo} exists.
     * @param ctx the {@link Context}.
     * @param electionId the electionId of the {@link ElectionInfo} to check.
     * @return a boolean representing the {@link ElectionInfo}'s existence.
     */
    private boolean electionInfoExists(final Context ctx, final String electionId) {
        final String electionInfoSerialized = ctx.getStub().getStringState(electionId);
        return electionInfoSerialized != null && !electionInfoSerialized.isEmpty();
    }

    /**
     * Return all the existing {@link ElectionInfo}s.
     * Expected JSON input in the following format:
     * <pre>
     * {
     *      "function": "ElectionInfoContract:getAllElectionInfo",
     *      "Args": []
     * }
     * </pre>
     * @param ctx the {@link Context}.
     * @return all the {@link ElectionInfo}s retrieved from the ledger as {@link List}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public List<ElectionInfo> getAllElectionInfo(final Context ctx) {
        final List<ElectionInfo> queryResults = new ArrayList<>();
        final QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("", "");
        for (final KeyValue result: results) {
            final ElectionInfo election = genson.deserialize(result.getStringValue(), ElectionInfo.class);
            queryResults.add(election);
        }
        return queryResults;
    }
}

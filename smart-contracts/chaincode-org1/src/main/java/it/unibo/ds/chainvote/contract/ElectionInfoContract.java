package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.SerializersUtils;
import it.unibo.ds.chainvote.assets.ElectionInfo;
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
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Contracts managing {@link ElectionInfo}.
 * TODO improve documentation
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
        ELECTION_INFO_INVALID_ARGUMENT
    }

    /**
     * Create a {@link ElectionInfo}.
     * @param ctx the {@link Context}.
     * @param goal the goal of the {@link ElectionInfo} to build.
     * @param votersNumber the number of voters that could cast a vote in the {@link ElectionInfo} to build.
     * @param sDate the {@link String} representing the encoded (ISO format) starting date.
     * @param eDate the {@link String} representing the encoded (ISO format) ending date.
     * @param choices the {@link List} of {@link Choice} that the {@link ElectionInfo} to build has.
     * @return the {@link ElectionInfo} built.
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
        LocalDateTime startingDate = LocalDateTime.parse(sDate, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endingDate = LocalDateTime.parse(eDate, DateTimeFormatter.ISO_DATE_TIME);
        final ChaincodeStub stub = ctx.getStub();
        final String electionId = Utils.calculateID(goal, startingDate, endingDate, choices);
        if (electionInfoExists(ctx, electionId)) {
            String errorMessage = String.format("Election info %s already exists", electionId);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_ALREADY_EXISTS.toString());
        }
        try {
            ElectionInfo electionInfo = ElectionFactory
                .buildElectionInfo(goal, votersNumber, startingDate, endingDate, choices);
            String sortedJson = genson.serialize(electionInfo);
            stub.putStringState(electionId, sortedJson);
            return electionInfo;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionInfoTransferErrors.ELECTION_INFO_INVALID_ARGUMENT.toString());
        }
    }

    /**
     * Return the {@link ElectionInfo}.
     * @param ctx The {@link Context}.
     * @param electionId The electionId of the {@link ElectionInfo} to retrieve.
     * @return the {@link ElectionInfo}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionInfo readElectionInfo(final Context ctx, final String electionId) {
        System.out.println("[EIC] readElectionInfo");
        if (electionInfoExists(ctx, electionId)) {
            ChaincodeStub stub = ctx.getStub();
            String electionJSON = stub.getStringState(electionId);
            return genson.deserialize(electionJSON, ElectionInfo.class);
        } else {
            String errorMessage = String.format("Election info %s does not exist", electionId);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
        }
    }

    /**
     * Delete an {@link ElectionInfo} from the ledger.
     * @param ctx The {@link Context}.
     * @param electionId The electionId of the {@link ElectionInfo} to delete.
     * @return the {@link ElectionInfo} deleted.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ElectionInfo deleteElectionInfo(final Context ctx, final String electionId) {
        System.out.println("[EIC] deleteAsset");
        ChaincodeStub stub = ctx.getStub();
        if (!electionInfoExists(ctx, electionId)) {
            String errorMessage = String.format("Election info %s does not exist", electionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
        }

        ElectionInfo electionInfo = readElectionInfo(ctx, electionId);

        stub.delState(electionId);

        return electionInfo;
    }

    /**
     * Check if an {@link ElectionInfo} exists.
     * @param ctx The {@link Context}.
     * @param electionId The electionId of the {@link ElectionInfo} to check.
     * @return A boolean representing the {@link ElectionInfo} existence.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    private boolean electionInfoExists(final Context ctx, final String electionId) {
        System.out.println("[EIC] electionInfoExists");
        ChaincodeStub stub = ctx.getStub();
        String electionInfoSerialized = stub.getStringState(electionId);
        return (electionInfoSerialized != null && !electionInfoSerialized.isEmpty());
    }

    /**
     * Return all the existing {@link ElectionInfo}s.
     * @param ctx The {@link Context}.
     * @return All the {@link ElectionInfo}s retrieved from the ledger as {@link String}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public List<ElectionInfo> getAllElectionInfo(final Context ctx) {
        System.out.println("[EIC] getAllElectionInfo");

        ChaincodeStub stub = ctx.getStub();
        List<ElectionInfo> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");
        for (KeyValue result: results) {
            ElectionInfo election = genson.deserialize(result.getStringValue(), ElectionInfo.class);
            queryResults.add(election);
        }
        System.out.println("[GAEI] results: " + queryResults);

        return queryResults;
    }
}
package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.ElectionInfoAsset;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.factory.ElectionFactory;
import it.unibo.ds.core.manager.ElectionManagerImpl;
import it.unibo.ds.core.utils.Choice;
import it.unibo.ds.core.utils.Utils;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Contracts managing {@link ElectionInfo}.
 */
@Contract(
        name = "ElectionInfoContract",
        info = @Info(
                title = "Election Info Contract",
                description = "Contract used to manage election info"
        )
)

@Default
public final class ElectionInfoContract implements ContractInterface {

    private final Genson genson = new Genson();

    private enum ElectionInfoTransferErrors {
        ELECTION_INFO_NOT_FOUND,
        ELECTION_INFO_ALREADY_EXISTS,
        ELECTION_INFO_INVALID_ARGUMENT
    }

    /**
     * Initialize Ledger.
     * @param ctx the {@link Context}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
    }

    /**
     * Create a {@link ElectionInfoAsset}.
     * @param ctx the {@link Context}.
     * @param goal the goal of the new {@link ElectionInfo}.
     * @param votersNumber the voters number of the new {@link ElectionInfo}.
     * @param startingDate the starting {@link LocalDateTime} of the new {@link ElectionInfo}.
     * @param endingDate the ending {@link LocalDateTime} of the new {@link ElectionInfo}.
     * @param choices the list of {@link Choice} of the new {@link ElectionInfo}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createElection(final Context ctx, final String goal, final long votersNumber,
                                        final LocalDateTime startingDate, final LocalDateTime endingDate,
                                        final List<Choice> choices) {
        ChaincodeStub stub = ctx.getStub();
        String electionID = Utils.calculateID(goal, startingDate, endingDate, choices);
        if (electionInfoExists(ctx, electionID)) {
            String errorMessage = String.format("Election info %s already exists", electionID);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_ALREADY_EXISTS.toString());
        }

        try {
            ElectionInfo electionInfo = ElectionFactory
                    .buildElectionInfo(goal, votersNumber, startingDate, endingDate, choices);
            String sortedJson = genson.serialize(electionInfo);
            stub.putStringState(electionID, sortedJson);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionInfoTransferErrors.ELECTION_INFO_INVALID_ARGUMENT.toString());
        }
    }

    /**
     * Return the {@link ElectionInfoAsset}.
     * @param ctx the {@link Context}.
     * @param electionID the ID of the {@link ElectionInfoAsset} to retrieve.
     * @return the {@link ElectionInfoAsset}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionInfoAsset readElectionInfoAsset(final Context ctx, final String electionID) {
        if (electionInfoExists(ctx, electionID)) {
            ChaincodeStub stub = ctx.getStub();
            String electionJSON = stub.getStringState(electionID);
            ElectionInfo electionInfo = genson.deserialize(electionJSON, ElectionInfo.class);
            return new ElectionInfoAsset(electionID, electionInfo);
        } else {
            String errorMessage = String.format("Election info %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
        }
    }

    /**
     * Delete an {@link ElectionInfoAsset}.
     * @param ctx the {@link Context}.
     * @param electionID the {@link ElectionInfoAsset}'s ID.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteAsset(final Context ctx, final String electionID) {
        ChaincodeStub stub = ctx.getStub();
        if (!electionInfoExists(ctx, electionID)) {
            String errorMessage = String.format("Election info %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
        }
        stub.delState(electionID);
    }

    /**
     * Check if an {@link ElectionInfoAsset} exists.
     * @param ctx the {@link Context}.
     * @param electionID the {@link ElectionInfoAsset}'s ID.
     * @return if the {@link ElectionInfoAsset} exists.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean electionInfoExists(final Context ctx, final String electionID) {
        ChaincodeStub stub = ctx.getStub();
        String electionJSON = stub.getStringState(electionID);
        return (electionJSON != null && !electionJSON.isEmpty());
    }

    /**
     * Return all the existing {@link ElectionInfo}s.
     * @param ctx the {@link Context}.
     * @return the {@link ElectionInfo}s as {@link String}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getAllAssets(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<ElectionInfo> queryResults = new ArrayList<ElectionInfo>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            ElectionInfo election = genson.deserialize(result.getStringValue(), ElectionInfo.class);
            queryResults.add(election);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }
}
package it.unibo.ds.chainvote.contract;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.unibo.ds.chainvote.assets.BallotAsset;
import it.unibo.ds.chainvote.assets.ElectionAsset;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.BallotImpl;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionImpl;
import it.unibo.ds.core.utils.Choice;
import it.unibo.ds.core.utils.Utils;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

/**
 * A contract.
 */
@Contract(
        name = "basic",
        info = @Info(
                title = "Asset Transfer",
                description = "The hyperlegendary asset transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))

@Default
public final class ElectionTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum ElectionTransferErrors {
        ELECTION_NOT_FOUND,
        ELECTION_ALREADY_EXISTS,
        ELECTION_INVALID_ARGUMENT
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
     * Create a {@link Election}.
     * @param ctx the {@link Context}.
     * @param goal the goal of the new {@link Election}.
     * @param votersNumber the voters number of the new {@link Election}.
     * @param startingDate the starting {@link LocalDateTime} of the new {@link Election}.
     * @param endingDate the ending {@link LocalDateTime} of the new {@link Election}.
     * @param choices the list of {@link Choice} of the new {@link Election}.
     * @return the new {@link Election}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public ElectionAsset createElection(final Context ctx, final String goal, final long votersNumber,
                                        final LocalDateTime startingDate, final LocalDateTime endingDate,
                                        final List<Choice> choices) {
        ChaincodeStub stub = ctx.getStub();
        String electionID = Utils.calculateID(goal, startingDate, endingDate, choices);
        if (electionExists(ctx, electionID)) {
            String errorMessage = String.format("Election %s already exists", electionID);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_ALREADY_EXISTS.toString());
        }

        try {
            Election election = new ElectionImpl.Builder().goal(goal)
                    .voters(votersNumber)
                    .start(startingDate)
                    .end(endingDate)
                    .choices(choices)
                    .build();
            String sortedJson = genson.serialize(election);
            stub.putStringState(electionID, sortedJson);
            return new ElectionAsset("", election);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
        }
    }

    /**
     * Return the {@link Election}.
     * @param ctx the {@link Context}.
     * @param electionID the {@link Election}'s ID of the {@link Election} to retrieve.
     * @return the {@link Election}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionAsset readElection(final Context ctx, final String electionID) {
        if (electionExists(ctx, electionID)) {
            ChaincodeStub stub = ctx.getStub();
            String electionJSON = stub.getStringState(electionID);
            return genson.deserialize(electionJSON, ElectionAsset.class);
        } else {
            String errorMessage = String.format("Election %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
        }
    }

    /**
     * Cast a {@link Ballot} in an existing {@link Election}.
     * @param ctx the {@link Context}.
     * @param choice the {@link Choice} of the {@link Ballot}.
     * @param voterID the voter ID of the {@link Ballot}.
     * @param electionID the {@link Election}'s ID.
     * @return the {@link Ballot} casted.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public BallotAsset castVote(final Context ctx, final Choice choice, final String voterID, final String electionID) {
        ChaincodeStub stub = ctx.getStub();
        if (!electionExists(ctx, electionID)) {
            String errorMessage = String.format("Election %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
        }
        try {
            BallotAsset ballot = new BallotAsset(electionID, new BallotImpl.Builder().electionID(electionID)
            .voterID(voterID)
            .dateUnchecked(LocalDateTime.now())
            .choiceUnchecked(choice)
            .build());
            ElectionAsset election = readElection(ctx, electionID);
            election.getAsset().castVote(ballot.getAsset());

            String electionJson = genson.serialize(election);
            // Required ??
            deleteAsset(ctx, electionID);
            stub.putStringState(electionID, electionJson);
            return ballot;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
        }
    }

    /**
     * Delete an {@link Election}.
     * @param ctx the {@link Context}.
     * @param electionID the {@link Election}'s ID.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteAsset(final Context ctx, final String electionID) {
        ChaincodeStub stub = ctx.getStub();

        if (!electionExists(ctx, electionID)) {
            String errorMessage = String.format("Election %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
        }
        stub.delState(electionID);
    }

    /**
     * Check if an {@link Election} exists.
     * @param ctx the {@link Context}.
     * @param electionID the {@link Election}'s ID.
     * @return if the {@link Election} exists.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean electionExists(final Context ctx, final String electionID) {
        ChaincodeStub stub = ctx.getStub();
        String electionJSON = stub.getStringState(electionID);
        return (electionJSON != null && !electionJSON.isEmpty());
    }

    /**
     * Return all the existing {@link Election}s.
     * @param ctx the {@link Context}.
     * @return the {@link Election}s.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getAllAssets(final Context ctx) {
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

        final String response = genson.serialize(queryResults);

        return response;
    }
}

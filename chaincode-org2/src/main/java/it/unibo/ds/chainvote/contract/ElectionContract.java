package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.ElectionAsset;
import it.unibo.ds.chainvote.assets.ElectionInfoAsset;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.BallotImpl;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.factory.ElectionFactory;
import it.unibo.ds.core.manager.ElectionManagerImpl;
import it.unibo.ds.core.utils.Choice;
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
import java.util.HashMap;
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
                )
)

@Default
public final class ElectionContract implements ContractInterface {

    private static final String CHANNEL_INFO_NAME = "ch2";
    private static final String CHAINCODE_INFO_NAME = "chaincode-org1";

    private final Genson genson = new Genson();

    private enum ElectionTransferErrors {
        ELECTION_NOT_FOUND,
        ELECTION_ALREADY_EXISTS,
        ELECTION_INVALID_ARGUMENT,
        ELECTION_INVALID_BALLOT_ARGUMENT
    }

    /**
     * Initialize Ledger.
     * @param ctx the {@link Context}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        // ChaincodeStub stub = ctx.getStub();
    }

    /**
     * Create a {@link ElectionAsset}.
     * @param ctx the {@link Context}.
     * @param electionID the ID of the new {@link ElectionAsset}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createElection(final Context ctx, final String electionID) {
        this.createElection(ctx, electionID, new HashMap<>());
    }

    /**
     * Create a {@link ElectionAsset}.
     * @param ctx the {@link Context}.
     * @param electionID the ID of the new {@link ElectionAsset}.
     * @param results the results of the new {@link ElectionAsset}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createElection(final Context ctx, final String electionID, final Map<Choice, Long> results) {
        ChaincodeStub stub = ctx.getStub();
        if (electionExists(ctx, electionID)) {
            String errorMessage = String.format("ElectionAsset %s already exists", electionID);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_ALREADY_EXISTS.toString());
        }

        ElectionInfoAsset electionInfo = readElectionInfo(ctx, electionID);

        try {
            Election election = ElectionFactory
                    .buildElection(electionInfo.getAsset(), results);
            ElectionAsset electionAsset = new ElectionAsset(electionID, election);
            String sortedJson = genson.serialize(electionAsset.getAsset());
            stub.putStringState(electionAsset.getElectionId(), sortedJson);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
        }
    }

    /**
     * Return the {@link ElectionAsset}.
     * @param ctx the {@link Context}.
     * @param electionID the ID of the {@link ElectionAsset} to retrieve.
     * @return the {@link ElectionAsset}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionAsset readElectionAsset(final Context ctx, final String electionID) {
        if (electionExists(ctx, electionID)) {
            ChaincodeStub stub = ctx.getStub();
            String electionJSON = stub.getStringState(electionID);
            return new ElectionAsset(electionID, genson.deserialize(electionJSON, Election.class));
        } else {
            String errorMessage = String.format("ElectionAsset %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
        }
    }

    /**
     * Return the {@link ElectionInfoAsset}.
     * @param ctx the {@link Context}.
     * @param electionID the ID of the {@link ElectionInfoAsset} to retrieve.
     * @return the {@link ElectionInfoAsset}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionInfoAsset readElectionInfo(final Context ctx, final String electionID) {
        var response = ctx.getStub().invokeChaincodeWithStringArgs(CHAINCODE_INFO_NAME,
                "{\"function\":\"readElectionInfoAsset\", \"Args\":[\"" + electionID + "\"]}",
                CHANNEL_INFO_NAME);
        System.out.println(response.getStatusCode());
        System.out.println(response.getMessage());
        /*
        ChaincodeStub stub = ctx.getStub();
        // TODO complete how to read from different channels assets.
        String electionJSON = stub.getStringState(electionID);
        return genson.deserialize(electionJSON, ElectionInfoAsset.class);
         */
        return null;
    }

    /**
     * Cast a {@link Ballot} in an existing {@link Election}.
     * @param ctx the {@link Context}.
     * @param choice the {@link Choice} of the {@link Ballot}.
     * @param voterID the voter ID of the {@link Ballot}.
     * @param electionID the {@link ElectionAsset}'s ID.
     * @return the {@link Ballot} cast.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Ballot castVote(final Context ctx, final Choice choice, final String voterID, final String electionID) {
        ChaincodeStub stub = ctx.getStub();
        if (!electionExists(ctx, electionID)) {
            String errorMessage = String.format("ElectionAsset %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
        }
        Ballot ballot = null;
        try {
            ballot = new BallotImpl.Builder().electionID(electionID)
                .voterID(voterID)
                .date(LocalDateTime.now())
                .choice(choice)
                .build();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
        }
        ElectionInfoAsset electionInfo = readElectionInfo(ctx, electionID);
        ElectionAsset election = readElectionAsset(ctx, electionID);
        try {
            ElectionManagerImpl.getInstance().castVote(election.getAsset(), electionInfo.getAsset(), ballot);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw new ChaincodeException(ElectionTransferErrors.ELECTION_INVALID_BALLOT_ARGUMENT.toString());
        }
        String electionJson = genson.serialize(election.getAsset());
        // Required ??
        deleteAsset(ctx, electionID);
        stub.putStringState(election.getElectionId(), electionJson);
        return ballot;
    }

    /**
     * Delete an {@link ElectionAsset}.
     * @param ctx the {@link Context}.
     * @param electionID the {@link ElectionAsset}'s ID.
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
     * Check if an {@link ElectionAsset} exists.
     * @param ctx the {@link Context}.
     * @param electionID the {@link ElectionAsset}'s ID.
     * @return if the {@link ElectionAsset} exists.
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

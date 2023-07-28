package contracts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import assets.Ballot;
import assets.Election;
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

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Election createElection(final Context ctx, final String goal, final long votersNumber,
                                   final LocalDateTime startingDate, final LocalDateTime endingDate,
                                   final List<String> choices) {
        ChaincodeStub stub = ctx.getStub();

        if (startingDate.isEqual(endingDate) || startingDate.isAfter(endingDate)) {
            String errorMessage = String.format("Election %s has invalid starting %s and ending %s date", election.getElectionID(), startingDate, endingDate);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
        }

        if (votersNumber <= 0) {
            String errorMessage = String.format("Election %s has invalid number of voters %d", election.getElectionID(), votersNumber);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
        }

        if (choices.size() == 0) {
            String errorMessage = String.format("Election %s has invalid choices: %s", election.getElectionID(), choices);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
        }

        Election election = new Election(goal, votersNumber, startingDate, endingDate, choices);

        if (electionExists(ctx, election.getElectionID())) {
            String errorMessage = String.format("Election %s already exists", election.getElectionID());
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_ALREADY_EXISTS.toString());
        }

        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(election);

        stub.putStringState(electionID, sortedJson);

        return election;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Election readAsset(final Context ctx, final String electionID) {
        ChaincodeStub stub = ctx.getStub();
        String electionJSON = stub.getStringState(electionID);

        if (electionJSON == null || electionJSON.isEmpty()) {
            String errorMessage = String.format("Election %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
        }

        Election election = genson.deserialize(electionJSON, Election.class);
        return election;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset castVote(final Context ctx, final String choice, final String voterID, final String electionID) {
        ChaincodeStub stub = ctx.getStub();

        if (!electionExists(ctx, electionID)) {
            String errorMessage = String.format("Election %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
        }

        Ballot ballot = new Ballot(electionID, voterID, LocalDateTime.now(), choice);

        Asset newAsset = new Asset(assetID, color, size, owner, appraisedValue);
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newAsset);
        stub.putStringState(assetID, sortedJson);
        return newAsset;
    }

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

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean electionExists(final Context ctx, final String electionID) {
        ChaincodeStub stub = ctx.getStub();
        String electionJSON = stub.getStringState(electionID);

        return (electionJSON != null && !electionJSON.isEmpty());
    }

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

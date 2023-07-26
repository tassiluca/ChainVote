package contracts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import assets.Ballot;
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
public final class BallotTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum BallotTransferErrors {
        BALLOT_NOT_FOUND,
        BALLOT_ALREADY_EXISTS,
        BALLOT_INVALID_ARGUMENT
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Ballot createBallot(final Context ctx, final String electionID, final String voterCodeID,
                               final LocalDateTime date, final String choice) {
        ChaincodeStub stub = ctx.getStub();

        if (ballotExists(ctx, electionID, voterCodeID)) {
            String errorMessage = String.format("Voter %s has already casted his vote in %s election", voterCodeID, electionID);
            System.err.println(errorMessage);
            throw new ChaincodeException(errorMessage, BallotTransferErrors.BALLOT_ALREADY_EXISTS.toString());
        }

        Ballot ballot = new Ballot(electionID, voterCodeID, date, choice);
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(ballot);
        // Need to think how to update state!!
        stub.putStringState(ballot.hashCode(), sortedJson);

        return ballot;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Ballot readAsset(final Context ctx, final String electionID, final String voterCodeID) {
        ChaincodeStub stub = ctx.getStub();
        String ballotJSON = stub.getStringState(ballotID);

        if (ballotJSON == null || ballotJSON.isEmpty()) {
            String errorMessage = String.format("Ballot %s does not exist", ballotID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, BallotTransferErrors.BALLOT_NOT_FOUND.toString());
        }

        Ballot ballot = genson.deserialize(ballotJSON, Ballot.class);
        return ballot;
    }

    /*
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset UpdateAsset(final Context ctx, final String assetID, final String color, final int size,
                             final String owner, final int appraisedValue) {
        ChaincodeStub stub = ctx.getStub();

        if (!electionExists(ctx, assetID)) {
            String errorMessage = String.format("Asset %s does not exist", assetID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Asset newAsset = new Asset(assetID, color, size, owner, appraisedValue);
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newAsset);
        stub.putStringState(assetID, sortedJson);
        return newAsset;
    }
    */

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteAsset(final Context ctx, final String electionID) {
        ChaincodeStub stub = ctx.getStub();

        if (!ballotExists(ctx, electionID)) {
            String errorMessage = String.format("Election %s does not exist", electionID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
        }

        stub.delState(electionID);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean ballotExists(final Context ctx, final String electionID) {
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

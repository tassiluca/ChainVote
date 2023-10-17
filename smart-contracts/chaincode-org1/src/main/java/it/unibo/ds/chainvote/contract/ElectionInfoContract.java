package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.utils.ArgsData;
import it.unibo.ds.chainvote.assets.ElectionInfoAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.factory.ElectionFactory;
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
    ),
    transactionSerializer = "it.unibo.ds.chainvote.transaction.TransactionSerializer"
)

@Default
public final class ElectionInfoContract implements ContractInterface {

    private final Genson genson = GensonUtils.create();

    private enum ElectionInfoTransferErrors {
        ELECTION_INFO_NOT_FOUND,
        ELECTION_INFO_ALREADY_EXISTS,
        ELECTION_INFO_INVALID_ARGUMENT
    }

    /**
     * Create a {@link ElectionInfoAsset}.
     * @param ctx the {@link Context}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String createElectionInfo(
        final Context ctx,
        final String goal,
        final Long votersNumber,
        final LocalDateTime startingDate,
        final LocalDateTime endingDate,
        final List<Choice> choices
    ) {
        ChaincodeStub stub = ctx.getStub();
        String electionId = Utils.calculateID(goal, startingDate, endingDate, choices);
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
            return electionId;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            throw new ChaincodeException(e.getMessage(), ElectionInfoTransferErrors.ELECTION_INFO_INVALID_ARGUMENT.toString());
        }
    }

    /**
     * Return the {@link ElectionInfoAsset}.
     * @param ctx the {@link Context}.
     * @return the {@link ElectionInfoAsset}.
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
            throw new ChaincodeException(String.format(errorMessage, electionId), ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
        }
    }

    /**
     * Return the {@link ElectionInfoAsset} as String.
     * @param ctx the {@link Context}.
     * @param electionId the election identifier.
     * @return the {@link ElectionInfoAsset}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String readElectionInfoSerialized(final Context ctx, final String electionId) {
        System.out.println("[EIC] readElectionInfoSerialized");
        ElectionInfo electionInfo = readElectionInfo(ctx, electionId);
        return ArgsData.ELECTION_INFO.getKey() + ":" + genson.serialize(electionInfo);
    }

    /**
     * Delete an {@link ElectionInfoAsset}.
     * @param ctx the {@link Context}.
     * @param electionId the election identifier.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteAsset(final Context ctx, final String electionId) {
        System.out.println("[EIC] deleteAsset");
        ChaincodeStub stub = ctx.getStub();
        if (!electionInfoExists(ctx, electionId)) {
            String errorMessage = String.format("Election info %s does not exist", electionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
        }
        stub.delState(electionId);
    }

    /**
     * Check if an {@link ElectionInfoAsset} exists.
     * @param ctx the {@link Context}.
     * @return if the {@link ElectionInfoAsset} exists.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    private boolean electionInfoExists(final Context ctx, final String electionId) {
        System.out.println("[EIC] electionInfoExists");

        ChaincodeStub stub = ctx.getStub();
        String electionInfoSerialized = stub.getStringState(electionId);
        System.out.println("[EIC - electionInfoExists] ElectionInfo " + electionId +  " exists? " + (electionInfoSerialized != null && !electionInfoSerialized.isEmpty()));
        return (electionInfoSerialized != null && !electionInfoSerialized.isEmpty());
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

        return genson.serialize(queryResults);
    }
}
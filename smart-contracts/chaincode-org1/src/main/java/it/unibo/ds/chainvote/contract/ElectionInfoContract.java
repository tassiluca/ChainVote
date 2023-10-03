package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.unibo.ds.chaincode.utils.TransientData.ELECTION_ID;
import static it.unibo.ds.chaincode.utils.TransientData.ENDING_DATE;
import static it.unibo.ds.chaincode.utils.TransientData.GOAL;
import static it.unibo.ds.chaincode.utils.TransientData.LIST;
import static it.unibo.ds.chaincode.utils.TransientData.STARTING_DATE;
import static it.unibo.ds.chaincode.utils.TransientData.VOTERS;
import static it.unibo.ds.chaincode.utils.TransientUtils.applyToTransients;
import static it.unibo.ds.chaincode.utils.TransientUtils.getDateFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getListFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getLongFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getStringFromTransient;

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

    private final Genson genson = GensonUtils.create();

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
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createElectionInfo(final Context ctx) {
        applyToTransients(ctx,
            t -> getStringFromTransient(t, GOAL.getKey()),
            t -> getLongFromTransient(t, VOTERS.getKey()),
            t -> getDateFromTransient(t, STARTING_DATE.getKey()),
            t -> getDateFromTransient(t, ENDING_DATE.getKey()),
            t -> getListFromTransient(t, LIST.getKey()),
            (goal, votersNumber, startingDate, endingDate, choices) -> {

                List<Choice> choicesToUse = choices.stream().map(o -> (Choice) o).collect(Collectors.toList());

                ChaincodeStub stub = ctx.getStub();
                String electionId = Utils.calculateID(goal, startingDate, endingDate, choicesToUse);
                if (electionInfoExists(ctx, electionId)) {
                    String errorMessage = String.format("Election info %s already exists", electionId);
                    System.err.println(errorMessage);
                    throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_ALREADY_EXISTS.toString());
                }

                try {
                    ElectionInfo electionInfo = ElectionFactory
                        .buildElectionInfo(goal, votersNumber, startingDate, endingDate, choicesToUse);
                    String sortedJson = genson.serialize(electionInfo);
                    stub.putStringState(electionId, sortedJson);
                    return null;
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    throw new ChaincodeException(e.getMessage(), ElectionInfoTransferErrors.ELECTION_INFO_INVALID_ARGUMENT.toString());
                }
            }
        );
    }

    /**
     * Return the {@link ElectionInfoAsset}.
     * @param ctx the {@link Context}.
     * @return the {@link ElectionInfoAsset}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionInfo readElectionInfo(final Context ctx) {
        System.out.println("[EIC] readElectionInfo");
        return applyToTransients(ctx,
            t -> getStringFromTransient(t, ELECTION_ID.getKey()),
            (electionId) -> {
                if (electionInfoExists(ctx, electionId)) {
                    ChaincodeStub stub = ctx.getStub();
                    String electionJSON = stub.getStringState(electionId);
                    System.out.println("[EIC] readElectionInfo response: " + electionJSON);
                    return genson.deserialize(electionJSON, ElectionInfo.class);
                } else {
                    throw new ChaincodeException(String.format("Election info %s does not exist", electionId), ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
                }
            }
        );
    }

    /**
     * Delete an {@link ElectionInfoAsset}.
     * @param ctx the {@link Context}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteAsset(final Context ctx) {
        System.out.println("[EIC] deleteAsset");

        applyToTransients(ctx,
            t -> getStringFromTransient(t, ELECTION_ID.getKey()),
            (electionId) -> {
                ChaincodeStub stub = ctx.getStub();
                if (!electionInfoExists(ctx, electionId)) {
                    String errorMessage = String.format("Election info %s does not exist", electionId);
                    System.out.println(errorMessage);
                    throw new ChaincodeException(errorMessage, ElectionInfoTransferErrors.ELECTION_INFO_NOT_FOUND.toString());
                }
                stub.delState(electionId);
                return null;
            }
        );
    }

    /**
     * Check if an {@link ElectionInfoAsset} exists.
     * @param ctx the {@link Context}.
     * @return if the {@link ElectionInfoAsset} exists.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    private boolean electionInfoExists(final Context ctx, String electionId) {
        System.out.println("[EIC] electionInfoExists");

        ChaincodeStub stub = ctx.getStub();
        String electionJSON = stub.getStringState(electionId);
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
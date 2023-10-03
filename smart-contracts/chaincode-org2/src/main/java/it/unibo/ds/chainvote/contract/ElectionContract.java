package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.ElectionAsset;
import it.unibo.ds.chainvote.assets.ElectionInfoAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.BallotImpl;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.factory.ElectionFactory;
import it.unibo.ds.core.manager.ElectionManagerImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static it.unibo.ds.chaincode.utils.TransientData.CHOICE;
import static it.unibo.ds.chaincode.utils.TransientData.CODE;
import static it.unibo.ds.chaincode.utils.TransientData.ELECTION_ID;
import static it.unibo.ds.chaincode.utils.TransientData.RESULTS;
import static it.unibo.ds.chaincode.utils.TransientData.USER_ID;
import static it.unibo.ds.chaincode.utils.TransientUtils.applyToTransients;
import static it.unibo.ds.chaincode.utils.TransientUtils.getChoiceFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getLongFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getMapOfResultsFromTransient;
import static it.unibo.ds.chaincode.utils.TransientUtils.getStringFromTransient;

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

    private static final String CHANNEL_INFO_NAME_CH1 = "ch1";
    private static final String CHAINCODE_INFO_NAME_CH1 = "chaincode-org1";
    private static final String CHANNEL_INFO_NAME_CH2 = "ch2";
    private static final String CHAINCODE_INFO_NAME_CH2 = "chaincode-org2";

    private final Genson genson = GensonUtils.create();

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
        System.out.println("[EC] initLedger");
        // ChaincodeStub stub = ctx.getStub();
    }

    /**
     * Create a {@link ElectionAsset}.
     * @param ctx the {@link Context}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void createElection(final Context ctx) {
        System.out.println("[EC] createElection");

        applyToTransients(ctx,
            t -> getStringFromTransient(t, ELECTION_ID.getKey()),
            t -> getMapOfResultsFromTransient(t, RESULTS.getKey()),
            (electionId, results) -> {
                ChaincodeStub stub = ctx.getStub();
                if (electionExists(ctx, electionId)) {
                    String errorMessage = String.format("ElectionAsset %s already exists", electionId);
                    System.err.println(errorMessage);
                    throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_ALREADY_EXISTS.toString());
                }
                ElectionInfo electionInfo = null;
                try {
                    electionInfo = readElectionInfo(ctx);
                } catch (NullPointerException e) {
                    throw new ChaincodeException(e.getMessage(), ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
                }
                try {
                    Election election = ElectionFactory
                        .buildElection(electionInfo, results);
                    ElectionAsset electionAsset = new ElectionAsset(electionId, election);
                    String sortedJson = genson.serialize(electionAsset.getAsset());
                    stub.putStringState(electionAsset.getElectionId(), sortedJson);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    throw new ChaincodeException(e.getMessage(), ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
                }
                return null;
        });
    }

    /**
     * Return the {@link ElectionAsset}.
     * @param ctx the {@link Context}.
     * @return the {@link ElectionAsset}.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public ElectionAsset readElectionAsset(final Context ctx) {
        System.out.println("[EC] readElectionAsset");
        return applyToTransients(ctx,
            t -> getStringFromTransient(t, ELECTION_ID.getKey()),
            (electionId) -> {
                if (electionExists(ctx, electionId)) {
                    ChaincodeStub stub = ctx.getStub();
                    String electionSerialized = stub.getStringState(electionId);
                    return new ElectionAsset(electionId, genson.deserialize(electionSerialized, Election.class));
                } else {
                    String errorMessage = String.format("ElectionAsset %s does not exist", electionId);
                    System.out.println(errorMessage);
                    throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
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
        System.out.println("[EC] readElectionInfo");

        Chaincode.Response response = ctx.getStub().invokeChaincodeWithStringArgs(
            CHAINCODE_INFO_NAME_CH1,
            List.of("ElectionInfoContract:readElectionInfo"),
            CHANNEL_INFO_NAME_CH1
        );
        // TODO debug here
        System.out.println("[EC] readElectionInfo response received: " + response.getStringPayload());
        return genson.deserialize(response.getStringPayload(), ElectionInfo.class);
    }

    /**
     * Cast a {@link Ballot} in an existing {@link Election}.
     * @param ctx the {@link Context}.
     * @return the {@link Ballot} cast.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Ballot castVote(final Context ctx) {
        System.out.println("[EC] castVote");

        return applyToTransients(ctx,
            t -> getChoiceFromTransient(t, CHOICE.getKey()),
            t -> getStringFromTransient(t, USER_ID.getKey()),
            t -> getStringFromTransient(t, ELECTION_ID.getKey()),
            t -> getLongFromTransient(t, CODE.getKey()),
            (choice, voterId, electionId, code) -> {
                ChaincodeStub stub = ctx.getStub();
                if (!electionExists(ctx, electionId)) {
                    String errorMessage = String.format("ElectionAsset %s does not exist", electionId);
                    System.out.println(errorMessage);
                    throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
                }

                Chaincode.Response responseIsValid = ctx.getStub().invokeChaincodeWithStringArgs(
                    CHAINCODE_INFO_NAME_CH2,
                    List.of("CodesManagerContract:isValid"),
                    CHANNEL_INFO_NAME_CH2
                );
                if (!Boolean.parseBoolean(responseIsValid.getStringPayload())) {
                    String errorMessage = "Code " + code + " is not valid.";
                    System.out.println(errorMessage);
                    throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
                }
                Ballot ballot = null;
                try {
                    ballot = new BallotImpl.Builder().electionID(electionId)
                        .voterID(voterId)
                        .date(LocalDateTime.now())
                        .choice(choice)
                        .build();
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    throw new ChaincodeException(e.getMessage(), ElectionTransferErrors.ELECTION_INVALID_ARGUMENT.toString());
                }
                ElectionInfo electionInfo = readElectionInfo(ctx);
                ElectionAsset election = readElectionAsset(ctx);
                try {
                    ElectionManagerImpl.getInstance().castVote(election.getAsset(), electionInfo, ballot);
                } catch (IllegalStateException | IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    throw new ChaincodeException(e.getMessage(), ElectionTransferErrors.ELECTION_INVALID_BALLOT_ARGUMENT.toString());
                }
                ctx.getStub().invokeChaincodeWithStringArgs(
                    CHAINCODE_INFO_NAME_CH2,
                    List.of("CodesManagerContract:invalidate"),
                    CHANNEL_INFO_NAME_CH2
                );
                String electionSerialized = genson.serialize(election.getAsset());
                stub.putStringState(election.getElectionId(), electionSerialized);
                return ballot;
            }
        );
    }

    /**
     * Delete an {@link ElectionAsset}.
     * @param ctx the {@link Context}.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteAsset(final Context ctx) {
        System.out.println("[EC] deleteAsset");

        applyToTransients(ctx,
            t -> getStringFromTransient(t, ELECTION_ID.getKey()),
            (electionId) -> {
                ChaincodeStub stub = ctx.getStub();

                if (!electionExists(ctx, electionId)) {
                    String errorMessage = String.format("Election %s does not exist", electionId);
                    System.out.println(errorMessage);
                    throw new ChaincodeException(errorMessage, ElectionTransferErrors.ELECTION_NOT_FOUND.toString());
                }
                stub.delState(electionId);

                return null;
            }
        );
    }

    /**
     * Check if an {@link ElectionAsset} exists.
     * @param ctx the {@link Context}.
     * @param electionID the {@link ElectionAsset}'s ID.
     * @return if the {@link ElectionAsset} exists.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    private boolean electionExists(final Context ctx, final String electionID) {
        System.out.println("[EC] electionExists");
        ChaincodeStub stub = ctx.getStub();
        String electionSerialized = stub.getStringState(electionID);
        return (electionSerialized != null && !electionSerialized.isEmpty());
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

        return genson.serialize(queryResults);
    }
}

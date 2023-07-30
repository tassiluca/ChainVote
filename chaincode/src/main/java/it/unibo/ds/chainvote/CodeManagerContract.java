package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.CodeManager;
import it.unibo.ds.core.codes.CodeManagerImpl;
import it.unibo.ds.core.codes.CodeRepository;
import it.unibo.ds.core.codes.OneTimeCode;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.Optional;
import java.util.Set;

/**
 * An Hyperledger Fabric chaincode entry point contract to manage one-time-codes.
 * TODO replace args with transient data
 */
@Contract(
    name = "CodeManagerContract",
    info = @Info(
        title = "Code Manager Contract",
        description = "Contract used to manage one-time-code"
    )
)
public final class CodeManagerContract implements ContractInterface, CodeRepository<Context> {

    private static final String CODE_COLLECTION_NAME = "CodesCollection";
    private final CodeManager<Context> codeManager = new CodeManagerImpl<>(this);
    private final Genson genson = GensonUtils.create();

    /**
     * Generate a new one-time-code.
     * @param context the transaction context
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @return the code asset.
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public OneTimeCodeAsset generateFor(final Context context, final Long votingId, final String userId) {
        return new OneTimeCodeAsset(codeManager.generateFor(context, votingId, userId));
    }

    @Override
    public Optional<OneTimeCode> get(final Context context, final Long votingId, final String userId) {
        // TODO implement
        return Optional.empty();
    }

    @Override
    public void put(final Context context, final Long votingId, final String userId, final OneTimeCode code) {
        // TODO replace with private data
        context.getStub().putStringState(
            new CompositeKey(String.valueOf(votingId), userId).getObjectType(),
            genson.serialize(new OneTimeCodeAsset(code))
        );
    }

    @Override
    public Set<OneTimeCode> getAllOf(final Context context, final Long votingId) {
        // TODO implement
        return Set.of();
    }
}

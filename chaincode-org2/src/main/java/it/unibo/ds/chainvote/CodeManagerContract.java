package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.CodeManager;
import it.unibo.ds.core.codes.CodeManagerImpl;
import it.unibo.ds.core.codes.CodeRepository;
import it.unibo.ds.core.codes.OneTimeCode;
import it.unibo.ds.core.codes.OneTimeCodeImpl;
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
        description = "Contract used to manage one-time-codes"
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

    /**
     * Check if the given code is still valid, i.e. has not been consumed yet for the given voting.
     * @param context the transaction context
     * @param votingId the voting identifier
     * @param otc the one-time-code to validate
     * @return rue if the given code is still valid, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isValid(final Context context, final Long votingId, final Long otc) {
        return false; // TODO
    }

    /**
     * Invalidate the given code for the given voting. After calling this method the code can no longer be used.
     * @param context the transaction context
     * @param votingId the voting identifier
     * @param otc the one-time-code to validate
     */
    @Transaction
    public void invalidate(final Context context, final Long votingId, final Long otc) {
        // TODO
    }

    /**
     * Verifies if the given code has been generated for the given user and voting.
     * @param context the transaction context
     * @param votingId the voting identifier
     * @param userId the user identifier
     * @param otc the one-time-code to validate
     * @return true if the given code is correct, false otherwise.
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean verifyCodeOwner(final Context context, final Long votingId, final String userId, final Long otc) {
        return codeManager.verifyCodeOwner(context, votingId, userId, new OneTimeCodeImpl(otc));
    }

    @Override
    public Optional<OneTimeCode> get(final Context context, final Long votingId, final String userId) {
        final OneTimeCodeAsset data = genson.deserialize(
            context.getStub().getPrivateData(
                CODE_COLLECTION_NAME,
                new CompositeKey(String.valueOf(votingId), userId).getObjectType()
            ),
            OneTimeCodeAsset.class
        );
        return Optional.ofNullable(data).map(OneTimeCodeAsset::getAsset);
    }

    @Override
    public void put(final Context context, final Long votingId, final String userId, final OneTimeCode code) {
        context.getStub().putPrivateData(
            CODE_COLLECTION_NAME,
            new CompositeKey(String.valueOf(votingId), userId).getObjectType(),
            genson.serialize(new OneTimeCodeAsset(code))
        );
    }

    @Override
    public void replace(final Context context, final Long votingId, final OneTimeCode code) {
        // TODO implement
    }

    @Override
    public Set<OneTimeCode> getAllOf(final Context context, final Long votingId) {
        // TODO implement
        return Set.of();
    }
}

package it.unibo.ds.chainvote;

import org.hyperledger.fabric.Logger;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;

/**
 * Sample contract used to test cross-contract and cross-ledger invocation
 */
@Contract(
    name = "TestChaincode",
    info = @Info(
        title = "Test Contract",
        description = "Contract used to test cross-contract and cross-ledger invocation"
    )
)
@Default
public final class TestChaincode implements ContractInterface {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean sampleFunction(final Context context) {
        System.out.println("[SAMPLE FUNCTION] called");
        logger.info("[SAMPLE FUNCTION] called");
        return true;
    }

}

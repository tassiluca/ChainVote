import org.hyperledger.fabric.contract.*;
import org.hyperledger.fabric.contract.annotation.*;

@Contract(
    name = "Sample Smart Contract",
    info = @Info(
        title = "A simple contract",
        description = "An implementation of a smart contract in Java"
    ),
    transactionSerializer = "it.unibo.ds.MyTransactionSerializer"
)
public class HFSmartContract implements ContractInterface {

    @Override
    public void beforeTransaction(Context ctx) {
        // Custom logic to be invoked before each transaction
        ContractInterface.super.beforeTransaction(ctx);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void myTransaction(Context ctx, String arg1, int arg2) {
        // Transaction logic
    }

    @Override
    public void afterTransaction(Context ctx, Object result) {
        // Custom logic to be invoked after each transaction
        ContractInterface.super.afterTransaction(ctx, result);
    }
}

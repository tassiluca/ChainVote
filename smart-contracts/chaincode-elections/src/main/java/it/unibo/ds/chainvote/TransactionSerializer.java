package it.unibo.ds.chainvote;

import org.hyperledger.fabric.contract.annotation.Serializer;

/**
 * The custom transactions serializer.
 */
@Serializer()
public class TransactionSerializer extends GensonTransactionsSerializer {

    /**
     * Creates the new transaction serializer.
     */
    public TransactionSerializer() {
        super(SerializersUtils.gensonInstance());
    }
}

package it.unibo.ds.chainvote;

import org.hyperledger.fabric.contract.annotation.Serializer;

@Serializer()
public class TransactionSerializer extends AbstractTransactionSerializer {

    public TransactionSerializer() {
        super(SerializersUtils.gensonInstance());
    }
}

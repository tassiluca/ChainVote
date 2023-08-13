package it.unibo.ds.chainvote;

import it.unibo.ds.chainvote.contract.CodeManagerContract;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class CodeManagerTest {

    private final CodeManagerContract contract = new CodeManagerContract();
    private Context context;
    private ChaincodeStub stub;

    @BeforeEach
    void setup() {
        context = mock(Context.class);
        stub = mock(ChaincodeStub.class);
        when(context.getStub()).thenReturn(stub);
    }

    @Nested
    class TestCodeGeneration {

        @Test
        void whenNotExists() {
            final Map<String, byte[]> transientData = new HashMap<>() {{
                put("userId", "mrossi".getBytes());
                put("electionId", "test-election".getBytes());
            }};
            when(context.getStub().getTransient()).thenReturn(transientData);
            // TODO
        }

        @Test
        void whenAlreadyExists() {

        }

    }
}

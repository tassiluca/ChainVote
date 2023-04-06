Context contextMock = mock(Context.class);
ChaincodeStub stubMock = mock(ChaincodeStub.class);
String MY_ELECTION_ID = "election_example"
ElectionInfo MyElectionInfo = ... // ElectionInfo creation

when(contextMock.getStub()).thenReturn(stubMock);
when(contextMock.getStub().invokeChaincodeWithStringArgs(
    CHAINCODE_INFO_NAME,
    List.of("ElectionInfoContract:readElectionInfo", MY_ELECTION_ID),
    CHANNEL_INFO_NAME
)).thenReturn(
    new Chaincode.Response(
        Chaincode.Response.Status.SUCCESS,
        "A payload example",
        genson.serialize(new Response<>(MyElectionInfo)).getBytes(UTF_8)
    )
);
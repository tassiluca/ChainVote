Context contextMockSpy = spy(new Context());
ChaincodeStub stub = spy(new ChaincodeStub());
String MY_ELECTION_ID = "election_example"
ElectionInfo MyElectionInfo = ... // ElectionInfo creation

doReturn(stub).when(contextMockSpy).getStub();
doReturn(new Chaincode.Response(
    Chaincode.Response.Status.SUCCESS,
    "A payload example",
    genson.serialize(new Response<>(MyElectionInfo)).getBytes(UTF_8))
).when(contextMockSpy).getStub().invokeChaincodeWithStringArgs(
    CHAINCODE_INFO_NAME,
    List.of("ElectionInfoContract:readElectionInfo", MY_ELECTION_ID),
    CHANNEL_INFO_NAME
);
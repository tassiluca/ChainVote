Response response = ctx.getStub().invokeChaincodeWithStringArgs(
    CHAINCODE_NAME,
    List.of("ContractName:methodName", arg1, arg2, ...),
    CHANNEL_NAME
);
if (response.getStatus().equals(Response.Status.SUCCESS)) {
    try {
        Response<MethodReturnType> payload = genson.deserialize(
            response.getStringPayload(),
            new GenericType<>() { }
        );
        ...
    } catch (JsonBindingException | NullPointerException e) {
        throw new ChaincodeException(
            e.getMessage(), 
            Error.CROSS_INVOCATION_ERROR.toString()
        );
    }
} else {
    var errorMessage = "Cross invocation failed.";
    throw new ChaincodeException(
        errorMessage, 
        Error.CROSS_INVOCATION_FAILED.toString()
    );
}
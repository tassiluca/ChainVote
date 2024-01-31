/* Note: a Context object is expected to be available in the scope of
 * this code (it is passed as an argument to the transaction functions). 
 */
final String CODES_COLLECTION = "CodesCollection";

// example of how to persist data in a private collection
context.getStub().getPrivateData(
    CODES_COLLECTION,
    new CompositeKey(electionId, userId).toString()
);

// example of how to retrieve data from a private collection
context.getStub().putPrivateData(
    CODES_COLLECTION,
    new CompositeKey(electionId, userId).toString(),
    genson.serialize(new OneTimeCodeAsset(electionId, userId, code))
);

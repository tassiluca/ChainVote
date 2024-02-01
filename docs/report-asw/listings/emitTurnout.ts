function emitTurnoutUpdate(io: Server, contract: Contract, electionId: string) {
    contract.evaluate('ElectionContract:readElection', {
        arguments: [electionId]
    }).then((result) => {
        const electionDetails = JSON.parse(utf8Decoder.decode(result));
        io.to("election-" + electionId).emit('updateTurnout', electionDetails.result.affluence);
    });
}
export async function getAllElection(req: Request, res: Response, next: NextFunction) {
    if(!ac.can(res.locals.user.role).readAny('election').granted) {
        next(new UnauthorizedError("Can't access to the resource"));
    }
    try {
        const gatewayOrg2: Gateway = await GrpcClientPool
            .getInstance()
            .getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg2.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const submission: Uint8Array = await contract
            .evaluate('ElectionContract:getAllElection', {
                arguments: []
            }
        );
        const result = utf8Decoder.decode(submission);
        res.locals.code = StatusCodes.OK;
        res.locals.data = JSON.parse(result).result;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next;
}

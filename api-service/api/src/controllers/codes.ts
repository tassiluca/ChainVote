import {NextFunction, Request, Response} from "express";
import {Contract, Gateway, Network} from "@hyperledger/fabric-gateway";
import GrpcClientPool from "../blockchain/grpc.client.pool";

import {Org2Peer} from "../blockchain/peer.enum";
import {StatusCodes} from "http-status-codes";

const channelName = "ch2";
const contractName = "chaincode-org2";
const utf8Decoder = new TextDecoder();

export async function generateFor(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const userId = req.body.userId;
        const codeRequest: Uint8Array = await contract.submit('CodesManagerContract:generateFor', {
            arguments: [`userId:${userId}`],
            transientData: {
                "userId": userId,
            }
        });

        const resultJson = utf8Decoder.decode(codeRequest);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        return next(error)
    }
}



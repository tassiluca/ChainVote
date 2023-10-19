import { Request, Response, NextFunction } from "express";
import {StatusCodes} from "http-status-codes";
import {Contract, Gateway, Network} from "@hyperledger/fabric-gateway";
import GrpcClientPool from "../blockchain/grpc.client.pool";
import {Org2Peer} from "../blockchain/peer.enum";

const channelName = "ch2";
const contractName = "chaincode-org2";
const utf8Decoder = new TextDecoder();
const utf8Encoder = new TextEncoder();

/**
 * Create a new election
 * @param req
 * @param res
 * @param next
 */
export async function createElection(req: Request, res: Response, next: NextFunction){
    try {
        const gatewayOrg2: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg2.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const electionId: Uint8Array = utf8Encoder.encode(req.body.electionId);
        const submission: Uint8Array = await contract.submit('createElectionInfo', {
            arguments: [`electionId:${electionId}`]
        });

        const resultJson = utf8Decoder.decode(submission);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        return next(error)
    }
}


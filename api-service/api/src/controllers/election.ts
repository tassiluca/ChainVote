import { Request, Response, NextFunction } from "express";
import {StatusCodes} from "http-status-codes";
import {Contract, Gateway, Network} from "@hyperledger/fabric-gateway";
import GrpcClientPool from "../blockchain/grpc.client.pool";
import {Org2Peer} from "../blockchain/peer.enum";

const channelName = "ch2";
const contractName = "chaincode-org2";
const utf8Decoder = new TextDecoder();

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

        const electionId: string = req.body.electionId
        const submission: Uint8Array = await contract.submit('createElectionInfo', {
            arguments: [`electionId:${electionId}`]
        });

        const resultJson = utf8Decoder.decode(submission);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        return next(error)
    }
}

/**
 * Get the information for a specific election
 * @param req
 * @param res
 * @param next
 */
export async function readElection(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg2: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg2.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const electionId: string = req.params.electionId;
        const submission: Uint8Array = await contract.evaluate('readElection', {
            arguments: [`electionId:${electionId}`]
        });

        const resultJson = utf8Decoder.decode(submission);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        return next(error)
    }
}

/**
 * Cast a vote for a specific election
 * @param req
 * @param res
 * @param next
 */
export async function castVote(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg2: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg2.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const electionId: string = req.params.electionId;
        const choice: string = req.body.choice;
        const userId: string = req.body.userId;
        const code: string = req.body.code;

        const submission: Uint8Array = await contract.submit('castVote', {
            arguments: [
                `electionId:${electionId}`,
                `choice:${choice}`
            ],
            transientData: {
                userId: userId,
                code: code
            }
        });

        const resultJson = utf8Decoder.decode(submission);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        return next(error)
    }
}
import {NextFunction, Request, Response} from "express";
import {Contract, Gateway, Network} from "@hyperledger/fabric-gateway";

import {StatusCodes} from "http-status-codes";
import GrpcClientPool from "../blockchain/grpc.client.pool";
import {Org1Peer} from "../blockchain/peer.enum";

const channelName = "ch1";
const contractName = "chaincode-org1";
const utf8Decoder = new TextDecoder();
const utf8Encoder = new TextEncoder();

/**
 * Get all the election data
 * @param req request object
 * @param res response object
 * @param next next function
 */
export async function getAllAssets(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const allAssets: Uint8Array = await contract.evaluate('ElectionInfoContract:getAllAssets');
        const resultJson = utf8Decoder.decode(allAssets);

        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        console.log(error.cause);
        return next(error)
    }
}

/**
 * Create the election data
 * @param req request object
 * @param res response object
 * @param next next function
 */
export async function createElectionInfo(req: Request, res: Response, next: NextFunction){

    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const startDate = {
            year: 2023,
            month: 8,
            day: 22,
            hour: 10,
            minute: 0,
            second: 0
        };

        const endDate = {
            year: 2023,
            month: 8,
            day: 23,
            hour: 10,
            minute: 0,
            second: 0
        }

        const choices =  {
            value: [
                {choice: "prova1"},
                {choice: "prova2"},
                {choice: "prova3"},
                {choice: "prova4"}
            ]
        }

        const data = [
            "goal:prova2",
            "voters:100",
            `startDate:${JSON.stringify(startDate)}`,
            `endDate: ${JSON.stringify(endDate)}`,
            `choices:${JSON.stringify(choices)}`
        ];

        const submission: Uint8Array = await contract.submit('createElectionInfo', {
            arguments: data
        });

        const resultJson = utf8Decoder.decode(submission);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));

    } catch (error) {
        console.log(error.cause);
        return next(error)
    }
}

/**
 * Read the election data with a specific id
 * @param req
 * @param res
 * @param next
 */
export async function readElectionInfo(req: Request, res: Response, next: NextFunction){
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const electionId: Uint8Array = utf8Encoder.encode(req.params.electionId);
        const submission: Uint8Array = await contract.evaluateTransaction('readElectionInfoSerialized', electionId);
        const resultJson = utf8Decoder.decode(submission);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));

    } catch (error) {
        console.log(error.cause);
        return next(error)
    }
}

/**
 * Delete the election data with a specific id
 * @param req
 * @param res
 * @param next
 */
export async function deleteAsset(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const assetId: Uint8Array = utf8Encoder.encode(req.params.electionId);
        await contract.submitTransaction('deleteAsset', assetId);
        return res.status(StatusCodes.OK).send({message: "Asset deleted successfully"});

    } catch (error) {
        console.log(error.cause);
        return next(error)
    }
}
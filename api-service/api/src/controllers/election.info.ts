import {NextFunction, Request, Response} from "express";
import {Contract, Gateway, Network} from "@hyperledger/fabric-gateway";

import {StatusCodes} from "http-status-codes";
import GrpcClientPool from "../blockchain/grpc.client.pool";
import {Org1Peer} from "../blockchain/peer.enum";
import transformHyperledgerError from "../blockchain/errors/error.handling";

const channelName = "ch1";
const contractName = "chaincode-org1";
const utf8Decoder = new TextDecoder();

type ChoiceRecord = {
    choice: string,
}

/**
 * Return all the generated data of election info.
 *
 * @param req request object
 * @param res response object
 * @param next next function
 */
export async function getAllElectionInfo(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const allAssets: Uint8Array = await contract.evaluate('ElectionInfoContract:getAllElectionInfo');
        const invocationResults = JSON.parse(utf8Decoder.decode(allAssets));
        res.locals.code = StatusCodes.OK;
        res.locals.data = invocationResults.result;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

/**
 * Convert an array of string representing the choices of an ElectionInfo to a ChoiceList object.
 * @param choices
 */
function convertToChoiceList(choices: string[] | undefined): ChoiceRecord[] {
    if (choices === undefined) {
        return [];
    }
    return choices.map((choice) => {
        return {
            choice: choice
        }
    });
}

/**
 * Create an election information.
 *
 * @param req request object
 * @param res response object
 * @param next next function
 */
export async function createElectionInfo(req: Request, res: Response, next: NextFunction){
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const goal: string = req.body.goal;
        const voters: string = req.body.voters;
        const startDate: string = req.body.startDate;
        const endDate: string = req.body.endDate;
        const choices: string= JSON.stringify(convertToChoiceList(req.body.choices));
        const data = [goal, voters, startDate, endDate, choices];
        const submission: Uint8Array = await contract.submit('ElectionInfoContract:createElectionInfo', {
            arguments: data
        });

        const resultJson = JSON.parse(utf8Decoder.decode(submission));
        return res.status(StatusCodes.OK).send(resultJson);
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
}

/**
 * Read an election data info with a specific id
 * @param req
 * @param res
 * @param next
 */
export async function readElectionInfo(req: Request, res: Response, next: NextFunction){
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const electionId: string = req.params.electionId
        const submission: Uint8Array = await contract.evaluateTransaction('ElectionInfoContract:readElectionInfo', electionId);
        const resultJson = utf8Decoder.decode(submission);
        res.locals.code = StatusCodes.OK;
        res.locals.data = resultJson;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

/**
 * Delete an election info with a specific id
 *
 * @param req
 * @param res
 * @param next
 */
export async function deleteElectionInfo(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const electionId: string = req.body.electionId;
        await contract.submitTransaction('ElectionInfoContract:deleteElectionInfo', electionId);
        return res.status(StatusCodes.OK).send({message: "Asset deleted successfully"});
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
}
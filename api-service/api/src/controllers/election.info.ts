import {NextFunction, Request, Response} from "express";
import {Contract, Gateway, Network} from "@hyperledger/fabric-gateway";

import {StatusCodes} from "http-status-codes";
import GrpcClientPool from "../blockchain/grpc.client.pool";
import {Org1Peer} from "../blockchain/peer.enum";
import transformHyperledgerError from "../blockchain/errors/error.handling";
import {convertToChoiceList} from "../blockchain/utils/utils";

import { ac } from "../configs/accesscontrol.config";
import {ErrorTypes, UnauthorizedError} from "core-components";
import {convertToISO, convertToUTC} from "../utils/date.utils";

const channelName = "ch1";
const contractName = "chaincode-elections";
const utf8Decoder = new TextDecoder();

/**
 * Return all the generated data of election info.
 *
 * @param req request object
 * @param res response object
 * @param next next function
 */
export async function getAllElectionInfo(req: Request, res: Response, next: NextFunction) {
    if (!ac.can(res.locals.user.role).readAny('electionInfo').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const allAssets: Uint8Array = await contract.evaluate('ElectionInfoContract:getAllElectionInfo');
        const invocationResults = JSON.parse(utf8Decoder.decode(allAssets));
        invocationResults.result.forEach((element: any) => {
            element.startDate = convertToISO(element.startDate);
            element.endDate = convertToISO(element.endDate);
        });
        res.locals.code = StatusCodes.OK;
        res.locals.data = invocationResults.result;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

/**
 * Read an election data info with a specific id
 * 
 * @param req
 * @param res
 * @param next
 */
export async function readElectionInfo(req: Request, res: Response, next: NextFunction){
    if (!ac.can(res.locals.user.role).readAny('electionInfo').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const invocationResult = await getElectionInfo(req.params.electionId);
        res.locals.code = StatusCodes.OK;
        res.locals.data = invocationResult;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

export async function getElectionInfo(electionId: string) {
    const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
    const network: Network = gatewayOrg1.getNetwork(channelName);
    const contract: Contract = network.getContract(contractName);
    const submission: Uint8Array = await contract.evaluateTransaction('ElectionInfoContract:readElectionInfo', electionId);
    const results = utf8Decoder.decode(submission);
    const invocationResult = JSON.parse(results).result;
    invocationResult.startDate = convertToISO(invocationResult.startDate);
    invocationResult.endDate = convertToISO(invocationResult.endDate);
    return invocationResult;
}

/**
 * Create an election info.
 *
 * @param req request object
 * @param res response object
 * @param next next function
 */
export async function createElectionInfo(req: Request, res: Response, next: NextFunction){
    if (!ac.can(res.locals.user.role).createAny('electionInfo').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const choices: string= JSON.stringify(convertToChoiceList(req.body.choices));
        const convertedStartDate = convertToUTC(req.body.startDate);
        const convertedEndDate = convertToUTC(req.body.endDate);

        const data = [
            req.body.goal,
            req.body.voters,
            convertedStartDate,
            convertedEndDate,
            choices
        ];
        const submission: Uint8Array = await contract.submit('ElectionInfoContract:createElectionInfo', {
            arguments: data
        });

        const resultJson = JSON.parse(utf8Decoder.decode(submission));
        res.locals.code = StatusCodes.CREATED;
        res.locals.data = resultJson.result;
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
    if (!ac.can(res.locals.user.role).deleteAny('electionInfo').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org1Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const electionId: string = req.body.electionId;
        await contract.submitTransaction('ElectionInfoContract:deleteElectionInfo', electionId);
        res.locals.code = StatusCodes.OK;
        res.locals.data = {message: "Asset deleted successfully"};
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

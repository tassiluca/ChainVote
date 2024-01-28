import { Request, Response, NextFunction } from "express";
import { StatusCodes } from "http-status-codes";
import { Contract, Gateway, Network } from "@hyperledger/fabric-gateway";
import GrpcClientPool from "../blockchain/grpc.client.pool";
import { Org2Peer } from "../blockchain/peer.enum";
import transformHyperledgerError from "../blockchain/errors/error.handling";
import {toChoice} from "../blockchain/utils/utils";
import {ac} from "../configs/accesscontrol.config";
import {ErrorTypes, InternalServerError, UnauthorizedError} from "core-components";
import {convertToISO} from "../utils/date.utils";
import { Server } from "socket.io";

const channelName = "ch2";
const contractName = "chaincode-votes";
const utf8Decoder = new TextDecoder();

/**
 * Return all the existing elections in the ledger.
 * @param req
 * @param res
 * @param next
 */
export async function getAllElection(req: Request, res: Response, next: NextFunction) {
    if (!ac.can(res.locals.user.role).readAny('election').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg2: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg2.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const submission: Uint8Array = await contract.evaluate('ElectionContract:getAllElection', {
            arguments: []
        });

        const invocationResult = JSON.parse(utf8Decoder.decode(submission));
        res.locals.code = StatusCodes.OK;
        invocationResult.result.forEach((element: any) => {
            element.startDate = convertToISO(element.startDate);
            element.endDate = convertToISO(element.endDate);
        });
        res.locals.data = invocationResult.result;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

/**
 * Get the information of a specific election.
 * @param req
 * @param res
 * @param next
 */
export async function readElection(req: Request, res: Response, next: NextFunction) {
    if (!ac.can(res.locals.user.role).readAny('election').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg2: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg2.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const electionId: string = req.params.electionId;
        const submission: Uint8Array = await contract.evaluate('ElectionContract:readElection', {
            arguments: [electionId]
        });
        const invocationResult = JSON.parse(utf8Decoder.decode(submission));

        invocationResult.result.startDate = convertToISO(invocationResult.result.startDate);
        invocationResult.result.endDate = convertToISO(invocationResult.result.endDate);

        res.locals.code = StatusCodes.OK;
        res.locals.data = invocationResult.result;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

/**
 * Create a new election from an election info passing an electionId.
 * @param req
 * @param res
 * @param next
 */
export async function createElection(req: Request, res: Response, next: NextFunction){
    if (!ac.can(res.locals.user.role).createAny('election').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg2: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg2.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const electionId: string = req.body.electionId;
        const submission: Uint8Array = await contract.submit('ElectionContract:createElection', {
            arguments: [electionId, '{}']
        });
        const invocationResult = JSON.parse(utf8Decoder.decode(submission));
        res.locals.code = StatusCodes.OK;
        res.locals.data = invocationResult.result;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}


/**
 * Cast a vote for a specific election.
 * @param req
 * @param res
 * @param next
 * @param io
 */
export async function castVote(req: Request, res: Response, next: NextFunction, io: Server) {
    if (!ac.can(res.locals.user.role).updateAny('election').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg2: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg2.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const electionId: string = req.params.electionId;
        const choice: string = req.body.choice;
        const userId: string = res.locals.user._id.toString();
        const code: string = req.body.code;

        const submission: Uint8Array = await contract.submit('ElectionContract:castVote', {
            arguments: [JSON.stringify(toChoice(choice)), electionId],
            transientData: {userId: userId, code: code}
        });
        const result = utf8Decoder.decode(submission);
        if (!JSON.parse(result).result) {
            return next(
                new InternalServerError(
                    "Error while casting vote",
                    undefined,
                    ErrorTypes.CHAINCODE_ERROR
                )
            );
        }
        res.locals.code = StatusCodes.OK;
        res.locals.data = JSON.parse(result).result;
        emitTurnoutUpdate(io, contract, electionId);
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

/**
 * Notifies the socket clients that the turnout has changed.
 * @param io the instance of the socket server
 * @param contract the smart contract instance used to retrieve the election details
 * @param electionId the id of the election that has been updated
 */
function emitTurnoutUpdate(io: Server, contract: Contract, electionId: string) {
    contract.evaluate('ElectionContract:readElection', {
        arguments: [electionId]
    }).then((result) => {
        const electionDetails = JSON.parse(utf8Decoder.decode(result));
        io.to("election-" + electionId).emit('updateTurnout', electionDetails.result.affluence);
    });
}

/**
 * Delete an election
 * @param req
 * @param res
 * @param next
 */
export async function deleteElection(req: Request, res: Response, next: NextFunction) {
    if (!ac.can(res.locals.user.role).deleteAny('election').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg2: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg2.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const electionId: string = req.body.electionId;
        const submission: Uint8Array = await contract.submit('ElectionContract:deleteElection', {
            arguments: [electionId]
        });

        const resultJson = utf8Decoder.decode(submission);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
}

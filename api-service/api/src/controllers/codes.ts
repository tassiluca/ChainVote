import {NextFunction, Request, Response} from "express";
import {Contract, Gateway, Network} from "@hyperledger/fabric-gateway";
import GrpcClientPool from "../blockchain/grpc.client.pool";

import seedrandom from 'seedrandom';

import {Org2Peer} from "../blockchain/peer.enum";
import {StatusCodes} from "http-status-codes";
import transformHyperledgerError from "../blockchain/errors/error.handling";
import {ac} from "../configs/accesscontrol.config";
import {UnauthorizedError} from "core-components";

const channelName = "ch2";
const contractName = "chaincode-org2";
const utf8Decoder = new TextDecoder();


/**
 * Check if a code is valid
 * @param req
 * @param res
 * @param next
 */
export async function isValid(req: Request, res: Response, next: NextFunction) {
    if(!ac.can(res.locals.user.role).readAny('code').granted) {
        next(new UnauthorizedError("Can't access to the resource"));
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const code = req.body.code;
        const userId = req.body.userId;
        const electionId = req.body.electionId;

        const codeRequest: Uint8Array = await contract.evaluate('CodesManagerContract:isValid', {
            arguments: [electionId],
            transientData: {
                "code": code,
                "userId": userId
            }
        });

        const result = utf8Decoder.decode(codeRequest);
        res.locals.code = StatusCodes.OK;
        res.locals.data = JSON.parse(result).result;

    } catch (error) {
        return next(transformHyperledgerError(error));
    }

    return next();
}

/**
 * Verify if a code belongs to the specified user
 * @param req
 * @param res
 * @param next
 */
export async function verifyCodeOwner(req: Request, res: Response, next: NextFunction) {
    if(!ac.can(res.locals.user.role).readAny('code').granted) {
        next(new UnauthorizedError("Can't access to the resource"));
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const code = req.body.code;
        const userId = req.body.userId;
        const electionId = req.body.electionId;

        const codeRequest: Uint8Array = await contract.evaluate('CodesManagerContract:verifyCodeOwner', {
            arguments: [electionId],
            transientData: {
                "code": code,
                "userId": userId
            }
        });
        const result = utf8Decoder.decode(codeRequest);
        res.locals.code = StatusCodes.OK;
        res.locals.data = JSON.parse(result).result;

    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

/**
 * Generate a code that can be used by a user for voting
 *
 * @param req
 * @param res
 * @param next
 */
export async function generateCodeFor(req: Request, res: Response, next: NextFunction) {
    if(!ac.can(res.locals.user.role).createAny('code').granted) {
        next(new UnauthorizedError("Can't access to the resource"));
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const electionId = req.body.electionId

        const randomNumber = Math.floor(Math.random() * 1_000_000_000);
        const seed: string = seedrandom(randomNumber+ Date.now()).int32().toString();
        const userId = req.body.userId;

        const codeRequest: Uint8Array = await contract.submit('CodesManagerContract:generateCodeFor', {
            arguments: [electionId, seed],
            transientData: { "userId": userId }
        });

        const result = utf8Decoder.decode(codeRequest);
        res.locals.code = StatusCodes.OK;
        res.locals.data = JSON.parse(result).result;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

/**
 * Invalidate a code
 * @param req
 * @param res
 * @param next
 */
export async function invalidate(req: Request, res: Response, next: NextFunction) {
    if(!ac.can(res.locals.user.role).updateAny('code').granted) {
        next(new UnauthorizedError("Can't access to the resource"));
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const code = req.body.code;
        const userId = req.body.userId;
        const electionId = req.body.electionId;

        const codeRequest: Uint8Array = await contract.submit('CodesManagerContract:invalidate', {
            arguments: [electionId],
            transientData: {
                "code": code,
                "userId": userId
            }
        });

        const result = utf8Decoder.decode(codeRequest);
        res.locals.code = StatusCodes.OK;
        res.locals.data = JSON.parse(result).result;
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}


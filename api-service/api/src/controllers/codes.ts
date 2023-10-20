import {NextFunction, Request, Response} from "express";
import {Contract, Gateway, Network} from "@hyperledger/fabric-gateway";
import GrpcClientPool from "../blockchain/grpc.client.pool";

import {Org2Peer} from "../blockchain/peer.enum";
import {StatusCodes} from "http-status-codes";

const channelName = "ch2";
const contractName = "chaincode-org2";
const utf8Decoder = new TextDecoder();

/**
 * Generate a code that can be used by an user for voting
 * @param req
 * @param res
 * @param next
 */
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

/**
 * Check if a code is valid
 * @param req
 * @param res
 * @param next
 */
export async function isValid(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const code = req.body.code;
        const userId = req.body.userId;

        const codeRequest: Uint8Array = await contract.evaluate('CodesManagerContract:isValid', {
            transientData: {
                "code": code,
                "userId": userId
            }
        });

        const resultJson = utf8Decoder.decode(codeRequest);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        return next(error)
    }
}

/**
 * Invalidate a code
 * @param req
 * @param res
 * @param next
 */
export async function invalidate(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const code = req.body.code;
        const userId = req.body.userId;

        const codeRequest: Uint8Array = await contract.submit('CodesManagerContract:invalidate', {
            transientData: {
                "code": code,
                "userId": userId
            }
        });

        const resultJson = utf8Decoder.decode(codeRequest);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        return next(error)
    }
}

/**
 * Verify if a code belongs to the specified user
 * @param req
 * @param res
 * @param next
 */
export async function verifyCodeOwner(req: Request, res: Response, next: NextFunction) {
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);

        const code = req.body.code;
        const userId = req.body.userId;

        const codeRequest: Uint8Array = await contract.evaluate('CodesManagerContract:verifyCodeOwner', {
            transientData: {
                "code": code,
                "userId": userId
            }
        });

        const resultJson = utf8Decoder.decode(codeRequest);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        return next(error)
    }
}
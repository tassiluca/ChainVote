import {NextFunction, Request, Response} from "express";
import {Contract, Gateway, Network} from "@hyperledger/fabric-gateway";
import GrpcClientPool from "../blockchain/grpc.client.pool";
import seedrandom from 'seedrandom';
import {Org2Peer} from "../blockchain/peer.enum";
import {StatusCodes} from "http-status-codes";
import transformHyperledgerError from "../blockchain/errors/error.handling";
import {ac} from "../configs/accesscontrol.config";
import {ErrorTypes, UnauthorizedError} from "core-components";
import mailer from "../configs/mailer.config";

const channelName = "ch2";
const contractName = "chaincode-votes";
const utf8Decoder = new TextDecoder();

/**
 * Check if a code is valid
 * @param req
 * @param res
 * @param next
 */
export async function isValid(req: Request, res: Response, next: NextFunction) {
    if (!ac.can(res.locals.user.role).readAny('code').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const code = req.body.code;
        const userId = res.locals.user._id.toString();
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
    if (!ac.can(res.locals.user.role).readAny('code').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const code = req.body.code;
        const userId = res.locals.user._id.toString();
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
 * @param req
 * @param res
 * @param next
 */
export async function generateCodeFor(req: Request, res: Response, next: NextFunction) {
    if (!ac.can(res.locals.user.role).createAny('code').granted) {
        next(
            new UnauthorizedError(
                "Can't access to the resource",
                undefined,
                ErrorTypes.AUTHENTICATION_ERROR
            )
        );
    }
    try {
        const gatewayOrg1: Gateway = await GrpcClientPool.getInstance().getClientForPeer(Org2Peer.PEER1);
        const network: Network = gatewayOrg1.getNetwork(channelName);
        const contract: Contract = network.getContract(contractName);
        const electionId = req.body.electionId
        const randomNumber = Math.floor(Math.random() * 1_000_000_000);
        const seed: string = seedrandom(randomNumber+ Date.now()).int32().toString();
        const userId = res.locals.user._id.toString();
        const codeRequest: Uint8Array = await contract.submit('CodesManagerContract:generateCodeFor', {
            arguments: [electionId],
            transientData: { "seed": seed, "userId": userId }
        });
        const result = utf8Decoder.decode(codeRequest);
        const code = JSON.parse(result).result;
        // Divide the code in two parts
        const firstPart = code.substring(0, code.length/2);
        const secondPart = code.substring(code.length/2, code.length);

        res.locals.code = StatusCodes.CREATED;
        res.locals.data = firstPart;


        const message = {
            from: 'ChainVote',
            to: res.locals.user.email,
            subject: 'The other part of your code is here',
            html: `
                Hello ${res.locals.user.firstName} ${res.locals.user.secondName} &#128075;,<br>
                This is the other part of your code: <b>${secondPart}</b>
            `
        };
        const info = await mailer.sendMail(message);
        res.locals.code = StatusCodes.CREATED;
        res.locals.data = {
            msg: "Email sent",
            info: info.messageId
        }
    } catch (error) {
        return next(transformHyperledgerError(error));
    }
    return next();
}

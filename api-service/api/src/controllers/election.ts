import { Request, Response, NextFunction } from "express";
// import { ac } from "../configs/accesscontrol.config";
// import {UnauthorizedError} from "core-components";
import {CommunicatorFactory} from "../blockchain/communicator/communicator.factory";
import * as grpc from "@grpc/grpc-js";
import {connect, Contract, Gateway, Identity, Network, Signer} from "@hyperledger/fabric-gateway";
import {TextDecoder, TextEncoder} from "util";
import {StatusCodes} from "http-status-codes";

let client: grpc.Client, identity: Identity, signer: Signer;
let gatewayOrg1: Gateway;
let contract: Contract, network: Network;


const CHANNEL1_NAME = "ch1";
const utf8Decoder = new TextDecoder();
const utf8Encoder = new TextEncoder();

const communicator = CommunicatorFactory.org1WithEndpoint(
    "peer1",
    "localhost:7051",
    "peer1-org1"
);

async function getGateway() : Promise<Gateway> {

    client= await communicator.createGrpcClient();
    identity = await communicator.createIdentity();
    signer = await communicator.createSigner();

    return connect({
        client,
        identity,
        signer,
        evaluateOptions: () => {
            return {deadline: Date.now() + 5000}; // 5 seconds
        },
        endorseOptions: () => {
            return {deadline: Date.now() + 15000}; // 15 seconds
        },
        submitOptions: () => {
            return {deadline: Date.now() + 5000}; // 5 seconds
        },
        commitStatusOptions: () => {
            return {deadline: Date.now() + 60000}; // 1 minute
        },
    });
}


/**
 * Get all the election data
 * @param req request object
 * @param res response object
 * @param next next function
 */
export async function getAllAssets(req: Request, res: Response, next: NextFunction) {
    try {
        gatewayOrg1 = gatewayOrg1 == undefined ? await getGateway() : gatewayOrg1;
        network = gatewayOrg1.getNetwork(CHANNEL1_NAME);
        contract = network.getContract("chaincode-org1");

        const allAssets: Uint8Array = await contract.evaluateTransaction('getAllAssets');
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
        gatewayOrg1 = gatewayOrg1 == undefined ? await getGateway() : gatewayOrg1;
        network = gatewayOrg1.getNetwork(CHANNEL1_NAME);
        contract = network.getContract("chaincode-org1");

        const startDate = {
            year: 2023,
            month: 8,
            day: 20,
            hour: 10,
            minute: 0,
            second: 0
        };

        const endDate = {
            year: 2023,
            month: 8,
            day: 21,
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
        gatewayOrg1 = gatewayOrg1 == undefined ? await getGateway() : gatewayOrg1;
        network = gatewayOrg1.getNetwork(CHANNEL1_NAME);
        contract = network.getContract("chaincode-org1");

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
        gatewayOrg1 = gatewayOrg1 == undefined ? await getGateway() : gatewayOrg1;
        network = gatewayOrg1.getNetwork(CHANNEL1_NAME);
        contract = network.getContract("chaincode-org1");

        const assetId: Uint8Array = utf8Encoder.encode(req.params.electionId);
        await contract.submitTransaction('deleteAsset', assetId);
        return res.status(StatusCodes.OK).send({message: "Asset deleted successfully"});

    } catch (error) {
        console.log(error.cause);
        return next(error)
    }
}

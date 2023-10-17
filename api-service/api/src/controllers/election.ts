import { Request, Response, NextFunction } from "express";
// import { ac } from "../configs/accesscontrol.config";
// import {UnauthorizedError} from "core-components";
import {CommunicatorFactory} from "../blockchain/communicator/communicator.factory";
import * as grpc from "@grpc/grpc-js";
import {connect, Contract, Gateway, Identity, Network, Signer} from "@hyperledger/fabric-gateway";
import {TextDecoder} from "util";
import {StatusCodes} from "http-status-codes";

let client: grpc.Client, identity: Identity, signer: Signer;
let gatewayOrg1: Gateway;
let contract: Contract, network: Network;


const CHANNEL1_NAME = "ch1";
const utf8Decoder = new TextDecoder();
const communicator = CommunicatorFactory.org1WithEndpoint(
    "peer1",
    "peer1-org1:7051",
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
export async function getElectionData(req: Request, res: Response, next: NextFunction) {
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
export async function createElectionData(req: Request, res: Response, next: NextFunction){

    try {
        gatewayOrg1 = gatewayOrg1 == undefined ? await getGateway() : gatewayOrg1;
        network = gatewayOrg1.getNetwork(CHANNEL1_NAME);
        contract = network.getContract("chaincode-org1");

        const data = {
            goal: Buffer.from("Test"),
            voters: Buffer.from("100"),
            startDate: Buffer.from(JSON.stringify({
                year: "2023",
                month: "2",
                day: "20",
                hour: "10",
                minute: "0",
                second: "0",
            })),
            endDate: Buffer.from(JSON.stringify({
                year: "2023",
                month: "2",
                day: "21",
                hour: "0",
                minute: "0",
                second: "0",
            })),
            list: Buffer.from(JSON.stringify({value: [
                {choice: "prova1"},
                {choice: "prova2"},
                {choice: "prova3"},
            ]}))
        }

        const submission: Uint8Array = await contract.submit('createElectionInfo', {
            transientData: data
        });

        const resultJson = utf8Decoder.decode(submission);
        return res.status(StatusCodes.OK).send(JSON.parse(resultJson));

    } catch (error) {
        console.log(error.cause);
        return next(error)
    }

}
import { Request, Response, NextFunction } from "express";
// import { ac } from "../configs/accesscontrol.config";
// import {UnauthorizedError} from "core-components";
import {CommunicatorFactory} from "../blockchain/communicator/communicator.factory";
import * as grpc from "@grpc/grpc-js";
import {connect, Contract, Gateway, Identity, Network, Signer} from "@hyperledger/fabric-gateway";
import {TextDecoder} from "util";
import {StatusCodes} from "http-status-codes";

let client: grpc.Client, identity: Identity, signer: Signer;
let contract: Contract, network: Network;

let gatewayOrg1:Gateway;
const CHANNEL1_NAME = "ch1";

(async () => {
    const communicator = CommunicatorFactory.org1WithEndpoint(
        "peer2",
        "localhost:8051",
        "peer2-org1"
    );

    client= await communicator.createGrpcClient();
    identity = await communicator.createIdentity();
    signer = await communicator.createSigner();

    gatewayOrg1 = connect({
        client,
        identity,
        signer,
        evaluateOptions: () => {
            return { deadline: Date.now() + 5000 }; // 5 seconds
        },
        endorseOptions: () => {
            return { deadline: Date.now() + 15000 }; // 15 seconds
        },
        submitOptions: () => {
            return { deadline: Date.now() + 5000 }; // 5 seconds
        },
        commitStatusOptions: () => {
            return { deadline: Date.now() + 60000 }; // 1 minute
        },
    });

    network = gatewayOrg1.getNetwork(CHANNEL1_NAME);
    contract = network.getContract("ElectionInfoContract");
})();

const utf8Decoder = new TextDecoder();


export async function createElectionData(req: Request, res: Response, next: NextFunction) {
    /*const grantAccess = ac.can(res.locals.user.role).readAny('users').granted;
    if(grantAccess) {
        throw new UnauthorizedError("Can't create the resource");
    }*/
    try {

        /*const allAssets: Uint8Array = await contract.evaluateTransaction('getAllAssets');
        const resultJson = utf8Decoder.decode(allAssets);
        res.status(StatusCodes.OK).send(JSON.parse(resultJson));*/

        const submission: Uint8Array = await contract.submit('createElectionInfo', {
            transientData: {
                goal: "Test",
                voters: "100",
                startDate: JSON.stringify({
                    year: 2023,
                    month: 1,
                    day: 20,
                    hour: 10,
                    minute: 0,
                    second: 0,
                }),
                endDate: JSON.stringify({
                    year: 2023,
                    month: 1,
                    day: 21,
                    hour: 0,
                    minute: 0,
                    second: 0,
                }),
                list: JSON.stringify([
                    {choice: "prova1"},
                    {choice: "prova2"},
                    {choice: "prova3"},
                ]),
            },
        });

        const resultJson = utf8Decoder.decode(submission);
        res.status(StatusCodes.OK).send(JSON.parse(resultJson));
    } catch (error) {
        console.log(error.cause);
        next(error)
    }

}
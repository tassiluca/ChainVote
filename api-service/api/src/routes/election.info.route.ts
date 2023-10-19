import { Router } from "express";
import {
    createElectionInfo,
    deleteAsset,
    getAllAssets,
    readElectionInfo
} from "../controllers/election.info";


const electionInfoRouter = Router();

/**
 * Get all the election data
 */
electionInfoRouter.get("/data", getAllAssets);

/**
 * Return a specific election data
 */
electionInfoRouter.get("/data/:electionId", readElectionInfo);

/**
 * Create a new election data
 */
electionInfoRouter.post("/data", createElectionInfo);

/**
 * Delete a specific election data
 */
electionInfoRouter.delete("/data/:electionId", deleteAsset);

/**
 * Check if a specific election data exists
 */
// electionRouter.get("/data/:electionId/exists", electionInfoExists);

export default electionInfoRouter;
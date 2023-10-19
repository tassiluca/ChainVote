import { Router } from "express";
import {
    createElectionInfo,
    deleteAsset,
    getAllAssets,
    readElectionInfo
} from "../controllers/election.info";


const electionRouter = Router();

/**
 * Get all the election data
 */
electionRouter.get("/data", getAllAssets);

/**
 * Return a specific election data
 */
electionRouter.get("/data/:electionId", readElectionInfo);

/**
 * Create a new election data
 */
electionRouter.post("/data", createElectionInfo);

/**
 * Delete a specific election data
 */
electionRouter.delete("/data/:electionId", deleteAsset);

/**
 * Check if a specific election data exists
 */
// electionRouter.get("/data/:electionId/exists", electionInfoExists);

export default electionRouter;
import { Router } from "express";
import {
    createElectionInfo,
    deleteAsset,
    getAllElectionInfo,
    readElectionInfo
} from "../controllers/election.info";


const electionInfoRouter = Router();

/**
 * Get all the election data
 */
electionInfoRouter.get("/all", getAllElectionInfo);

/**
 * Return a specific election data
 */
electionInfoRouter.get("/:electionId", readElectionInfo);

/**
 * Create a new election data
 */
electionInfoRouter.post("/", createElectionInfo);

/**
 * Delete a specific election data
 */
electionInfoRouter.delete("/:electionId", deleteAsset);

/**
 * Check if a specific election data exists
 */
// electionRouter.get("/data/:electionId/exists", electionInfoExists);

export default electionInfoRouter;
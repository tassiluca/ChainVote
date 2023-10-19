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
electionRouter.get("/", getAllAssets);

/**
 * Return a specific election data
 */
electionRouter.get("/:electionId", readElectionInfo);

/**
 * Create a new election data
 */
electionRouter.post("/", createElectionInfo);

/**
 * Delete a specific election data
 */
electionRouter.delete("/:electionId", deleteAsset);

export default electionRouter;
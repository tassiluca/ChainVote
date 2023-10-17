import { Router } from "express";
import {createElectionInfo, getAllAssets, readElectionInfo} from "../controllers/election";


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
export default electionRouter;
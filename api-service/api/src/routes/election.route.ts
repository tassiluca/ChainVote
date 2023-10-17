import { Router } from "express";
import {createElectionData, getElectionData} from "../controllers/election";


const electionRouter = Router();

/**
 * Get the election data
 */
electionRouter.get("/data", getElectionData);

/**
 * Create a new election data
 */
electionRouter.post("/data", createElectionData);

export default electionRouter;
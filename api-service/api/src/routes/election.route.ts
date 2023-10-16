import { Router } from "express";
import { getElectionData } from "../controllers/election";


const electionRouter = Router();

/**
 * Get the election data
 */
electionRouter.get("/data", getElectionData);

export default electionRouter;
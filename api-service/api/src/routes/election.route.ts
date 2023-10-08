import { Router } from "express";
import { createElectionData } from "../controllers/election";


const electionRouter = Router();

/**
 * Add new election data.
 */
electionRouter.post("/data", createElectionData);


export default electionRouter;
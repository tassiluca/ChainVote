import { Router } from "express";
import {castVote, createElection, readElection} from "../controllers/election";

const electionRouter = Router();

electionRouter.get("/:electionId", readElection);

electionRouter.post("/", createElection);

electionRouter.post("/:electionId/vote", castVote);


export default electionRouter;
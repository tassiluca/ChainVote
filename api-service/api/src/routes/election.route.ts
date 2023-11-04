import { Router } from "express";
import {castVote, createElection, deleteElection, getAllElection, readElection} from "../controllers/election";

const electionRouter = Router();

electionRouter.get("/all", getAllElection);

electionRouter.get("/detail/:electionId", readElection);

electionRouter.post("/", createElection);

electionRouter.put("/:electionId/vote", castVote);

electionRouter.delete("/",  deleteElection);

export default electionRouter;
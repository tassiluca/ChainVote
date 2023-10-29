import { Router } from "express";
import {castVote, createElection, deleteAsset, getAllElection, readElection} from "../controllers/election";

const electionRouter = Router();

electionRouter.get("/all", getAllElection);

electionRouter.get("/:electionId", readElection);

electionRouter.post("/", createElection);

electionRouter.post("/:electionId/vote", castVote);

electionRouter.delete("/:electionId",  deleteAsset);

export default electionRouter;
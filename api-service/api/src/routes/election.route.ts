import { Router } from "express";
import {castVote, createElection, deleteAsset, getAllAssets, readElection} from "../controllers/election";

const electionRouter = Router();

electionRouter.get("/", getAllAssets);

electionRouter.get("/:electionId", readElection);

electionRouter.post("/", createElection);

electionRouter.post("/:electionId/vote", castVote);

electionRouter.delete("/:electionId",  deleteAsset);

export default electionRouter;
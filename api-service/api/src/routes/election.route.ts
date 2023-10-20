import { Router } from "express";
import {createElection, readElection} from "../controllers/election";

const electionRouter = Router();

electionRouter.post("/", createElection);

electionRouter.get("/:electionId", readElection);

export default electionRouter;
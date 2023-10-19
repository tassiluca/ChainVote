import { Router } from "express";
import { createElection } from "../controllers/election";

const electionRouter = Router();

electionRouter.post("/", createElection);

export default electionRouter;
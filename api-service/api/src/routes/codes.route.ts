import { Router } from "express";
import {generateFor, invalidate, isValid, verifyCodeOwner} from "../controllers/codes";

const codesRoute =  Router();

codesRoute.post("/generate/:electionId", generateFor);


codesRoute.post("/isValid", isValid);


codesRoute.put("/invalidate", invalidate);


codesRoute.post("/verifyOwner", verifyCodeOwner);

export default codesRoute;
import { Router } from "express";
import {generateCodeFor, invalidate, isValid, verifyCodeOwner} from "../controllers/codes";

const codesRoute =  Router();

codesRoute.post("/generate", generateCodeFor);


codesRoute.post("/is-valid", isValid);


codesRoute.patch("/invalidate", invalidate);


codesRoute.post("/verify-owner", verifyCodeOwner);

export default codesRoute;
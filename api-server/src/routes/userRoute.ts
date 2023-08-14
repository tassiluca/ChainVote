import { Router } from "express";
import { createUser } from "../controllers/users";


const userRouter = Router();

userRouter.post("/", createUser);

// router.get("/:id", getUser);

export default userRouter;
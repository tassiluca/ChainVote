import { Router } from "express";
import { createUser, getProfile, editProfile, deleteProfile } from "../controllers/users";
import { authenticationHandler } from "../middleware/authentication.middleware";

const userRouter = Router();

/**
 * Retrieve the informations of a user 
 */
userRouter.get("/:email", authenticationHandler, getProfile);

userRouter.get("/", authenticationHandler, getProfile);

/** 
 * Update the informations of a user 
 */
userRouter.put("/:email", authenticationHandler, editProfile);

userRouter.put("/", authenticationHandler, editProfile);

/**
 * Delete a user from the system
 */
userRouter.delete("/:email", authenticationHandler, deleteProfile);

userRouter.delete("/", authenticationHandler, deleteProfile);

/**
 * Create a new user.
 */
userRouter.post("/", createUser);

export default userRouter;
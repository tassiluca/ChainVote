import { Request, Response, NextFunction } from "express";

/**
 * Create a new election
 * @param req
 * @param res
 * @param next
 */
export async function createElection(req: Request, res: Response, next: NextFunction){
    try {
        return res.status(200).send("createElection");
    } catch (error) {
        console.log(error.cause);
        return next(error)
    }
}
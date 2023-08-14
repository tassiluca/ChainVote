
import { Request, Response, NextFunction } from "express";
import { HttpBaseError } from "./errors";

export default async function defaultErrorHandler(
    err: Error, 
    req: Request, 
    res: Response, 
    next: NextFunction
) {
    if(err instanceof HttpBaseError) {
        res.status(err.code);
        res.send({
            code: err.code,
            name: err.name,
            message: err.message
        });
    } else {
        res.status(500);
        res.send("Error received" +  err.toString());
    }
}


import { Request, Response, NextFunction } from "express";
import { HttpBaseError } from "../errors/errors";
import {StatusCodes} from "http-status-codes";

export async function defaultErrorHandler(
    err: Error, 
    req: Request, 
    res: Response, 
    next: NextFunction
) {
    if(err instanceof HttpBaseError) {
        res.status(err.code);
        return res.send({
            code: err.code,
            name: err.name,
            message: err.message
        });
    } else {
        res.status(StatusCodes.INTERNAL_SERVER_ERROR);
        return res.send("Error received" +  err.toString());
    }
}

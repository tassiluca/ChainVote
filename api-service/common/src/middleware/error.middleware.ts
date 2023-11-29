
import { Request, Response, NextFunction } from "express";
import { HttpBaseError } from "../errors/errors";
import {StatusCodes} from "http-status-codes";
import {ErrorResponse} from "../utils/response/response";

export async function defaultErrorHandler(
    err: Error, 
    req: Request, 
    res: Response, 
    next: NextFunction
) {
    const response: ErrorResponse = {
        success: false,
        date: new Date(),
        code: StatusCodes.INTERNAL_SERVER_ERROR,
        error: {
            name: err.name,
            message: err.message
        }
    }
    if(err instanceof HttpBaseError) {
        response.code = err.code;
        response.error.name = err.name;
        response.error.message = err.message;
    }
    res.status(response.code);
    return res.send(response);
}


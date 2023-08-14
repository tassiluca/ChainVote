
import { ErrorRequestHandler, Request, Response, NextFunction } from "express";

export async function defaultHandler(
    err: ErrorRequestHandler, 
    req: Request, 
    res: Response, 
    next: NextFunction
) {
    res.status(500);
    res.send("Error received" +  err.toString());
}

import { Request, Response, NextFunction } from "express";
import {Response as RespObj} from "../utils/response/response";
import {StatusCodes} from "http-status-codes";
import {BadRequestError, InternalServerError, NotFoundError} from "../errors/errors";

export async function defaultResponseHandler(
    req: Request,
    res: Response,
    next: NextFunction
) {
    const data = res.locals.data;
    const code = res.locals.code;
    if (data == undefined || code == undefined) {
        return next(new BadRequestError("The route doesn't exist"));
    }
    const response: RespObj = {
        success: true,
        date: new Date(),
        data: data,
        code: code
    }
    res.status(response.code);
    return res.send(response);
}

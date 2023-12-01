
import { StatusCodes } from "http-status-codes"
import {ErrorTypes} from "./error.types";

export class HttpBaseError implements Error {
    code: StatusCodes | number;
    name: string;
    message: string;
    stack?: string | undefined;
    type: number;
    
    constructor(
        code: StatusCodes | number, 
        name: string,
        message?: string,
        stack?: string | undefined,
        type?: ErrorTypes
    ) {
        this.code = code;
        this.name = name;
        this.message = message? message : code.toString();
        if(this.stack) {
            this.stack = stack;
        }
        this.type = type ? type : ErrorTypes.GENERIC_ERROR;
    }
}


export class BadRequestError extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined, type?: ErrorTypes) {
        super(
            StatusCodes.BAD_REQUEST,
            "Bad Request",
            message,
            stack,
            type
        )
    } 
}

export class InternalServerError extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined, type?: ErrorTypes) {
        super(
            StatusCodes.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            message,
            stack,
            type
        )
    } 
}

export class NotFoundError extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined, type?: ErrorTypes) {
        super(
            StatusCodes.NOT_FOUND,
            "Not Found",
            message,
            stack,
            type
        )
    } 
}

export class UnauthorizedError extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined, type?: ErrorTypes) {
        super(
            StatusCodes.UNAUTHORIZED,
            "Unauthorized",
            message,
            stack,
            type
        )
    } 
}

export class TooManyRequests extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined, type?: ErrorTypes) {
        super(
            StatusCodes.TOO_MANY_REQUESTS,
            "Too Many Requests",
            message,
            stack,
            type
        )
    }
}
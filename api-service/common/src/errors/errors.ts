
import { StatusCodes } from "http-status-codes"

export class HttpBaseError implements Error {
    code: StatusCodes | number;
    name: string;
    message: string;
    stack?: string | undefined;
    
    constructor(
        code: StatusCodes | number, 
        name: string,
        message?: string,
        stack?: string | undefined
    ) {
        this.code = code;
        this.name = name;

        if(message) {
            this.message = message;
        } else {
            this.message = code.toString();
        }
        
        if(this.stack) {
            this.stack = stack;
        }
    }
}


export class BadRequestError extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined) {
        super(
            StatusCodes.BAD_REQUEST,
            "Bad Request",
            message,
            stack
        )
    } 
}

export class InternalServerError extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined) {
        super(
            StatusCodes.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            message,
            stack
        )
    } 
}

export class NotFoundError extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined) {
        super(
            StatusCodes.NOT_FOUND,
            "Not Found",
            message,
            stack
        )
    } 
}

export class UnauthorizedError extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined) {
        super(
            StatusCodes.UNAUTHORIZED,
            "Unauthorized",
            message,
            stack
        )
    } 
}


export class TooManyRequests extends HttpBaseError {
    constructor(message?: string, stack?: string | undefined) {
        super(
            StatusCodes.TOO_MANY_REQUESTS,
            "Too Many Requests",
            message,
            stack
        )
    }
}
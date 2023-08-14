
import { StatusCodes } from "http-status-codes"

export class HttpBaseError implements Error {
    code: StatusCodes | number;
    name: string;
    message: string;
    stack?: string | undefined;

    constructor(
        code: StatusCodes | number, 
        name: string,
        message: string,
        stack?: string | undefined
    ) {
        this.code = code;
        this.name = name;
        this.message = message;
        
        if(this.stack) {
            this.stack = stack;
        }
    }
}

function errorBuilder(
    statusCode:StatusCodes | number,
    name: string,
    message?: string,
    stack?: string | undefined
    ): HttpBaseError {
    
    let _message = "Error";
    if(message) {
        _message = message;
    }

    return new HttpBaseError(statusCode, name, _message, stack);
}

export function InternalServerError(message: string, stack?: string | undefined): HttpBaseError {
    return errorBuilder(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "Internal Server Error",
        message,
        stack
    );
}


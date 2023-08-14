
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
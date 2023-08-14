
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




import {StatusCodes} from "http-status-codes";

export interface DefaultResponse {
    success: boolean;
    date: Date;
    code: StatusCodes;
}

export interface Response extends DefaultResponse {
    data: any;
}

export interface ErrorResponse extends DefaultResponse {
    error: {
        name: string;
        message: string;
    }
}
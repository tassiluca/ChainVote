import {BadRequestError} from "core-components";


/**
 * Transform an error from the http module to an HTTP-based error
 * @param error the error to transform from the blockchain
 */
export default function transformHyperledgerError(error: Error) {
    console.log(error);
    const message = error.message.split(": ")[1];
    return new BadRequestError(message);
}
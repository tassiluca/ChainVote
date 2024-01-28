import {BadRequestError, ErrorTypes} from "core-components";

/**
 * Transform an error from the http module to an HTTP-based error
 * @param error the error to transform from the blockchain
 */
export default function transformHyperledgerError(error: Error) {
    let message;
    if ("details" in error) {
        // @ts-ignore
        message = error.details[0].message;
    } else {
        message = error.message.split(": ")[1];
    }
    return new BadRequestError(message, error.stack, ErrorTypes.CHAINCODE_ERROR);
}

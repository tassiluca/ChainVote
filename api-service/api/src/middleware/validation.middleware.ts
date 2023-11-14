import {Request, Response, NextFunction} from 'express';
import { validationResult, ValidationChain } from 'express-validator';
import {BadRequestError} from "core-components";


/**
 * Middleware to handle validation errors
 * @param validations
 */
export function validationHandler(validations: ValidationChain[]) {
    return async (req: Request, res: Response, next: NextFunction) => {
        for (let validation of validations) {
            const result = await validation.run(req);
            if (result.array().length) break;
        }

        const errors = validationResult(req);
        console.log(errors);
        if (errors.isEmpty()) {
            return next();
        }

        return next(new BadRequestError(`Some validation errors occurred on the request.`));
    };
}
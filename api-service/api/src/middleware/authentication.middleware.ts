import { BadRequestError, NotFoundError} from "core-components";
import {Request, Response, NextFunction} from "express";
import { User } from "core-components";
import { Jwt } from "core-components";

export async function authenticationHandler (req: Request, res: Response, next: NextFunction) {
    const authorizationHeader = req.headers["authorization"];
    if(authorizationHeader == undefined) {
        return next(new BadRequestError("Authorization header not found in the request"));
    }
    
    const accessToken = authorizationHeader.split(" ")[1];
    try {

        const jwtRecord = await Jwt.findOne({accessToken: accessToken});
        if(jwtRecord == null) {
            return next(new NotFoundError("The submitted jwt token doesn't exists"));
        }
        const validationResponse:any = await jwtRecord.validateAccessToken();
        const user = await User.findOne({email: validationResponse.sub.email});
        if(user == null) {
            return next(new NotFoundError("User not found"));
        }
        res.locals.user= user;

    } catch(error) {
        return next(error);
    }
    
    next();
}


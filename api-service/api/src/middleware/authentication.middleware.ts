import { BadRequestError, NotFoundError, UnauthorizedError, verifyAccessToken } from "core-components";
import {Request, Response, NextFunction} from "express";
import { User } from "core-components";


export async function authenticationHandler (req: Request, res: Response, next: NextFunction) {
    const email = req.body.email;
    const authorizationHeader = req.headers["authorization"];
    if(authorizationHeader == undefined || authorizationHeader == null) {
        return next(new BadRequestError("Authorization header not found in the request"));
    }
    
    const accessToken = authorizationHeader.split(" ")[1];
    try {
        const decodedToken: any = verifyAccessToken(accessToken);
        const subscriber = decodedToken.sub;
        if(subscriber.email != email) {
            return next(new UnauthorizedError("Invalid access token"));
        }
        
        
        const user = await User.findOne({email: email});
        if(user == null) {
            return next(new NotFoundError("User not found"));
        }
        res.locals.user= user;

    } catch(error) {
        return next(error);
    }

    next();
}


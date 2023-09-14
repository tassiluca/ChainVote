import { Request, Response, NextFunction } from "express";
import { User, UnauthorizedError, BadRequestError, NotFoundError } from "core-components"
import { Jwt } from "core-components";

export async function login(req: Request, res: Response, next: NextFunction) {
    const email = req.body.email;
    const password = req.body.password;
    const responseError = new UnauthorizedError("Login error");

    if(!(email)) {
        return next(new BadRequestError("The request should contains the \"email\" field"));
    }

    try {
        const user = await User.findOne({email: email});
        if (user === null || user === undefined) {
            return next(responseError); 
        }
        
        user.comparePassword(password, async function(error, isMatch) {
            if (error || !isMatch) {
                return next(responseError);
            }

            const tokens = await Jwt.createTokenPair(user);
            return res.status(200).send({
                message: "Login successfull",
                email: email,
                accessToken: tokens.accessToken,
                refreshToken: tokens.refreshToken
            });
        });
       
    } catch(error) {
        next(error);      
    }
}

export async function refreshToken(req: Request, res: Response, next: NextFunction) {
    const email = req.body.email;
    const refreshToken = req.body.refreshToken;

    if(!(email)) {
        return next(new BadRequestError("The request should contains the \"email\" field"));
    }

    if(!(refreshToken)) {
        return next(new BadRequestError("The request should contains the \"refreshToken\" field"));
    }

    const user = await User.findOne({email: email});
    if(user === null || user === undefined) {
        return next(new NotFoundError("The specified email doesn't belong to any users"));
    }

    const tokenRecord = await Jwt.findOne({refreshToken: refreshToken});
    if(tokenRecord) {
        try {
            await tokenRecord.validateRefreshToken(email);
        } catch (error) {
            return next(error);
        }

        try {
            const newTokens = await Jwt.createTokenPair(user);
            return res.status(200).send({
                message: "Access token refreshed, the new one is attached to this response",
                email: email,
                accessToken: newTokens.accessToken,
                refreshToken: newTokens.refreshToken
            });
        } catch (error) {
            return next(Error("Error while creating a new token pair"));
        }
    } else {
        return next(new NotFoundError("Can't find the requested token"));
    }
}
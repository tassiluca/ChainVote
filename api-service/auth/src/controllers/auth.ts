import { Request, Response, NextFunction } from "express";
import {User, UnauthorizedError, NotFoundError} from "core-components"
import { Jwt } from "core-components";
import {StatusCodes} from "http-status-codes";

/**
 * Login a user and return a token pair
 *
 * @param req
 * @param res
 * @param next
 */
export async function login(req: Request, res: Response, next: NextFunction) {
    const email = req.body.email;
    const password = req.body.password;

    try {
        const user = await User.findOne({email: email});
        const responseError = new UnauthorizedError("Login error");
        if (user === null || user === undefined) {
            return next(responseError); 
        }

        user.comparePassword(password, async function(error, isMatch) {
            if (error || !isMatch) {
                return next(responseError);
            }

            const tokens = await Jwt.createTokenPair(user);
            res.locals.code = StatusCodes.OK;
            res.locals.data = {
                accessToken: tokens.accessToken,
                refreshToken: tokens.refreshToken
            };
            return next();
        });

    } catch(error) {
        return next(error);
    }
}

/**
 * Logout an user
 * 
 * @param req 
 * @param res 
 * @param next 
 */
export async function logout(req: Request, res: Response, next: NextFunction) {
    const authorizationHeader = req.headers["authorization"] as string;
    const accessToken = authorizationHeader.split(" ")[1];
    try {
        await Jwt.findOneAndUpdate(
            {accessToken: accessToken},
            { $set: { enabled: false } },
            { new: true, useFindAndModify: false },
        );

        res.locals.code = StatusCodes.OK;
        res.locals.data = {
            message: "User logged out"
        }
    } catch (error) {
        return next(error);
    }

    return next();
}

/**
 * Refresh the access token.
 * If the refresh token is valid, then a new token pair is generated and returned.
 *
 * @param req
 * @param res
 * @param next
 */
export async function refreshToken(req: Request, res: Response, next: NextFunction) {

    const email = req.body.email;
    const refreshToken = req.body.refreshToken;

    const user = await User.findOne({email: email});
    if(user === null || user === undefined) {
        return next(new NotFoundError("The specified email doesn't belong to any users"));
    }

    const tokenRecord = await Jwt.findOne({refreshToken: refreshToken});
    if(tokenRecord) {
        try {
            const tokenResponse: any = await tokenRecord.validateRefreshToken();
            if(tokenResponse.sub.email != email) {
                return next(new UnauthorizedError("The submitted token doesn't belong to the specified user"));
            }
            const newTokens = await Jwt.createTokenPair(user);

            res.locals.code = StatusCodes.OK;
            res.locals.data = {
                accessToken: newTokens.accessToken,
                refreshToken: newTokens.refreshToken
            };

        } catch (error) {
            return next(error);
        }
    } else {
        return next(new NotFoundError("Can't find the requested token"));
    }

    return next();
}



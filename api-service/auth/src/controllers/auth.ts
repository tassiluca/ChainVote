import { Request, Response, NextFunction } from "express";
import { User, UnauthorizedError, BadRequestError, NotFoundError, InternalServerError } from "core-components"
import { signRefreshToken, signAccessToken, verifyRefreshToken } from "core-components";


export async function login(req: Request, res: Response, next: NextFunction) {
    const email = req.body.email;
    const password = req.body.password;
    const responseError = new UnauthorizedError("Login error"); 

    try {
        const user = await User.findOne({email: email});
        if (user === null || user === undefined) {
            return next(responseError); 
        }
        
        user.comparePassword(password, function(error, isMatch) {
            if (error || !isMatch) {
                return next(responseError);
            }

            const accessToken = signAccessToken({sub: user}, "1h");
            const refreshToken = signRefreshToken({sub: user}, "2h");

            return res.status(200).send({
                message: "Login successfull",
                email: email,
                accessToken: accessToken,
                refreshToken: refreshToken
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

    try {
        const decodedToken: any = verifyRefreshToken(refreshToken);
        if(decodedToken) {
            const decodedUser = decodedToken.sub;
            if(decodedUser.email != email) {
                return next(new UnauthorizedError("Invalid token"));
            }
            const accessToken = signAccessToken({sub: user});
            const refhreshToken = signRefreshToken({sub: user});
            return res.status(200).send({
                message: "Access token refreshed, the new one is attached to this response",
                email: email,
                accessToken: accessToken,
                refreshToken: refhreshToken
            });

        } else {
            return next(new InternalServerError("Error during the validation of the token"));
        }
    } catch(error) {
        next(error);
    }
}
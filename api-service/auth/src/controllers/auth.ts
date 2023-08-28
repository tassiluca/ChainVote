import { Request, Response, NextFunction } from "express";
import { User, UnauthorizedError } from "core-components"
import { signRefreshToken, signAccessToken } from "../jwt/jwt.auth";


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

            const accessToken = signAccessToken({sub: user.email});
            const refreshToken = signRefreshToken({sub: user.email});

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
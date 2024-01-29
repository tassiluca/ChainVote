import { Request, Response, NextFunction } from "express";
import { StatusCodes } from "http-status-codes";
import {BadRequestError, ErrorTypes, NotFoundError, UnauthorizedError, User} from "core-components";
import { ac } from "../configs/accesscontrol.config";
import mailer from "../configs/mailer.config";

/**
 * Set the user identity to work with. This function should be used when, in an API control method, an admin entity
 * wants to access to the data of another user. In this case, the request should specify the email of the user
 * to control.
 *
 * @param req
 * @param res
 * @param next
 * @param isAllowed
 */
async function setWorkData(req: Request, res: Response, next:NextFunction, isAllowed:boolean) {
    let user = res.locals.user;
    const email = req.body.email;
    if (email && email !== user.email) {
        try {
            if (!isAllowed) {
                next(
                    new UnauthorizedError(
                        "Can't access to the resource",
                        undefined,
                        ErrorTypes.AUTHENTICATION_ERROR
                    )
                );
            }
            user = await User.findOne({email: email});
            if (user == undefined) {
                next(new NotFoundError(
                    "Can't find the user",
                    undefined,
                    ErrorTypes.AUTHENTICATION_ERROR
                ));
            }
        } catch(error) {
            throw error; 
        }
    }
    return user;
}

/**
 * Create a new user
 *
 * @param req
 * @param res
 * @param next
 */
export async function createUser(req: Request, res: Response, next: NextFunction) {
    const user = new User({
        email: req.body.email,
        password: req.body.password,
        firstName: req.body.firstName,
        secondName: req.body.secondName,
        role: req.body.role
    });
    try {
        await user.save();
        res.locals.code = StatusCodes.CREATED;
        res.locals.data = {
            email: user.email,
            firstName: user.firstName,
            secondName: user.secondName
        };
    } catch(error) {
        return next(
            new BadRequestError(
                error.message,
                undefined,
                ErrorTypes.REGISTER_ERROR
            )
        );
    }
    return next();
}

/**
 * Get the profile of the user
 *
 * @param req
 * @param res
 * @param next
 */
export async function getProfile(req: Request, res: Response, next: NextFunction) {
    let user;
    const isAllowed = ac.can(res.locals.user.role).readAny('users').granted;
    try {
        user = await setWorkData(req, res, next, isAllowed);
        res.locals.code = StatusCodes.OK;
        res.locals.data = {
            email: user.email,
            firstName: user.firstName,
            secondName: user.secondName,
            role: user.role
        };
    } catch (error) {
        return next(error);
    }
    return next();
}

/**
 * Edit the profile of the user
 *
 * @param req
 * @param res
 * @param next
 */
export async function editProfile(req: Request, res: Response, next: NextFunction) {
    let user;
    const isAllowed = ac.can(res.locals.user.role).updateAny('users').granted;
    try {
        user = await setWorkData(req, res, next, isAllowed);
    } catch (error) {
        return next(error);
    }
    const data = req.body.data;
    if (data === null || data === undefined) {
        return next(
            new BadRequestError(
                "Request need to have a \"data\" field",
                undefined,
                ErrorTypes.VALIDATION_ERROR
            )
        );
    }

    try {
        await User.updateOne({ email: user.email }, data);
        res.locals.code = StatusCodes.OK;
        res.locals.data = true;
    } catch(error) {
        return next(error);
    }
    return next();
}

/**
 * Delete the profile of the user
 *
 * @param req
 * @param res
 * @param next
 */
export async function deleteProfile(req: Request, res: Response, next: NextFunction) {
    let user;
    const isAllowed = ac.can(res.locals.user.role).deleteAny('users').granted;
    try {
        user = await setWorkData(req, res, next, isAllowed);
        await User.deleteOne({ email: user.email });
        res.locals.code = StatusCodes.OK;
        res.locals.data = true;
    } catch (error) {
        return next(error);
    }
    return next();
}

function generateRandomPassword(regexPattern: RegExp, length: number): string {
    const validChars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*';
    let password = '';
    for (let i = 0; i < length; i++) {
        let randomChar = validChars.charAt(Math.floor(Math.random() * validChars.length));
        password += randomChar;
    }
    return password;
}

function getRandomNumber(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

// TODO check if works
/**
 * Send user a new password via mail
 *
 * @param req
 * @param res
 * @param next
 */
export async function passwordForgotten(req: Request, res: Response, next: NextFunction) {
    const regexPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])/; // Example pattern: Alphanumeric characters only
    const passwordLength = getRandomNumber(10, 16);
    let password = '';
    while (!regexPattern.test(password)) {
        password = generateRandomPassword(regexPattern, passwordLength);
    }
    const data = {'password': password}
    try {
        await User.updateOne({ email: req.body.email }, data);
        res.locals.code = StatusCodes.OK;
        res.locals.data = true;
    } catch(error) {
        return next(error);
    }
    const message = {
        from: 'ChainVote',
        to: req.body.email,
        subject: 'Your new password has been created.',
        html: `
                Hello ${res.locals.user.firstName} ${res.locals.user.secondName},<br>
                This is the new password: <b>${password}</b>
            `
    };
    mailer.sendMail(message).then((info) => {
        res.locals.code = 201
        res.locals.data = {
            msg: "Email sent",
            info: info.messageId
        }
    }).catch((err) => next(err));
    return next();
}

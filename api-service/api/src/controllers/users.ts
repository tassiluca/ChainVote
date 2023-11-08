import { Request, Response, NextFunction } from "express";
import { StatusCodes } from "http-status-codes";
import { BadRequestError, NotFoundError, UnauthorizedError, User } from "core-components";
import { ac } from "../configs/accesscontrol.config";

async function setWorkData(req: Request, res: Response, next:NextFunction, isAllowed:boolean) {
    let user = res.locals.user;
    const email = req.params.email;
    
    if(email && email !== user.email) {
        try {
            if(!isAllowed) {
                next(new UnauthorizedError("Can't access to the resource"));
            }
            user = await User.findOne({email: email});
            if(user == undefined) {
                next(new NotFoundError("Can't find the user"));
            }
        } catch(error) {
            throw error; 
        }
    }
    
    return user;
}

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
        return next(error);
    }

    return next();
}

export async function getProfile(req: Request, res: Response, next: NextFunction) {
    let user;
    const isAllowed = ac.can(res.locals.user.role).readAny('users').granted;
    try {
        user = await setWorkData(req, res, next, isAllowed);
        res.locals.code = StatusCodes.OK;
        res.locals.data = {
                email: user.email,
                firstName: user.firstName,
                secondName: user.secondName
        };
    } catch (error) {
        return next(error);
    }
    return next();
}

export async function editProfile(req: Request, res: Response, next: NextFunction) {
    let user;
    const isAllowed = ac.can(res.locals.user.role).updateAny('users').granted;
    try {
        user = await setWorkData(req, res, next, isAllowed);
    } catch (error) {
        return next(error);
    }

    const data = req.body.data;
    if(data === null || data === undefined) {
        return next(new BadRequestError("Request need to have a \"data\" field"));
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
